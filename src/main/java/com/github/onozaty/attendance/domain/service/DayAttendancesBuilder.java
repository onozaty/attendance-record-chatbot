package com.github.onozaty.attendance.domain.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;
import com.github.onozaty.attendance.domain.entity.AttendanceEntity.AttendanceType;
import com.github.onozaty.attendance.domain.repository.AttendanceRepository;
import com.github.onozaty.attendance.domain.service.DayAttendance.UserAttendance;
import com.github.onozaty.attendance.domain.service.DayAttendance.UserAttendance.UserAttendanceBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DayAttendancesBuilder {

    private final AttendanceRepository attendanceRepository;

    public List<DayAttendance> build(YearMonth month) {

        LocalDate fromDate = month.atDay(1);
        LocalDate toDate = month.atEndOfMonth();

        List<AttendanceEntity> attendanceEntities = attendanceRepository.findByDateRange(fromDate, toDate);

        Map<LocalDate, List<AttendanceEntity>> attendancesDayMap = attendanceEntities.stream()
                .collect(Collectors.groupingBy(AttendanceEntity::getDate));

        List<DayAttendance> dayAttendances = new ArrayList<>();

        LocalDate currentDate = fromDate;
        while (currentDate.isBefore(toDate) || currentDate.isEqual(toDate)) {

            dayAttendances.add(
                    createDayAttendance(
                            currentDate,
                            attendancesDayMap.getOrDefault(currentDate, Collections.emptyList())));

            currentDate = currentDate.plusDays(1);
        }

        return dayAttendances;
    }

    private DayAttendance createDayAttendance(LocalDate date, List<AttendanceEntity> attendanceEntities) {

        List<UserAttendance> userAttendances = attendanceEntities.stream()
                .collect(Collectors.groupingBy(AttendanceEntity::getUserName))
                .entrySet()
                .stream()
                .map(x -> createUserAttendance(x.getKey(), x.getValue()))
                .sorted(Comparator.comparing(UserAttendance::getUserName))
                .collect(Collectors.toList());

        return DayAttendance.builder()
                .day(date)
                .users(userAttendances)
                .build();
    }

    private UserAttendance createUserAttendance(String userName, List<AttendanceEntity> attendanceEntities) {

        UserAttendanceBuilder builder = UserAttendance.builder()
                .userName(userName);

        attendanceEntities.stream()
                .filter(x -> x.getType() == AttendanceType.COME)
                .sorted(Comparator.comparing(AttendanceEntity::getTime).reversed())
                .findFirst()
                .map(AttendanceEntity::getTime)
                .ifPresent(builder::comeTime);

        attendanceEntities.stream()
                .filter(x -> x.getType() == AttendanceType.LEAVE)
                .sorted(Comparator.comparing(AttendanceEntity::getTime).reversed())
                .findFirst()
                .map(AttendanceEntity::getTime)
                .ifPresent(builder::leaveTime);

        return builder.build();
    }
}
