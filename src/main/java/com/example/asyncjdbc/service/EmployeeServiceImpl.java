package com.example.asyncjdbc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.asyncjdbc.db.DatabaseInstance;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangyu
 * @created 2021-06-04 10:26
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final DatabaseInstance instance;

    public EmployeeServiceImpl(DatabaseInstance instance) {
        this.instance = instance;
    }

    // private final Scheduler jdbcScheduler;
    //
    // private final Scheduler customScheduler = Schedulers.newParallel("customScheduler");
    //
    //
    //
    // public EmployeeServiceImpl(@Qualifier("jdbcScheduler") Scheduler jdbcScheduler, DatabaseInstance instance) {
    //     this.jdbcScheduler = jdbcScheduler;
    //     this.instance = instance;
    // }

    @Override
    public Mono<Map<String, Object>> saveAndResponse(FilePart filePart) {

        return filePart.content().flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            String content = new String(bytes, StandardCharsets.UTF_8);
            return Mono.just(content);
        })
                .map(this::processAndGetLinesAsList)
                .flatMapIterable(Function.identity())
                .collectList()
                .delayElement(Duration.of(500, ChronoUnit.MILLIS))
                .map((strings) -> {
                    String s = "";
                    for (String s1 : strings) {
                        s += s1;
                    }
                    Map<String, Object> dataMap = JSONObject.parseObject(s, Map.class);

                    dataMap.put("id", new Random().nextInt());
                    return dataMap;
                })
                // .publishOn(jdbcScheduler)
                .flatMap(items -> {
                    // log.info("flatMap线程： " + Thread.currentThread().getName());
                    Mono<Integer> integerMono = this.saveFile(items);
                    return integerMono.map(integer -> {
                        if (integer == -2) {
                            items.put("count", 1);
                        }
                        return items;
                    });
                })
                .doOnError(throwable -> {
                    //todo
                });
                // .publishOn(customScheduler);
                // .flatMap(this::getDefaultDataBufferMono)
                // .subscribeOn(customScheduler);
    }


    private Mono<FileInputStream> getDefaultDataBufferMono(Map<String, Object> emp) {

        // log.info("getDefaultDataBufferMono开始:" + System.currentTimeMillis());
        // 临时目录或者是固定的文件夹
        File f = new File("1.json");

        try {
            FileWriter writer = new FileWriter(f);
            JSON.writeJSONString(writer, emp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DefaultDataBuffer defaultDataBuffer = new DefaultDataBufferFactory().allocateBuffer(256);
        try {
            return Mono.just(new FileInputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private Mono<Integer> saveFile(Map<String, Object> dataMap) {
        // log.info("数据库操作开始:" + System.currentTimeMillis());
        try {

            String createSql =
                    "INSERT INTO EMPLOYEE (ID, NAME, AGE) VALUES (?, ?, ?)";
            Flowable<Integer> counts = instance.getDB().update(createSql)
                    .parameters(dataMap.get("id"),
                            dataMap.get("name"),
                            dataMap.get("age"))
                    .counts();

            return Mono.from(counts);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

    }

    private List<String> processAndGetLinesAsList(String string) {

        Supplier<Stream<String>> streamSupplier = string::lines;

        List<String> collect = streamSupplier.get().filter(s -> !s.isBlank()).collect(Collectors.toList());

        return collect;
    }

}
