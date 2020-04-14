package com.github.onozaty.attendance.domain.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    private final DayAttendancesBuilder dayAttendancesBuilder;

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

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Optional<String> recoding(Message message) {

        Optional<AttendanceType> type = judgeType(message);

        LocalDateTime localDateTime = toLocalDateTime(message.getTimestamp());

        return type
                .map(x -> attendanceRepository.save(
                        AttendanceEntity.builder()
                                .userName(message.getUserName())
                                .type(x)
                                .date(localDateTime.toLocalDate())
                                .time(localDateTime.toLocalTime())
                                .build()))
                .map(this::createRecodingResponse);
    }

    public List<DayAttendance> getDayAttendances(YearMonth month) {

        return dayAttendancesBuilder.build(month);
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

    private String createRecodingResponse(AttendanceEntity currentAttendanceEntity) {

        StringBuilder response = new StringBuilder()
                .append("@").append(currentAttendanceEntity.getUserName())
                .append(" ");

        switch (currentAttendanceEntity.getType()) {

            case COME:

                response.append(comeResponseMessage);
                break;

            case LEAVE:

                AttendanceEntity comeAttendanceEntity = attendanceRepository
                        .findLastComeByUserNameAndDate(
                                currentAttendanceEntity.getUserName(),
                                currentAttendanceEntity.getDate());

                response.append(
                        String.format(
                                leaveResponseMessage,
                                formatTime(comeAttendanceEntity),
                                formatTime(currentAttendanceEntity)));
                break;
            default:
                break;
        }

        return response.toString();
    }

    private String formatTime(AttendanceEntity attendanceEntity) {

        if (attendanceEntity == null) {
            return "";
        }

        return TIME_FORMATTER.format(attendanceEntity.getTime());
    }

    private LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {

        // システムのタイムゾーンに合わせた日時に変換
        return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}
