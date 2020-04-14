package com.github.onozaty.attendance.domain.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DayAttendance {

    private LocalDate day;

    private List<UserAttendance> users;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserAttendance {

        private String userName;

        private LocalTime comeTime;

        private LocalTime leaveTime;
    }
}
