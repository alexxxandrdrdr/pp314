document.addEventListener("DOMContentLoaded", async function () {
    console.log("отправлен запрос текущего пользователя")
    const currentUser = await fetch('/user/current-user', {
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => response.json());
    if (currentUser !== null) {
        console.log("текущий пользователь получен ", currentUser.email)
    }
    let rolesCache = null;

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

        // Загрузка ролей
        async function loadRoles() {
            if (rolesCache != null) return rolesCache;
            try {
                const response = await fetch('/user/roles', {
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
    }


    function updateSidebar() {
        const tab = document.querySelector('#tab-user');
        if (tab) {
            tab.classList.add('active');
            document.querySelector(tab.getAttribute('href'))?.classList.add('show', 'active');
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
    loadCurrentUser();
    updateUserPanel();
});