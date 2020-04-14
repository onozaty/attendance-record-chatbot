package com.github.onozaty.attendance.domain.service;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAttendance {

    private String userName;

    private LocalTime comeTime;

    private LocalTime leaveTime;
}