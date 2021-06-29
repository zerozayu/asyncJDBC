package com.example.asyncjdbc.controller;

import com.alibaba.fastjson.JSON;
import com.example.asyncjdbc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author zhangyu
 * @created 2021-06-04 10:14
 */

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @ResponseBody
    @PostMapping("/upload")
    public Mono<String> upload(@RequestPart("file") FilePart filePart, ServerHttpResponse response) {
        try {
            // log.info("-start:" + System.currentTimeMillis());
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            Mono<Map<String, Object>> dataBufferMono = this.employeeService.saveAndResponse(filePart);
            // log.info("-end:" + System.currentTimeMillis());

            // Flux<String> p = dataBufferMono.flatMapMany((buffer) -> {
            //     BufferedReader reader = new BufferedReader(new InputStreamReader(buffer, StandardCharsets.UTF_8));
            //     String s = null;
            //     List<String> strings = new ArrayList<>();
            //     try {
            //         while ((s = reader.readLine()) != null) {
            //             strings.add(s);
            //         }
            //     } catch (IOException e) {
            //         e.printStackTrace();
            //     }
            //     return Flux.fromStream(strings.stream());
            // });
            Mono<String> p = dataBufferMono.flatMap(map -> {
                String s = JSON.toJSONString(map);
                return Mono.just(s);
            });
            return p;
        }catch (Exception e) {
            log.error("error:", e);
            return Mono.error(e);
        }
    }
}
