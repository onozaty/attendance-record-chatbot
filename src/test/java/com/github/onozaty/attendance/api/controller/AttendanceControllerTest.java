package com.github.onozaty.attendance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;
import com.github.onozaty.attendance.domain.entity.AttendanceEntity.AttendanceType;
import com.github.onozaty.attendance.domain.repository.AttendanceRepository;
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
                .returns(message.getTimestamp(), AttendanceEntity::getDateTime)
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
                .returns(new RecordingResponse("@user1 お疲れ様でした。(勤務時間:  - 04月12日12:23)"), ResponseEntity::getBody);

        assertThat(attendanceRepository.findAll())
                .hasSize(1)
                .first()
                .returns(message.getUserName(), AttendanceEntity::getUserName)
                .returns(message.getTimestamp(), AttendanceEntity::getDateTime)
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
                .returns(new RecordingResponse("@user1 お疲れ様でした。(勤務時間: 04月12日09:15 - 04月12日18:30)"),
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
}
