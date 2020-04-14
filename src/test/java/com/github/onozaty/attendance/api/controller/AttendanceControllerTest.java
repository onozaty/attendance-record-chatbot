package com.github.onozaty.attendance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
import com.github.onozaty.attendance.domain.service.Message;

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
    void recoding_出勤() {

        Message message = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T12:23:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 出勤")
                .build();

        ResponseEntity<RecordingResponse> response = restTemplate.postForEntity(
                "/api/attendance/recoding",
                message,
                RecordingResponse.class);

        assertThat(response)
                .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
                .returns(new RecordingResponse("@user1 おはようございます。"), ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .hasSize(1)
                .first()
                .returns(message.getUserName(), AttendanceEntity::getUserName)
                .returns(LocalDate.of(2020, 4, 12), AttendanceEntity::getDate)
                .returns(LocalTime.of(12, 23, 1), AttendanceEntity::getTime)
                .returns(AttendanceType.COME, AttendanceEntity::getType);
    }

    @Test
    void recoding_退勤() {

        Message message = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T12:23:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 退勤")
                .build();

        ResponseEntity<RecordingResponse> response = restTemplate.postForEntity(
                "/api/attendance/recoding",
                message,
                RecordingResponse.class);

        assertThat(response)
                .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
                .returns(new RecordingResponse("@user1 お疲れ様でした。(勤務時間:  - 12:23)"), ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .hasSize(1)
                .first()
                .returns(message.getUserName(), AttendanceEntity::getUserName)
                .returns(LocalDate.of(2020, 4, 12), AttendanceEntity::getDate)
                .returns(LocalTime.of(12, 23, 1), AttendanceEntity::getTime)
                .returns(AttendanceType.LEAVE, AttendanceEntity::getType);
    }

    @Test
    void recoding_出勤_退勤() {

        Message comeMessage = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T09:15:15")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 出勤")
                .build();

        Message leaveMessage = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T18:30:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 退勤")
                .build();

        restTemplate.postForEntity(
                "/api/attendance/recoding",
                comeMessage,
                RecordingResponse.class);

        ResponseEntity<RecordingResponse> response = restTemplate.postForEntity(
                "/api/attendance/recoding",
                leaveMessage,
                RecordingResponse.class);

        assertThat(response).returns(HttpStatus.OK, ResponseEntity::getStatusCode)
                .returns(new RecordingResponse("@user1 お疲れ様でした。(勤務時間: 09:15 - 18:30)"),
                        ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .hasSize(2);
    }

    @Test
    void recoding_退勤_出勤が前日() {

        Message comeMessage = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-11T09:15:15")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 出勤")
                .build();

        Message leaveMessage = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T18:30:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 退勤")
                .build();

        restTemplate.postForEntity(
                "/api/attendance/recoding",
                comeMessage,
                RecordingResponse.class);

        ResponseEntity<RecordingResponse> response = restTemplate.postForEntity(
                "/api/attendance/recoding",
                leaveMessage,
                RecordingResponse.class);

        // 出勤が前日なので、出勤時間なし
        assertThat(response).returns(HttpStatus.OK, ResponseEntity::getStatusCode)
                .returns(new RecordingResponse("@user1 お疲れ様でした。(勤務時間:  - 18:30)"),
                        ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .hasSize(2);
    }

    @Test
    void recoding_宛先の指定なし() {

        Message message = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T12:23:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("出勤")
                .build();

        ResponseEntity<RecordingResponse> response = restTemplate.postForEntity(
                "/api/attendance/recoding",
                message,
                RecordingResponse.class);

        assertThat(response)
                .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
                .returns(null, ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .isEmpty();
    }

    @Test
    void recoding_フレーズに不一致() {

        Message message = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T12:23:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 出")
                .build();

        ResponseEntity<RecordingResponse> response = restTemplate.postForEntity(
                "/api/attendance/recoding",
                message,
                RecordingResponse.class);

        assertThat(response)
                .returns(HttpStatus.OK, ResponseEntity::getStatusCode)
                .returns(null, ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .isEmpty();
    }

    @Test
    void getDayAttendances() {

        Message comeMessage = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T09:15:15")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 出勤")
                .build();

        Message leaveMessage = Message.builder()
                .userName("user1")
                .timestamp(
                        LocalDateTime.parse("2020-04-12T18:30:01")
                                .atZone(ZoneId.systemDefault())
                                .toOffsetDateTime())
                .text("@attendance-bot 退勤")
                .build();

        restTemplate.postForEntity(
                "/api/attendance/recoding",
                comeMessage,
                RecordingResponse.class);

        restTemplate.postForEntity(
                "/api/attendance/recoding",
                leaveMessage,
                RecordingResponse.class);

        ResponseEntity<List<DayAttendance>> response = restTemplate.exchange(
                "/api/attendances?month=2020-04",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DayAttendance>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .containsExactly(
                        new DayAttendance(LocalDate.of(2020, 4, 1), Collections.emptyList()),
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
                                        new DayAttendance.UserAttendance(
                                                "user1",
                                                LocalTime.of(9, 15, 15),
                                                LocalTime.of(18, 30, 1)))),
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
