package com.todo.service;

import com.todo.exception.TodoNotFoundException;
import com.todo.model.Todo;
import com.todo.model.User;
import com.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public List<Todo> getAllTodosByUser(User user) {
        return todoRepository.findByUserId(user.getId());
    }

    @Override
    public Todo getTodoByIdAndUser(Long id, User user) {
        return todoRepository.findById(id)
                .filter(todo -> todo.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Override
    public Todo createTodo(Todo todo, User user) {
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    @Override
    public Todo updateTodo(Long id, Todo todoDetails, User user) {
        Todo todo = getTodoByIdAndUser(id, user);
        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setCompleted(todoDetails.isCompleted());
        return todoRepository.save(todo);
    }

    @Override
    public void deleteTodo(Long id, User user) {
        Todo todo = getTodoByIdAndUser(id, user);
        todoRepository.delete(todo);
    }
}
