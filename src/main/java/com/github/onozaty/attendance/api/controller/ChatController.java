package com.github.onozaty.attendance.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.onozaty.attendance.domain.service.ChatService;
import com.github.onozaty.attendance.domain.service.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService service;

    @PostMapping("/api/chat/handle")
    public ResponseMessage handleMessage(@RequestBody Message message) {

        return service.handleMessage(message)
                .map(ResponseMessage::new)
                .orElse(null);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ResponseMessage {
        private String text;
    }
}
