const API_URL = '/api';
let token = localStorage.getItem('token');
let username = localStorage.getItem('username');

// === Инициализация ===
document.addEventListener('DOMContentLoaded', () => {
    if (token) {
        showTodoScreen();
    }
});

// === Авторизация ===
function showTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.getElementById('loginForm').style.display = tab === 'login' ? 'block' : 'none';
    document.getElementById('registerForm').style.display = tab === 'register' ? 'block' : 'none';
    document.querySelectorAll('.auth-tab')[tab === 'login' ? 0 : 1].classList.add('active');
}

async function login() {
    const loginUsername = document.getElementById('loginUsername').value.trim();
    const loginPassword = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('loginError');
    errorEl.textContent = '';

    if (!loginUsername || !loginPassword) {
        errorEl.textContent = 'Заполните все поля';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: loginUsername, password: loginPassword })
        });

        const data = await response.json();

        if (!response.ok) {
            errorEl.textContent = data.message || 'Ошибка входа';
            return;
        }

        token = data.token;
        username = data.username;
        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        showTodoScreen();
    } catch (e) {
        errorEl.textContent = 'Сервер недоступен';
    }
}

async function register() {
    const regUsername = document.getElementById('regUsername').value.trim();
    const regEmail = document.getElementById('regEmail').value.trim();
    const regPassword = document.getElementById('regPassword').value;
    const errorEl = document.getElementById('regError');
    errorEl.textContent = '';

    if (!regUsername || !regEmail || !regPassword) {
        errorEl.textContent = 'Заполните все поля';
        return;
    }

    if (regPassword.length < 6) {
        errorEl.textContent = 'Пароль минимум 6 символов';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: regUsername, email: regEmail, password: regPassword })
        });

        const data = await response.json();

        if (!response.ok) {
            errorEl.textContent = data.message || 'Ошибка регистрации';
            return;
        }

        token = data.token;
        username = data.username;
        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        showTodoScreen();
    } catch (e) {
        errorEl.textContent = 'Сервер недоступен';
    }
}

function logout() {
    token = null;
    username = null;
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    document.getElementById('authScreen').style.display = 'block';
    document.getElementById('todoScreen').style.display = 'none';
}

function showTodoScreen() {
    document.getElementById('authScreen').style.display = 'none';
    document.getElementById('todoScreen').style.display = 'block';
    document.getElementById('currentUser').textContent = username;
    loadTodos();
}

// === API запросы с JWT ===
function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

async function loadTodos(completed) {
    let url = `${API_URL}/todos`;
    if (completed !== undefined) {
        url += `?completed=${completed}`;
    }

    const response = await fetch(url, { headers: authHeaders() });

    if (response.status === 401 || response.status === 403) {
        logout();
        return;
    }

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

    const response = await fetch(`${API_URL}/todos`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({
            title: title,
            description: descInput.value.trim()
        })
    });

    if (response.status === 401 || response.status === 403) {
        logout();
        return;
    }

    titleInput.value = '';
    descInput.value = '';
    loadTodos();
}

async function toggleTodo(id, completed) {
    const response = await fetch(`${API_URL}/todos/${id}`, { headers: authHeaders() });

    if (response.status === 401 || response.status === 403) {
        logout();
        return;
    }

    const todo = await response.json();

    await fetch(`${API_URL}/todos/${id}`, {
        method: 'PUT',
        headers: authHeaders(),
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

    await fetch(`${API_URL}/todos/${id}`, {
        method: 'DELETE',
        headers: authHeaders()
    });

    loadTodos();
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
