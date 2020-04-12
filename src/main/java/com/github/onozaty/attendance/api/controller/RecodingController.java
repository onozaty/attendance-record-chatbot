package com.github.onozaty.attendance.api.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.onozaty.attendance.domain.service.AttendanceService;
import com.github.onozaty.attendance.domain.service.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecodingController {

    private final AttendanceService service;

    @PostMapping("/api/recoding")
    public Response recoding(@RequestBody Message message) {

        Optional<String> responseMessage = service.recoding(message);

        if (responseMessage.isPresent()) {
            return new Response(responseMessage.get());
        }

        return null;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Response {
        private String text;
    }
}
