package com.alefiengo.springboot.api.service;

import com.alefiengo.springboot.api.repository.CourseRepository;
import com.alefiengo.springboot.api.repository.StudentRepository;
import com.alefiengo.springboot.api.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    StudentService studentService;

    @Mock
    StudentRepository studentRepository;

    @Mock
    CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        studentService = new StudentServiceImpl(studentRepository, courseRepository);
    }

    @Test
    void findStudentByLastNameAndFirstName() {
        //given
        when(studentRepository.findStudentByLastNameAndFirstName("Fiengo", "Alejandro"))
                .thenReturn(Optional.of(new Student("Fiengo", "Alejandro", "STU-001")));

        //when
        Optional<Student> expected = studentService.findStudentByLastNameAndFirstName("Fiengo", "Alejandro");

        //then
        assertThat(expected).isPresent();
        verify(studentRepository).findStudentByLastNameAndFirstName("Fiengo", "Alejandro");
    }

    @Test
    void findStudentByLastName() {
        //given
        when(studentRepository.findStudentByLastName("Fiengo"))
                .thenReturn(Arrays.asList(new Student("Fiengo", "Alejandro", "STU-001")));

        //when
        List<Student> expected = studentService.findStudentByLastName("Fiengo");

        //then
        assertThat(expected).hasSize(1);
        verify(studentRepository).findStudentByLastName("Fiengo");
    }

    @Test
    void findStudentByFirstName() {
        //given
        when(studentRepository.findStudentByFirstName("Alejandro"))
                .thenReturn(Arrays.asList(new Student("Fiengo", "Alejandro", "STU-001")));

        //when
        List<Student> expected = studentService.findStudentByFirstName("Alejandro");

        //then
        assertThat(expected).hasSize(1);
        verify(studentRepository).findStudentByFirstName("Alejandro");
    }

    @Test
    void assignCourse() {
        //given
        Student student = new Student("Fiengo", "Alejandro", "STU-001");
        Course course = new Course("COD-101", "Intro", "Basico");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(studentRepository.save(student)).thenReturn(student);

        //when
        Student expected = studentService.assignCourse(1L, 2L);

        //then
        assertThat(expected.getCourses()).contains(course);
        assertThat(course.getStudents()).contains(student);
        verify(studentRepository).save(student);
    }

    @Test
    void createThrowsConflictWhenDuplicateStudentNumber() {
        //given
        Student existing = new Student("Fiengo", "Alejandro", "STU-999");
        when(studentRepository.findStudentByStudentNumberIgnoreCase("STU-999"))
                .thenReturn(Optional.of(existing));

        Student toCreate = new Student("Fiengo", "Alejandro", "STU-999");

        //when / then
        assertThatThrownBy(() -> studentService.create(toCreate))
                .isInstanceOf(ConflictException.class);
        verify(studentRepository, never()).save(any());
    }

    @Test
    void assignCourseThrowsConflictWhenAlreadyAssigned() {
        //given
        Student student = new Student("Fiengo", "Alejandro", "STU-001");
        Course course = new Course("COD-101", "Intro", "Basico");
        student.addCourse(course);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));

        //when / then
        assertThatThrownBy(() -> studentService.assignCourse(1L, 2L))
                .isInstanceOf(ConflictException.class);
        verify(studentRepository, never()).save(any());
    }
}
