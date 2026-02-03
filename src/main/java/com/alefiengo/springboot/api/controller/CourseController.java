package com.alefiengo.springboot.api.controller;

import com.alefiengo.springboot.api.dto.ApiResponse;
import com.alefiengo.springboot.api.dto.CourseRequest;
import com.alefiengo.springboot.api.dto.CourseResponse;
import com.alefiengo.springboot.api.dto.StudentResponse;
import com.alefiengo.springboot.api.entity.Course;
import com.alefiengo.springboot.api.mapper.CourseMapper;
import com.alefiengo.springboot.api.mapper.StudentMapper;
import com.alefiengo.springboot.api.service.CourseService;
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
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getAll(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        boolean hasFilters = (code != null && !code.isBlank())
                || (title != null && !title.isBlank())
                || (description != null && !description.isBlank());

        if (!hasFilters && (page != null || size != null)) {
            int pageNumber = page == null ? 0 : Math.max(page, 0);
            int pageSize = size == null ? 10 : Math.max(size, 1);
            Page<CourseResponse> coursesPage = courseService.findAll(PageRequest.of(pageNumber, pageSize))
                    .map(CourseMapper::toResponse);
            return ResponseEntity.ok(ApiResponse.success(coursesPage));
        }

        List<CourseResponse> courses = courseService.search(code, title, description).stream()
                .map(CourseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(pagedOrList(courses, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getById(@PathVariable Long id) {
        Course course = courseService.getByIdOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(CourseMapper.toResponse(course)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> create(@Valid @RequestBody CourseRequest request) {
        Course created = courseService.create(CourseMapper.toEntity(request));
        return ResponseEntity.status(201).body(ApiResponse.success(CourseMapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        Course updated = courseService.update(id, CourseMapper.toEntity(request));
        return ResponseEntity.ok(ApiResponse.success(CourseMapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.getByIdOrThrow(id);
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<ApiResponse<Page<StudentResponse>>> getStudentsByCourseId(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        List<StudentResponse> students = courseService.findStudentsByCourseId(id).stream()
                .map(StudentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(pagedOrList(students, page, size)));
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
