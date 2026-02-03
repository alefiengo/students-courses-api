package com.alefiengo.springboot.api.mapper;

import com.alefiengo.springboot.api.dto.CourseRequest;
import com.alefiengo.springboot.api.dto.CourseResponse;
import com.alefiengo.springboot.api.entity.Course;

public final class CourseMapper {

    private CourseMapper() {
    }

    public static Course toEntity(CourseRequest request) {
        return new Course(request.getCode(), request.getTitle(), request.getDescription());
    }

    public static CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getDescription(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
