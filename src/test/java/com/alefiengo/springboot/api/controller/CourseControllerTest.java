package com.alefiengo.springboot.api.controller;

import com.alefiengo.springboot.api.exception.handler.GlobalExceptionHandler;
import com.alefiengo.springboot.api.exception.ConflictException;
import com.alefiengo.springboot.api.exception.ResourceNotFoundException;
import com.alefiengo.springboot.api.service.CourseService;
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

@WebMvcTest(CourseController.class)
@Import(GlobalExceptionHandler.class)
class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CourseService courseService;

    @Test
    @DisplayName("GET /courses returns 200 with empty list")
    void getAllReturnsEmptyList() throws Exception {
        when(courseService.search(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("GET /courses/{id} returns 404 when not found")
    void getByIdNotFound() throws Exception {
        when(courseService.getByIdOrThrow(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/courses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /courses returns 400 with validation errors")
    void createValidationErrors() throws Exception {
        String payload = "{}";

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /courses returns 409 when code already exists")
    void createDuplicateCode() throws Exception {
        when(courseService.create(any()))
                .thenThrow(new ConflictException("duplicate"));

        String payload = "{\"code\":\"COD-101\",\"title\":\"Intro\",\"description\":\"Basico\"}";
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }
}
