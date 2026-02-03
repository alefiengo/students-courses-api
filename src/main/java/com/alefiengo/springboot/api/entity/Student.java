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
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 60)
    @Column(name = "last_name", nullable = false, length = 60)
    @NonNull
    private String lastName;

    @NotNull
    @NotEmpty
    @Size(max = 60)
    @Column(name = "first_name", nullable = false, length = 60)
    @NonNull
    private String firstName;

    @NotNull
    @NotEmpty
    @Size(max = 30)
    @Column(name = "student_number", nullable = false, unique = true, length = 30)
    @NonNull
    private String studentNumber;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonIgnoreProperties({"students"})
    @ToString.Exclude
    private Set<Course> courses = new HashSet<>();

    public void addCourse(Course course) {
        if (course == null) {
            return;
        }
        boolean exists = courses.stream().anyMatch(existing -> sameCourse(existing, course));
        if (exists) {
            return;
        }
        courses.add(course);
        course.getStudents().add(this);
    }

    public void removeCourse(Course course) {
        if (course == null) {
            return;
        }
        courses.remove(course);
        course.getStudents().remove(this);
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
        Student student = (Student) o;
        return id != null && id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
