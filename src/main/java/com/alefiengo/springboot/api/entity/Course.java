package com.alefiengo.springboot.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    @Column(name = "code", nullable = false, length = 10, unique = true)
    @NonNull
    private String code;

    @NotNull
    @NotEmpty
    @Size(max = 150)
    @Column(name = "title", nullable = false, length = 150)
    @NonNull
    private String title;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    @Column(name = "description", nullable = false, length = 255)
    @NonNull
    private String description;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToMany(
            mappedBy = "courses",
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"courses"})
    @ToString.Exclude
    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        if (student == null) {
            return;
        }
        boolean exists = students.stream().anyMatch(existing -> sameStudent(existing, student));
        if (exists) {
            return;
        }
        students.add(student);
        student.getCourses().add(this);
    }

    public void removeStudent(Student student) {
        if (student == null) {
            return;
        }
        students.remove(student);
        student.getCourses().remove(this);
    }

    private boolean sameStudent(Student a, Student b) {
        if (a == b) {
            return true;
        }
        if (a.getId() != null && b.getId() != null) {
            return a.getId().equals(b.getId());
        }
        if (a.getStudentNumber() != null && b.getStudentNumber() != null) {
            return a.getStudentNumber().equalsIgnoreCase(b.getStudentNumber());
        }
        return false;
    }

    @PrePersist
    public void beforePersist() {
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
