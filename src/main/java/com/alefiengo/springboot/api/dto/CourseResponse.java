package com.alefiengo.springboot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String code;
    private String title;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
