package com.github.onozaty.attendance.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.onozaty.attendance.domain.service.AttendanceService;
import com.github.onozaty.attendance.domain.service.Message;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    @PostMapping("/api/attendance/recoding")
    public RecordingResponse recoding(@RequestBody Message message) {

        return service.recoding(message)
                .map(RecordingResponse::new)
                .orElse(null);
    }
}
