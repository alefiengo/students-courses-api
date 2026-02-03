package com.alefiengo.springboot.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {

    @NotBlank
    @Size(max = 10)
    private String code;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;
}
