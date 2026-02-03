package com.alefiengo.springboot.api.mapper;

import com.alefiengo.springboot.api.dto.StudentRequest;
import com.alefiengo.springboot.api.dto.StudentResponse;
import com.alefiengo.springboot.api.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentRequest request) {
        return new Student(request.getLastName(), request.getFirstName(), request.getStudentNumber());
    }

    public static StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getLastName(),
                student.getFirstName(),
                student.getStudentNumber(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}
