package com.alefiengo.springboot.api.controller;

import com.alefiengo.springboot.api.entity.Student;
import com.alefiengo.springboot.api.exception.ResourceNotFoundException;
import com.alefiengo.springboot.api.exception.handler.GlobalExceptionHandler;
import com.alefiengo.springboot.api.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    StudentService studentService;

    @Test
    @DisplayName("GET /students returns 200 with empty list")
    void getAllReturnsEmptyList() throws Exception {
        when(studentService.search(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("GET /students/{id} returns 404 when not found")
    void getByIdNotFound() throws Exception {
        when(studentService.getByIdOrThrow(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /students returns 400 with validation errors")
    void createValidationErrors() throws Exception {
        String payload = "{}";

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /students/{idStudent}/courses/{idCourse} assigns course")
    void assignCourseToStudent() throws Exception {
        Student student = new Student("Fiengo", "Alejandro", "STU-001");
        when(studentService.assignCourse(1L, 1L)).thenReturn(student);

        mockMvc.perform(post("/students/1/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
