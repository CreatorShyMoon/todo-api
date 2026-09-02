package com.todo.service;

import com.todo.exception.TodoNotFoundException;
import com.todo.model.Todo;
import com.todo.model.User;
import com.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private User user;
    private Todo todo1;
    private Todo todo2;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setId(1L);

        todo1 = new Todo("Задача 1", "Описание 1", user);
        todo1.setId(1L);

        todo2 = new Todo("Задача 2", "Описание 2", user);
        todo2.setId(2L);
        todo2.setCompleted(true);
    }

    @Test
    void getAllTodosByUser_shouldReturnUserTodos() {
        when(todoRepository.findByUserId(1L)).thenReturn(Arrays.asList(todo1, todo2));

        List<Todo> result = todoService.getAllTodosByUser(user);

        assertEquals(2, result.size());
        verify(todoRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getTodoByIdAndUser_shouldReturnTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));

        Todo result = todoService.getTodoByIdAndUser(1L, user);

        assertEquals("Задача 1", result.getTitle());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    void getTodoByIdAndUser_shouldThrowException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class,
                () -> todoService.getTodoByIdAndUser(99L, user));
    }

    @Test
    void createTodo_shouldReturnCreatedTodo() {
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        Todo result = todoService.createTodo(todo1, user);

        assertNotNull(result);
        assertEquals("Задача 1", result.getTitle());
        assertEquals(user, result.getUser());
        verify(todoRepository, times(1)).save(todo1);
    }

    @Test
    void updateTodo_shouldReturnUpdatedTodo() {
        Todo updatedDetails = new Todo("Обновлённая", "Новое описание", user);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        Todo result = todoService.updateTodo(1L, updatedDetails, user);

        assertEquals("Обновлённая", result.getTitle());
        verify(todoRepository, times(1)).save(todo1);
    }

    @Test
    void deleteTodo_shouldDeleteTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        doNothing().when(todoRepository).delete(todo1);

        todoService.deleteTodo(1L, user);

        verify(todoRepository, times(1)).delete(todo1);
    }
}
