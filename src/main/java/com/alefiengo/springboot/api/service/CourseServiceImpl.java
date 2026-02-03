package com.alefiengo.springboot.api.service;

import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.entity.Student;
import com.alefiengo.springboot.api.exception.ConflictException;
import com.alefiengo.springboot.api.exception.ResourceNotFoundException;
import com.alefiengo.springboot.api.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends GenericServiceImpl<Course, CourseRepository> implements CourseService {

    public CourseServiceImpl(CourseRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findCourseByCodeIgnoreCase(String code) {
        return repository.findCourseByCodeIgnoreCase(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCourseByTitleContains(String title) {
        return repository.findCourseByTitleContains(title);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCourseByDescriptionContains(String description) {
        return repository.findCourseByDescriptionContains(description);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentsByCourseId(Long id) {
        return repository.findStudentsByCourseId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Course getByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No course with ID: '%d', found.", id)
                ));
    }

    @Override
    @Transactional
    public Course create(Course course) {
        if (repository.findCourseByCodeIgnoreCase(course.getCode()).isPresent()) {
            throw new ConflictException(String.format("Course with code: '%s' already exists.", course.getCode()));
        }
        return repository.save(course);
    }

    @Override
    @Transactional
    public Course update(Long id, Course course) {
        Course existing = getByIdOrThrow(id);
        Optional<Course> byCode = repository.findCourseByCodeIgnoreCase(course.getCode());
        if (byCode.isPresent() && !byCode.get().getId().equals(id)) {
            throw new ConflictException(String.format("Course with code: '%s' already exists.", course.getCode()));
        }
        existing.setCode(course.getCode());
        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> search(String code, String title, String description) {
        boolean hasCode = code != null && !code.isBlank();
        boolean hasTitle = title != null && !title.isBlank();
        boolean hasDescription = description != null && !description.isBlank();

        if (hasCode) {
            return repository.findCourseByCodeIgnoreCase(code)
                    .map(List::of)
                    .orElseGet(List::of);
        }

        if (hasTitle && hasDescription) {
            String descriptionLower = description.toLowerCase();
            return repository.findCourseByTitleContains(title).stream()
                    .filter(c -> c.getDescription() != null && c.getDescription().toLowerCase().contains(descriptionLower))
                    .collect(Collectors.toList());
        }

        if (hasTitle) {
            return repository.findCourseByTitleContains(title);
        }

        if (hasDescription) {
            return repository.findCourseByDescriptionContains(description);
        }

        return repository.findAll();
    }
}
