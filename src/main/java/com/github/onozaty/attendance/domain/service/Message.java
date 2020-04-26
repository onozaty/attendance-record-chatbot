package com.github.onozaty.attendance.domain.service;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @JsonProperty("channel_name")
    private String channelName;

    private OffsetDateTime timestamp;

    @JsonProperty("user_name")
    private String userName;

    private String text;
}
