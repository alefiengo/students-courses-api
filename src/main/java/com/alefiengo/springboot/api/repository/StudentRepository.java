package com.alefiengo.springboot.api.repository;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s from Student s where s.lastName = ?1 and s.firstName = ?2")
    Optional<Student> findStudentByLastNameAndFirstName(String lastName, String firstName);

    @Query("select s from Student s where lower(s.lastName) like lower(concat('%', ?1, '%'))")
    List<Student> findStudentByLastName(String lastName);

    @Query("select s from Student s where lower(s.firstName) like lower(concat('%', ?1, '%'))")
    List<Student> findStudentByFirstName(String firstName);

    @Query("select c from Student s join s.courses c where s.id = ?1")
    List<Course> findCoursesByStudentId(Long id);

    Optional<Student> findStudentByStudentNumberIgnoreCase(String studentNumber);
}
