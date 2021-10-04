package com.alefiengo.springboot.api.service;

import com.alefiengo.springboot.api.entity.Course;

import java.util.Optional;

public interface CourseService extends GenericService<Course> {

    Optional<Course> findCourseByCodeIgnoreCase(String code);

    Iterable<Course> findCourseByTitleContains(String title);

    Iterable<Course> findCourseByDescriptionContains(String description);
}
