package com.alefiengo.springboot.api.service;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;

import java.util.List;
import java.util.Optional;

public interface CourseService extends GenericService<Course> {

    Optional<Course> findCourseByCodeIgnoreCase(String code);

    List<Course> findCourseByTitleContains(String title);

    List<Course> findCourseByDescriptionContains(String description);

    List<Student> findStudentsByCourseId(Long id);

    Course getByIdOrThrow(Long id);

    Course create(Course course);

    Course update(Long id, Course course);

    List<Course> search(String code, String title, String description);
}
