package com.github.onozaty.attendance.api.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;
import com.github.onozaty.attendance.domain.service.AttendanceService;
import com.github.onozaty.attendance.domain.service.DayAttendance;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    @GetMapping("/api/attendances")
    public List<DayAttendance> getDayAttendances(@RequestParam("month") YearMonth month) {

        return service.getDayAttendances(month);
    }

    @PostMapping("/api/attendances")
    public AttendanceEntity record(@RequestBody AttendanceRecordRequest request) {

        return service.record(request.getUserName(), request.getType(), LocalDateTime.now());
    }
}
