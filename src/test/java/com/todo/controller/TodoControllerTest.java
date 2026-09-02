package com.todo.controller;

import com.todo.model.Todo;
import com.todo.model.User;
import com.todo.repository.UserRepository;
import com.todo.security.CustomUserDetailsService;
import com.todo.security.JwtAuthFilter;
import com.todo.security.JwtUtil;
import com.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllTodos_shouldReturnTodos() throws Exception {
        Todo todo1 = new Todo("Задача 1", "Описание 1", user);
        todo1.setId(1L);
        Todo todo2 = new Todo("Задача 2", "Описание 2", user);
        todo2.setId(2L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(todoService.getAllTodosByUser(user)).thenReturn(Arrays.asList(todo1, todo2));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Задача 1"))
                .andExpect(jsonPath("$[1].title").value("Задача 2"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTodoById_shouldReturnTodo() throws Exception {
        Todo todo = new Todo("Задача 1", "Описание 1", user);
        todo.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(todoService.getTodoByIdAndUser(1L, user)).thenReturn(todo);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Задача 1"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTodo_shouldReturnCreatedTodo() throws Exception {
        Todo todo = new Todo("Новая задача", "Описание", user);
        todo.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(todoService.createTodo(any(Todo.class), eq(user))).thenReturn(todo);

        mockMvc.perform(post("/api/todos")
                        .contentType("application/json")
                        .content("{\"title\":\"Новая задача\",\"description\":\"Описание\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Новая задача"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTodo_shouldReturnNoContent() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }
}
