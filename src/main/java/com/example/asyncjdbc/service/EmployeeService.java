package com.example.asyncjdbc.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author zhangyu
 * @created 2021-06-04 10:26
 */
public interface EmployeeService {
    Mono<Map<String, Object>> saveAndResponse(FilePart filePart);
}
