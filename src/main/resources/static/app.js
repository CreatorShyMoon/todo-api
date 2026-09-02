const API_URL = '/api/todos';

async function loadTodos(completed) {
    let url = API_URL;
    if (completed !== undefined) {
        url += `?completed=${completed}`;
    }

    const response = await fetch(url);
    const todos = await response.json();

    const list = document.getElementById('todoList');
    list.innerHTML = '';

    if (todos.length === 0) {
        list.innerHTML = '<li class="empty-message">Нет задач</li>';
        return;
    }

    todos.forEach(todo => {
        const li = document.createElement('li');
        li.className = todo.completed ? 'completed' : '';

        li.innerHTML = `
            <input type="checkbox" ${todo.completed ? 'checked' : ''}
                   onchange="toggleTodo(${todo.id}, this.checked)" />
            <div class="todo-text">
                <div class="todo-title">${escapeHtml(todo.title)}</div>
                ${todo.description ? `<div class="todo-desc">${escapeHtml(todo.description)}</div>` : ''}
            </div>
            <button class="delete-btn" onclick="deleteTodo(${todo.id})">Удалить</button>
        `;

        list.appendChild(li);
    });
}

async function addTodo() {
    const titleInput = document.getElementById('titleInput');
    const descInput = document.getElementById('descInput');
    const title = titleInput.value.trim();

    if (!title) {
        alert('Введите название задачи');
        return;
    }

    await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            description: descInput.value.trim()
        })
    });

    titleInput.value = '';
    descInput.value = '';
    loadTodos();
}

async function toggleTodo(id, completed) {
    const response = await fetch(`${API_URL}/${id}`);
    const todo = await response.json();

    await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: todo.title,
            description: todo.description,
            completed: completed
        })
    });

    loadTodos();
}

async function deleteTodo(id) {
    if (!confirm('Удалить задачу?')) return;

    await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
    loadTodos();
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

document.addEventListener('DOMContentLoaded', () => loadTodos());
