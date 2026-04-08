let editMode = false;

window.loadUsers = async function () {
    const params = new URLSearchParams();

    const login = document.getElementById("loginFilter")?.value;
    const name = document.getElementById("nameFilter")?.value;
    const email = document.getElementById("emailFilter")?.value;

    if (login) params.append("login", login);
    if (name) params.append("userName", name);
    if (email) params.append("email", email);

    const res = await apiFetch(`/api/users/search?${params.toString()}`);
    const users = await res.json();

    const table = document.getElementById("usersTable");
    table.innerHTML = "";

    users.forEach(u => {
        table.innerHTML += `
            <tr>
                <td>${u.userId}</td>
                <td>${u.login}</td>
                <td>${u.userName}</td>
                <td>${u.email ?? "-"}</td>
                <td>${(u.roles || []).join(", ")}</td> 
                <td>
                    <button class="btn btn-sm btn-warning" onclick='editUser(${JSON.stringify(u)})'>Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${u.userId})">Delete</button>
                </td>
            </tr>
        `;
    });
}

window.openCreateDrawer = function () {
    editMode = false;
    clearForm();
    document.getElementById("drawerTitle").innerText = "Create User";
    openDrawer();
}

window.editUser = function (u) {
    editMode = true;

    document.getElementById("drawerTitle").innerText = "Edit User";

    document.getElementById("userId").value = u.userId;
    document.getElementById("userIdDisplay").value = u.userId;

    document.getElementById("login").value = u.login || "";
    document.getElementById("userName").value = u.userName || "";
    document.getElementById("email").value = u.email || "";

    openDrawer();
}

window.openDrawer = function () {
    document.getElementById("userDrawer").style.right = "0";
}

window.closeDrawer = function () {
    document.getElementById("userDrawer").style.right = "-400px";
}

window.saveUser = async function () {
    const id = document.getElementById("userId").value;

    const roles = document.getElementById("roles").value
        .split(/[;,]/)
        .map(r => r.trim())
        .filter(r => r);

    const body = {
        login: document.getElementById("login").value,
        password: document.getElementById("password").value,
        fullUserName: document.getElementById("userName").value,
        email: document.getElementById("email").value,
        roles: roles
    };

    if (editMode) {
        await apiFetch(`/api/users/${id}`, {
            method: "PUT",
            body: JSON.stringify(body)
        });
    } else {
        await apiFetch(`/api/users`, {
            method: "POST",
            body: JSON.stringify(body)
        });
    }

    closeDrawer();
    loadUsers();
}

window.deleteUser = async function (id) {
    if (!confirm("Delete user?")) return;

    await apiFetch(`/api/users/${id}`, {
        method: "DELETE"
    });

    loadUsers();
}

window.clearForm = function () {
    document.getElementById("userId").value = "";
    document.getElementById("userIdDisplay").value = "";
    document.getElementById("login").value = "";
    document.getElementById("password").value = "";
    document.getElementById("userName").value = "";
    document.getElementById("email").value = "";
    document.getElementById("roles").value = "";
}