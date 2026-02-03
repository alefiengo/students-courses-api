package com.alefiengo.springboot.api.controller;

import com.alefiengo.springboot.api.dto.ApiResponse;
import com.alefiengo.springboot.api.dto.CourseResponse;
import com.alefiengo.springboot.api.dto.StudentRequest;
import com.alefiengo.springboot.api.dto.StudentResponse;
import com.alefiengo.springboot.api.entity.Student;
import com.alefiengo.springboot.api.mapper.CourseMapper;
import com.alefiengo.springboot.api.mapper.StudentMapper;
import com.alefiengo.springboot.api.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponse>>> getAll(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        boolean hasFilters = (lastName != null && !lastName.isBlank())
                || (firstName != null && !firstName.isBlank())
                || (studentNumber != null && !studentNumber.isBlank());

        if (!hasFilters && (page != null || size != null)) {
            int pageNumber = page == null ? 0 : Math.max(page, 0);
            int pageSize = size == null ? 10 : Math.max(size, 1);
            Page<StudentResponse> studentsPage = studentService.findAll(PageRequest.of(pageNumber, pageSize))
                    .map(StudentMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.success(studentsPage));
        }

        List<StudentResponse> students = studentService.search(lastName, firstName, studentNumber).stream()
                .map(StudentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(pagedOrList(students, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id) {
        Student student = studentService.getByIdOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(StudentMapper.toResponse(student)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        Student created = studentService.create(StudentMapper.toEntity(request));
        return ResponseEntity.status(201).body(ApiResponse.success(StudentMapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        Student updated = studentService.update(id, StudentMapper.toEntity(request));
        return ResponseEntity.ok(ApiResponse.success(StudentMapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.getByIdOrThrow(id);
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idStudent}/courses/{idCourse}")
    public ResponseEntity<ApiResponse<StudentResponse>> toAssignClassStudent(@PathVariable Long idStudent, @PathVariable Long idCourse) {
        Student student = studentService.assignCourse(idStudent, idCourse);
        return ResponseEntity.ok(ApiResponse.success(StudentMapper.toResponse(student)));
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getCoursesByStudentId(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        List<CourseResponse> courses = studentService.findCoursesByStudentId(id).stream()
                .map(CourseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(pagedOrList(courses, page, size)));
    }

    private <T> Page<T> pagedOrList(List<T> items, Integer page, Integer size) {
        int pageNumber = page == null ? 0 : Math.max(page, 0);
        int pageSize = size == null ? Math.max(items.size(), 1) : Math.max(size, 1);
        int fromIndex = Math.min(pageNumber * pageSize, items.size());
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        List<T> content = items.subList(fromIndex, toIndex);
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), items.size());
    }

 
}
