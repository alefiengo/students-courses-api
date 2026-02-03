package com.alefiengo.springboot.api.repository;

import com.alefiengo.springboot.api.data.DataDummy;
import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;
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
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        //given
        studentRepository.save(DataDummy.student01());
        studentRepository.save(DataDummy.student02());
        studentRepository.save(DataDummy.student03());
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Find Student by Last Name and First Name")
    void findStudentByLastNameAndFirstName() {
        //when
        Optional<Student> expected = studentRepository.findStudentByLastNameAndFirstName("Fiengo", "Alejandro");

        //then
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Find Student by Last Name")
    void findStudentByLastName() {
        //when
        List<Student> expected = studentRepository.findStudentByLastName("Fiengo");

        //then
        assertThat(expected.size() == 1).isTrue();
    }

    @Test
    @DisplayName("Find Student by First Name")
    void findStudentByFirstName() {
        //when
        List<Student> expected = studentRepository.findStudentByFirstName("Alejandro");

        //then
        assertThat(expected.size() == 1).isTrue();
    }

    @Test
    @DisplayName("Assign Course to Student and Query by Student Id")
    void assignCourseAndFindByStudentId() {
        //given
        Student student = new Student("Fiengo", "Alejandro", "STU-101");
        Course course = new Course("COD-101", "Intro", "Basico");
        courseRepository.save(course);
        student.addCourse(course);
        Student savedStudent = studentRepository.save(student);

        //when
        List<Course> expected = studentRepository.findCoursesByStudentId(savedStudent.getId());

        //then
        assertThat(expected).hasSize(1);
        assertThat(expected.get(0).getCode()).isEqualTo("COD-101");
    }

    @Test
    @DisplayName("Student Number can be queried (case-insensitive)")
    void studentNumberQueryable() {
        //given
        Student student = new Student("Lopez", "Ana", "STU-999");
        Student savedStudent = studentRepository.save(student);

        //then
        Optional<Student> expected = studentRepository.findStudentByStudentNumberIgnoreCase("stu-999");
        assertThat(expected).isPresent();
    }
}
