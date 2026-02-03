package com.alefiengo.springboot.api.repository;

import com.alefiengo.springboot.api.data.DataDummy;
import com.alefiengo.springboot.api.entity.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        //given
        courseRepository.save(DataDummy.course01());
        courseRepository.save(DataDummy.course02());
        courseRepository.save(DataDummy.course03());
    }

    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("Find Courses by Code")
    void findCourseByCodeIgnoreCase() {
        //when
        Optional<Course> expected = courseRepository.findCourseByCodeIgnoreCase("cod-001");

        //then
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Find Courses by Title")
    void findCourseByTitleContains() {
        //when
        List<Course> expected = courseRepository.findCourseByTitleContains("Course");

        //then
        assertThat(expected.size() == 3).isTrue();
    }

    @Test
    @DisplayName("Find Courses by Description")
    void findCourseByDescriptionContains() {
        //when
        List<Course> expected = courseRepository.findCourseByDescriptionContains("Description");

        //then
        assertThat(expected.size() == 3).isTrue();
    }
}
