package com.github.onozaty.attendance.api.controller;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity.AttendanceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AttendanceRecordRequest {

    private String userName;

    @Enumerated(EnumType.STRING)
    private AttendanceType type;
}
