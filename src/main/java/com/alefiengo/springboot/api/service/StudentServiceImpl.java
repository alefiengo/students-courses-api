package com.alefiengo.springboot.api.service;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;
import com.alefiengo.springboot.api.exception.ConflictException;
import com.alefiengo.springboot.api.exception.ResourceNotFoundException;
import com.alefiengo.springboot.api.repository.CourseRepository;
import com.alefiengo.springboot.api.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends GenericServiceImpl<Student, StudentRepository> implements StudentService {

    private final CourseRepository courseRepository;

    public StudentServiceImpl(StudentRepository repository, CourseRepository courseRepository) {
        super(repository);
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findStudentByLastNameAndFirstName(String lastName, String firstName) {
        return repository.findStudentByLastNameAndFirstName(lastName, firstName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentByLastName(String lastName) {
        return repository.findStudentByLastName(lastName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentByFirstName(String firstName) {
        return repository.findStudentByFirstName(firstName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCoursesByStudentId(Long id) {
        return repository.findCoursesByStudentId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Student getByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No student with ID: '%d', found.", id)
                ));
    }

    @Override
    @Transactional
    public Student create(Student student) {
        validateUniqueStudentNumber(student.getStudentNumber(), null);
        return repository.save(student);
    }

    @Override
    @Transactional
    public Student update(Long id, Student student) {
        Student existing = getByIdOrThrow(id);
        validateUniqueStudentNumber(student.getStudentNumber(), existing.getId());
        existing.setLastName(student.getLastName());
        existing.setFirstName(student.getFirstName());
        existing.setStudentNumber(student.getStudentNumber());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public Student assignCourse(Long studentId, Long courseId) {
        Student student = getByIdOrThrow(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No course with ID: '%d', found.", courseId)
                ));
        if (isAlreadyAssigned(student, course)) {
            throw new ConflictException(String.format(
                    "Student with ID '%d' is already assigned to course with ID '%d'.",
                    studentId,
                    courseId
            ));
        }
        student.addCourse(course);
        return repository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Student getByStudentNumberOrThrow(String studentNumber) {
        return repository.findStudentByStudentNumberIgnoreCase(studentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No student with student number: '%s', found.", studentNumber)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Student getByLastNameAndFirstNameOrThrow(String lastName, String firstName) {
        return repository.findStudentByLastNameAndFirstName(lastName, firstName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No student with last name: '%s' and first name: '%s', found.", lastName, firstName)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> search(String lastName, String firstName, String studentNumber) {
        if (studentNumber != null && !studentNumber.isBlank()) {
            return repository.findStudentByStudentNumberIgnoreCase(studentNumber)
                    .map(List::of)
                    .orElseGet(List::of);
        }

        boolean hasLastName = lastName != null && !lastName.isBlank();
        boolean hasFirstName = firstName != null && !firstName.isBlank();

        if (hasLastName && hasFirstName) {
            List<Student> byLastName = repository.findStudentByLastName(lastName);
            String firstNameLower = firstName.toLowerCase();
            return byLastName.stream()
                    .filter(s -> s.getFirstName() != null && s.getFirstName().toLowerCase().contains(firstNameLower))
                    .collect(Collectors.toList());
        }

        if (hasLastName) {
            return repository.findStudentByLastName(lastName);
        }

        if (hasFirstName) {
            return repository.findStudentByFirstName(firstName);
        }

        return repository.findAll();
    }

    private void validateUniqueStudentNumber(String studentNumber, Long currentId) {
        if (studentNumber == null || studentNumber.isBlank()) {
            return;
        }
        repository.findStudentByStudentNumberIgnoreCase(studentNumber)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ConflictException(String.format(
                            "Student with student number '%s' already exists.",
                            studentNumber
                    ));
                });
    }

    private boolean isAlreadyAssigned(Student student, Course course) {
        return student.getCourses().stream().anyMatch(existing -> sameCourse(existing, course));
    }

    private boolean sameCourse(Course a, Course b) {
        if (a == b) {
            return true;
        }
        if (a.getId() != null && b.getId() != null) {
            return a.getId().equals(b.getId());
        }
        if (a.getCode() != null && b.getCode() != null) {
            return a.getCode().equalsIgnoreCase(b.getCode());
        }
        return false;
    }
}
