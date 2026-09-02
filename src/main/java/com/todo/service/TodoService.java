package com.todo.service;

import com.todo.model.Todo;
import com.todo.model.User;
import java.util.List;

public interface TodoService {
    List<Todo> getAllTodosByUser(User user);
    Todo getTodoByIdAndUser(Long id, User user);
    Todo createTodo(Todo todo, User user);
    Todo updateTodo(Long id, Todo todoDetails, User user);
    void deleteTodo(Long id, User user);
}
