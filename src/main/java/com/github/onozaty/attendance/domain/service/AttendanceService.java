package com.github.onozaty.attendance.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;
import com.github.onozaty.attendance.domain.entity.AttendanceEntity.AttendanceType;
import com.github.onozaty.attendance.domain.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final AttendanceAggregater attendanceAggregater;

    public AttendanceEntity record(String userName, AttendanceType attendanceType, LocalDateTime dateTime) {

        return attendanceRepository.save(
                AttendanceEntity.builder()
                        .userName(userName)
                        .type(attendanceType)
                        .date(dateTime.toLocalDate())
                        .time(dateTime.toLocalTime())
                        .build());
    }

    public List<DayAttendance> getDayAttendances(YearMonth month) {

        return attendanceAggregater.aggregate(month);
    }

    public UserAttendance getUserAttendance(String userName, LocalDate date) {

        return attendanceAggregater.aggregate(userName, date);
    }
}
