package com.github.onozaty.attendance.domain.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${application.bot-name}")
    private String botName;

    @Value("${application.come-phrase}")
    private String comePhrase;

    @Value("${application.leave-phrase}")
    private String leavePhrase;

    @Value("${application.come-response-message}")
    private String comeResponseMessage;

    @Value("${application.leave-response-message}")
    private String leaveResponseMessage;

    @Value("${application.datetime-format}")
    private String dateTimeFormat;

    public Optional<String> recoding(Message message) {

        Optional<AttendanceType> type = judgeType(message);

        return type
                .map(x -> attendanceRepository.save(
                        AttendanceEntity.builder()
                                .userName(message.getUserName())
                                .type(x)
                                .dateTime(message.getTimestamp())
                                .build()))
                .map(this::createResponse);
    }

    private Optional<AttendanceType> judgeType(Message message) {

        // Bot宛ての発言のみが対象
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

    private String createResponse(AttendanceEntity currentAttendanceEntity) {

        StringBuilder response = new StringBuilder()
                .append("@").append(currentAttendanceEntity.getUserName())
                .append(" ");

        switch (currentAttendanceEntity.getType()) {

            case COME:
                response.append(comeResponseMessage);
                break;
            case LEAVE:

                AttendanceEntity lastComeAttendanceEntity = attendanceRepository
                        .findLastCome(currentAttendanceEntity.getUserName());

                response.append(
                        String.format(
                                leaveResponseMessage,
                                formatDateTime(lastComeAttendanceEntity),
                                formatDateTime(currentAttendanceEntity)));
                break;
            default:
                break;
        }

        return response.toString();
    }

    private String formatDateTime(AttendanceEntity attendanceEntity) {

        if (attendanceEntity == null) {
            return "";
        }

        return DateTimeFormatter.ofPattern(dateTimeFormat)
                // システムのタイムゾーンに合わせてフォーマット
                .format(attendanceEntity.getDateTime().atZoneSameInstant(ZoneId.systemDefault()));
    }
}
