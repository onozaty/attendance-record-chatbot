package com.github.onozaty.attendance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;
import com.github.onozaty.attendance.domain.entity.AttendanceEntity.AttendanceType;
import com.github.onozaty.attendance.domain.repository.AttendanceRepository;
import com.github.onozaty.attendance.domain.service.DayAttendance;
import com.github.onozaty.attendance.domain.service.UserAttendance;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Sql(statements = "TRUNCATE TABLE attendances")
class AttendanceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    void getDayAttendances() {

        attendanceRepository.save(
                AttendanceEntity.builder()
                        .userName("user1")
                        .type(AttendanceType.COME)
                        .date(LocalDate.of(2020, 4, 1))
                        .time(LocalTime.of(9, 30, 0))
                        .build());

        attendanceRepository.save(
                AttendanceEntity.builder()
                        .userName("user1")
                        .type(AttendanceType.LEAVE)
                        .date(LocalDate.of(2020, 4, 1))
                        .time(LocalTime.of(18, 30, 0))
                        .build());

        attendanceRepository.save(
                AttendanceEntity.builder()
                        .userName("user1")
                        .type(AttendanceType.COME)
                        .date(LocalDate.of(2020, 4, 12))
                        .time(LocalTime.of(10, 5, 10))
                        .build());

        attendanceRepository.save(
                AttendanceEntity.builder()
                        .userName("user1")
                        .type(AttendanceType.LEAVE)
                        .date(LocalDate.of(2020, 4, 12))
                        .time(LocalTime.of(19, 31, 1))
                        .build());

        attendanceRepository.save(
                AttendanceEntity.builder()
                        .userName("user2")
                        .type(AttendanceType.COME)
                        .date(LocalDate.of(2020, 4, 12))
                        .time(LocalTime.of(11, 0, 30))
                        .build());

        ResponseEntity<List<DayAttendance>> response = restTemplate.exchange(
                "/api/attendances?month=2020-04",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DayAttendance>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .containsExactly(
                        new DayAttendance(
                                LocalDate.of(2020, 4, 1),
                                Arrays.asList(
                                        new UserAttendance(
                                                "user1",
                                                LocalTime.of(9, 30, 0),
                                                LocalTime.of(18, 30, 0)))),
                        new DayAttendance(LocalDate.of(2020, 4, 2), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 3), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 4), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 5), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 6), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 7), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 8), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 9), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 10), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 11), Collections.emptyList()),
                        new DayAttendance(
                                LocalDate.of(2020, 4, 12),
                                Arrays.asList(
                                        new UserAttendance(
                                                "user1",
                                                LocalTime.of(10, 5, 10),
                                                LocalTime.of(19, 31, 1)),
                                        new UserAttendance(
                                                "user2",
                                                LocalTime.of(11, 0, 30),
                                                null))),
                        new DayAttendance(LocalDate.of(2020, 4, 13), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 14), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 15), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 16), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 17), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 18), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 19), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 20), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 21), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 22), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 23), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 24), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 25), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 26), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 27), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 28), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 29), Collections.emptyList()),
                        new DayAttendance(LocalDate.of(2020, 4, 30), Collections.emptyList()));
    }
}
