package com.github.onozaty.attendance.domain.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;
import com.github.onozaty.attendance.domain.entity.AttendanceEntity.AttendanceType;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final AttendanceService attendanceService;

    @Value("${application.chat.bot-name}")
    private String botName;

    @Value("${application.chat.come-phrase}")
    private String comePhrase;

    @Value("${application.chat.leave-phrase}")
    private String leavePhrase;

    @Value("${application.chat.come-response-message}")
    private String comeResponseMessage;

    @Value("${application.chat.leave-response-message}")
    private String leaveResponseMessage;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Optional<String> handleMessage(Message message) {

        return judgeType(message)
                // 対象メッセージの場合のみ出退勤を登録
                .map(type -> attendanceService.record(
                        message.getUserName(),
                        type,
                        toLocalDateTime(message.getTimestamp())))
                .map(this::createResponseMessage);
    }

    private Optional<AttendanceType> judgeType(Message message) {

        // bot宛ての発言のみが対象
        if (!message.getText().contains("@" + botName)) {
            return Optional.empty();
        }

        if (message.getText().contains(comePhrase)) {
            return Optional.of(AttendanceType.COME);
        }

        if (message.getText().contains(leavePhrase)) {
            return Optional.of(AttendanceType.LEAVE);
        }

        return Optional.empty();
    }

    private String createResponseMessage(AttendanceEntity currentAttendanceEntity) {

        StringBuilder response = new StringBuilder()
                .append("@").append(currentAttendanceEntity.getUserName())
                .append(" ");

        switch (currentAttendanceEntity.getType()) {

            case COME:

                response.append(comeResponseMessage);
                break;

            case LEAVE:

                UserAttendance userAttendance = attendanceService.getUserAttendance(
                        currentAttendanceEntity.getUserName(),
                        currentAttendanceEntity.getDate());

                response.append(
                        String.format(
                                leaveResponseMessage,
                                formatTime(userAttendance.getComeTime()),
                                formatTime(currentAttendanceEntity.getTime())));
                break;
            default:
                break;
        }

        return response.toString();
    }

    private String formatTime(LocalTime time) {

        if (time == null) {
            return "";
        }

        return TIME_FORMATTER.format(time);
    }

    private LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {

        // システムのタイムゾーンに合わせた日時に変換
        return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}
