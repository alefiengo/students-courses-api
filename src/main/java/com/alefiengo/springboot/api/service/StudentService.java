package com.alefiengo.springboot.api.service;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService extends GenericService<Student> {

    Optional<Student> findStudentByLastNameAndFirstName(String lastName, String firstName);

    List<Student> findStudentByLastName(String lastName);

    List<Student> findStudentByFirstName(String firstName);

    List<Course> findCoursesByStudentId(Long id);

    Student getByIdOrThrow(Long id);

    Student create(Student student);

    Student update(Long id, Student student);

    Student assignCourse(Long studentId, Long courseId);

    Student getByStudentNumberOrThrow(String studentNumber);

    Student getByLastNameAndFirstNameOrThrow(String lastName, String firstName);

    List<Student> search(String lastName, String firstName, String studentNumber);
}
