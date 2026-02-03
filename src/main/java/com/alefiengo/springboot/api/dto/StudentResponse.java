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
public class StudentResponse {
    private Long id;
    private String lastName;
    private String firstName;
    private String studentNumber;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
