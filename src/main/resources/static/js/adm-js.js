
    function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email.toLowerCase());
}

    // функция для валидации поля email
    function checkEmailInput(inputId) {
    const input = document.getElementById(inputId);
    const email = input.value.trim();

    if (!validateEmail(email)) {
    input.focus();
    return false;
}
    return true;
}

    document.addEventListener("DOMContentLoaded", async function () {
        const currentUser = await fetch('/user/current-user', {
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => response.json());

        // Обработка закрытия модалок
        function handleModalClose(modalId) {
            document.querySelectorAll(`#${modalId} [data-bs-dismiss="modal"]`).forEach(btn => {
                btn.addEventListener('click', function () {
                    const modalElement = document.getElementById(modalId);
                    const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
                    modal.hide();
                    modalElement.addEventListener('hidden.bs.modal', function () {
                        document.body.classList.remove('modal-open');
                        const backdrop = document.querySelector('.modal-backdrop');
                        if (backdrop) backdrop.remove();
                        document.body.focus();
                        console.log(`Модальное окно ${modalId} закрыто`);
                    }, {once: true});
                });
            });
        }

        handleModalClose("editUserModal");
        handleModalClose("deleteUserModal");

        // Загрузка ролей
        let rolesCache =null;
        async function loadRoles() {
            if (rolesCache != null) return rolesCache;
            try {
                const response = await fetch('/admin/api/roles', {
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                rolesCache = await response.json();
                console.log("Роли загружены:", rolesCache);
                return rolesCache;
            } catch (error) {
                console.error("Ошибка загрузки ролей:", error);
                alert('Не удалось загрузить роли');
                return [];
            }
        }

        // Заполнение select ролей
        function populateRolesSelect(selectId, selectedRoleIds = []) {
            loadRoles().then(roles => {
                const select = document.getElementById(selectId);
                if (select) {
                    select.innerHTML = '';
                    if (roles.length === 0) {
                        const option = document.createElement('option');
                        option.text = 'Нет ролей';
                        option.disabled = true;
                        select.appendChild(option);
                    } else {
                        roles.forEach(role => {
                            const option = document.createElement('option');
                            option.value = role.id;
                            option.text = role.name.slice(5);

                            if (selectedRoleIds.includes(parseInt(role.id))) option.selected = true;
                            console.log('выбраны роли: ', selectedRoleIds)
                            select.appendChild(option);
                        });
                    }
                }
            });
        }

        // Загрузка текущего пользователя
        async function loadCurrentUser() {
            document.getElementById('username').textContent = currentUser.email || 'User';
            await loadRoles();
            const roleNames = currentUser.roles
                ? currentUser.roles
                    .map(role => role.name.startsWith('ROLE_') ? role.name.slice(5) : role.name)
                    .filter(name => name)
                    .join(', ')
                : '';
            document.getElementById('user-roles').textContent = `with roles: ${roleNames}`;
            updateUserPanel();
            updateSidebar();
            console.log('currentUser.roles:', currentUser.roles);
            console.log('rolesCache:', rolesCache);
        }

        /* // Активация вкладки по роли
         function updateSidebar() {
             if (!currentUser || !rolesCache) {
                 console.warn('Данные пользователя или роли не загружены');
                 return;
             }
             const adminTab = document.getElementById('tab-admin');
             const userTab = document.getElementById('tab-user');
             const adminContent = document.getElementById('content-admin');
             const userContent = document.getElementById('content-user');

             // Очистка активных классов
             [adminTab, userTab].forEach(tab => tab.classList.remove('active', 'text-white', 'bg-primary'));
             [adminContent, userContent].forEach(content => content.classList.remove('show', 'active'));


    // Проверка наличия ролей напрямую по имени
             const hasAdminRole = currentUser.roles.some(role => role.name === 'ROLE_ADMIN');
             // Активация вкладки по приоритету: ROLE_ADMIN > ROLE_USER
             if (hasAdminRole) {
                 adminTab.classList.add('active', 'text-white', 'bg-primary');
                 adminContent.classList.add('show', 'active');
                 console.log('Активирована вкладка "Admin" для ROLE_ADMIN');
             } else {
                 userTab.classList.add('active', 'text-white', 'bg-primary');
                 userContent.classList.add('show', 'active');
                 console.log('Активирована вкладка "User" по умолчанию');
             }
             console.log('currentUser.roles:', currentUser.roles);
             console.log('rolesCache:', rolesCache);
         }*/

        function updateSidebar() {
            const userRoles = currentUser.roles
                ? currentUser.roles.map(role => role.name) : "";

            let targetId = null;
            console.log("userRoles++ " + userRoles);
            if (userRoles.includes('ROLE_ADMIN')) targetId = '#tab-admin';
            else if (userRoles.includes('ROLE_USER')) targetId = '#tab-user';
            const tab = document.querySelector(targetId);
            if (tab) {
                tab.classList.add('active');
                document.querySelector(tab.getAttribute('href'))?.classList.add('show', 'active');
            }
        }

        // Обработчик переключения вкладок
        function handleTabSwitch(event) {
            const adminTab = document.getElementById('tab-admin');
            const userTab = document.getElementById('tab-user');
            const adminContent = document.getElementById('content-admin');
            const userContent = document.getElementById('content-user');

            // Очистка классов
            [adminTab, userTab].forEach(tab => {
                tab.classList.remove('active', 'text-white', 'bg-primary');
                tab.setAttribute('aria-selected', 'false');
            });
            [adminContent, userContent].forEach(content => {
                content.classList.remove('show', 'active');
            });

            // Активация выбранной вкладки
            const targetTab = event.target;
            const targetContentId = targetTab.getAttribute('href').substring(1); // Убираем # из href
            const targetContent = document.getElementById(targetContentId);
            targetTab.classList.add('active', 'text-white', 'bg-primary');
            targetTab.setAttribute('aria-selected', 'true');
            targetContent.classList.add('show', 'active');
            console.log(`Переключено на вкладку: ${targetTab.textContent}`);
        }

        // Привязка обработчиков переключения вкладок
        document.querySelectorAll('#role-tab .nav-link').forEach(tab => {
            tab.addEventListener('shown.bs.tab', handleTabSwitch);
        });

        // Загрузка списка пользователей
        async function loadUsers() {
            if (currentUser.roles.some(role => role.name === 'ROLE_ADMIN')) {
                try {
                    const response = await fetch('/admin/api/users', {
                        headers: {
                            'Accept': 'application/json'
                        }
                    });
                    const users = await response.json();
                    const tbody = document.getElementById('users-table-body');
                    tbody.innerHTML = '';
                    await loadRoles();
                    users.forEach(user => {
                        const roleBadges = user.roles ? user.roles.map(role => role.name.startsWith('ROLE_') ? role.name.slice(5) : role.name)
                                .filter(name => name)
                                .join(', ')
                            : '';
                        const row = document.createElement('tr');
                        row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.lastname || ''}</td>
                    <td>${user.firstname || ''}</td>
                    <td>${user.age || ''}</td>
                    <td>${user.email || ''}</td>
                    <td>${roleBadges}</td>
                    <td>
                        <button type="button" class="btn btn-primary edit-btn" data-id="${user.id}">Edit</button>
                    </td>
                    <td>
                    <button type="button" class="btn btn-danger delete-btn" data-id="${user.id}">Delete</button>
</td>
                `;
                        tbody.appendChild(row);
                    });
                    attachEventListeners();
                } catch (error) {
                    console.error('Ошибка загрузки пользователей:', error);
                    alert('Не удалось загрузить список пользователей');
                }
            }

        }

        // Обновление панели пользователя
        function updateUserPanel() {
            if (!currentUser) return;
            const tbody = document.getElementById('user-info-body');
            tbody.innerHTML = '';
            const roleBadges = currentUser.roles ? currentUser.roles.map(role => role.name.startsWith('ROLE_') ? role.name.slice(5) : role.name)
                    .filter(name => name)
                    .join(', ')
                : '';
            const row = document.createElement('tr');
            row.classList.add('table-light');
            row.innerHTML = `
            <td>${currentUser.id}</td>
            <td>${currentUser.lastname || ''}</td>
            <td>${currentUser.firstname || ''}</td>
            <td>${currentUser.age || ''}</td>
            <td>${currentUser.email || ''}</td>
            <td>${roleBadges}</td>
        `;
            tbody.appendChild(row);
        }

        // Загрузка данных пользователя для модалки
        async function loadUserForModal(userId, isEdit = true) {
            try {
                const response = await fetch(`/admin/api/user-info/${userId}`);
                if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
                const userDTO = await response.json();
                const prefix = isEdit ? 'edit' : 'delete';
                console.log('получен пользователь: ', userDTO.lastname, userDTO.roles)
                document.getElementById(`${prefix}-id`).value = userDTO.id || '';
                document.getElementById(`${prefix}-firstname`).value = userDTO.firstname || '';
                document.getElementById(`${prefix}-lastname`).value = userDTO.lastname || '';
                document.getElementById(`${prefix}-age`).value = userDTO.age || '';
                document.getElementById(`${prefix}-email`).value = userDTO.email || '';
                populateRolesSelect(`${prefix}-roles`, userDTO.roles || []);
                const modalId = isEdit ? 'editUserModal' : 'deleteUserModal';
                new bootstrap.Modal(document.getElementById(modalId)).show();
            } catch (error) {
                console.error('Ошибка загрузки данных:', error);
                alert('Не удалось загрузить данные пользователя');
            }
        }

        // Удаление пользователя
        async function deleteUser(userId) {
            try {
                const response = await fetch(`/admin/api/delete-user/${userId}`, {
                    method: 'DELETE',
                    headers: {'Content-Type': 'application/json'}
                });
                if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
                bootstrap.Modal.getInstance(document.getElementById('deleteUserModal')).hide();
                loadUsers();
            } catch (error) {
                console.error('Ошибка удаления:', error);
                alert('Не удалось удалить пользователя');
            }
        }

        // Привязка обработчиков
        function attachEventListeners() {
            document.querySelectorAll('.edit-btn').forEach(btn => {
                btn.addEventListener('click', () => loadUserForModal(btn.dataset.id, true));
            });
            document.querySelectorAll('.delete-btn').forEach(btn => {
                btn.addEventListener('click', () => loadUserForModal(btn.dataset.id, false));
            });
        }

        // Обработчики форм
        document.getElementById('editUserForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const userId = formData.get('id');
            const userData = new URLSearchParams();
            userData.append("id", parseInt(userId));
            userData.append("firstname", formData.get('firstname'));
            userData.append("lastname", formData.get('lastname'));
            userData.append("age", parseInt(formData.get('age')) || null);
            userData.append("email", formData.get('email'));
            userData.append("password", formData.get('password') || null);
            formData.getAll('roles').forEach(role => userData.append('roles', role));

            try {
                if (checkEmailInput('edit-email') === true) {
                    const response = await fetch(`/admin/api/edit-user/${userId}`, {
                        method: 'PATCH',
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: userData.toString()
                    });
                    if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
                    bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
                    loadUsers();
                } else {
                    alert("Введите email в формате: name@domain.com")
                }
            } catch (error) {
                console.error('Ошибка редактирования:', error);
                alert('Не удалось обновить пользователя');
            }
        });

        document.getElementById('newUserForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const data = {
                firstname: formData.get('firstname'),
                lastname: formData.get('lastname'),
                age: parseInt(formData.get('age')) || null,
                email: formData.get('email'),
                password: formData.get('password'),
                roles: Array.from(formData.getAll('roles')).map(Number)
            };
            try {
                if (checkEmailInput('new-email') === true) {
                    const response = await fetch('/admin/api/addUser', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(data)
                    });
                    if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
                    e.target.reset();
                    document.getElementById('users-tab').click();
                    loadUsers();
                } else {
                    alert("Введите email в формате: name@domain.com")
                }
            } catch (error) {
                console.error('Ошибка создания:', error);
                alert('Не удалось создать пользователя');
            }
        });

        document.getElementById('deleteUserForm').addEventListener('submit', (e) => {
            e.preventDefault();
            const userId = document.getElementById('delete-id').value;
            if (userId) deleteUser(userId);
        });

// Инициализация
        populateRolesSelect('edit-roles');
        populateRolesSelect('new-roles');
        populateRolesSelect('delete-userRoles');
        loadCurrentUser();
        loadUsers();


    });
