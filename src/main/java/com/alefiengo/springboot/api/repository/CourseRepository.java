package com.alefiengo.springboot.api.repository;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c where upper(c.code) = upper(?1)")
    Optional<Course> findCourseByCodeIgnoreCase(String code);

    @Query("select c from Course c where lower(c.title) like lower(concat('%', ?1, '%'))")
    List<Course> findCourseByTitleContains(String title);

    @Query("select c from Course c where lower(c.description) like lower(concat('%', ?1, '%'))")
    List<Course> findCourseByDescriptionContains(String description);

    @Query("select s from Course c join c.students s where c.id = ?1")
    List<Student> findStudentsByCourseId(Long id);
}
