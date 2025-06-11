package com.morning.taskapimain.controller.ai;

import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.service.ai.GigaChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/gigachat")
@RequiredArgsConstructor
public class GigaChatTestController {

    private final GigaChatService gigaChatService;

    @PostMapping
    public Mono<Void> generate(@RequestBody TaskDTO taskDTO,
                               @RequestParam(name = "count") int count,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return gigaChatService.splitAndCreateSubtasks(taskDTO, count, token);
    }
}
