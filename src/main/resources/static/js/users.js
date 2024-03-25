async function addRowsToUsersTable() {
    const tableFields = ['id','name','lastName','age','email'];
    const tableBody = document.querySelector('#usersTable').tBodies[0];
    const usersDataResponseURL = "http://localhost:8085/api/users/";

    const usersDataResponse = await fetch(usersDataResponseURL);

    if (!usersDataResponse.ok) {
        alert(`Bad status from response by URL ${usersDataResponseURL}: ${usersDataResponse.status} ${usersDataResponse.statusText}`);
        return;
    }

    const usersDataJSON = await usersDataResponse.json();
    usersDataJSON.forEach(user => {
        let row = tableBody.insertRow();
        row.setAttribute('class', 'userId-'+user.id)
        tableFields.forEach(tableField => {
            let cell = row.insertCell();
            cell.setAttribute('class', 'fieldName-'+tableField);
            cell.textContent = user[tableField];
        });

        let cell = row.insertCell();
        cell.setAttribute('class', 'fieldName-roles');
        cell.textContent = user.roles.map(role => role["name"]).join(" ");

        let btn = document.createElement('button');
        btn.setAttribute('type', 'button');
        btn.setAttribute('id', '#editUserButton-'+user.id);
        btn.classList.add('btn', 'btn-info');
        btn.textContent = 'Edit';
        btn.addEventListener('click', ((event) => {
            showEditModalForm(user.id);
        }));
        row.insertCell().append(btn);

        btn = document.createElement('button');
        btn.setAttribute('type', 'button');
        btn.setAttribute('id', '#deleteUserButton-'+user.id);
        btn.classList.add('btn', 'btn-danger');
        btn.textContent = 'Delete';
        btn.addEventListener('click', () => {
            showDeleteModalForm(user.id);
        });
        row.insertCell().append(btn);
    })
}

async function updateUserInfoTable(){
    const tableFields = ['id','name','lastName','age','email'];
    const tableBody = document.querySelector('#aboutUserTable').tBodies[0];
    const currentUserDataResponseURL = (tableBody.rows.length > 0) ? "http://localhost:8085/api/users/" + tableBody.rows[0].cells[0].textContent : "http://localhost:8085/api/currentUser/";

    const currentUserDataResponse = await fetch(currentUserDataResponseURL);

    if (!currentUserDataResponse.ok) {
        alert(`Bad status from response by URL ${currentUserDataResponseURL}: ${currentUserDataResponse.status} ${currentUserDataResponse.statusText}`);
        return;
    }

    const user = await currentUserDataResponse.json();
    if (tableBody.rows.length > 0) {
        tableBody.deleteRow(0);
    }
    const row = tableBody.insertRow();
    row.setAttribute('class', 'userId-' + user.id);
    tableFields.forEach(tableField => {
        let cell = row.insertCell();
        cell.setAttribute('class', 'fieldName-' + tableField);
        cell.textContent = user[tableField];
    });

    let cell = row.insertCell();
    cell.setAttribute('class', 'fieldName-roles');
    cell.textContent = user.roles.map(role => role["name"]).join(" ");
}

async function showEditModalForm(userId){

    const form = document.querySelector('#editUserForm');
    const fieldNames = ['name','lastName','age','email', 'password', 'roles'];

    fieldNames.forEach(fieldName=>{
        form[fieldName].classList.remove("err");
        //console.log(form[fieldName + 'Error']);
        document.querySelector('#' + fieldName + 'Error').innerHTML = '';
        //form[fieldName + 'Error'].innerHTML = '';
    });

    const userDataResponseURL = "http://localhost:8085/api/users/" + userId;
    const allRolesResponseURL = "http://localhost:8085/api/roles/";

    const userDataResponse = await fetch(userDataResponseURL);
    const allRolesResponse = await fetch(allRolesResponseURL);

    if (!userDataResponse.ok || !allRolesResponse.ok) {
        if (!userDataResponse.ok) {
            alert(`Bad status from response by URL ${userDataResponseURL}: ${userDataResponse.status} ${userDataResponse.statusText}`);
        }
        if (!allRolesResponse.ok) {
            alert(`Bad status from response by URL ${allRolesResponseURL}: ${allRolesResponse.status} ${allRolesResponse.statusText}`);
        }
        return;
    }

    const tableFields = ['id','name','lastName','age','email','password'];
    const userDataJSON = await userDataResponse.json();

    tableFields.forEach(tableField=>{form[tableField].value = userDataJSON[tableField]});

    Object.values(form.roles.options).forEach(optionRole=>{
        optionRole.selected = Object.values(userDataJSON.roles).map(role=>role.id.toString()).includes(optionRole.id);
    });

    $('#editUser').modal('show');
}

async function showDeleteModalForm(userId){

    const form = document.querySelector('#deleteUserForm');

    const userDataResponseURL = "http://localhost:8085/api/users/" + userId;
    const allRolesResponseURL = "http://localhost:8085/api/roles/";

    const userDataResponse = await fetch(userDataResponseURL);
    const allRolesResponse = await fetch(allRolesResponseURL);

    if (!userDataResponse.ok || !allRolesResponse.ok) {
        if (!userDataResponse.ok) {
            alert(`Bad status from response by URL ${userDataResponseURL}: ${userDataResponse.status} ${userDataResponse.statusText}`);
        }
        if (!allRolesResponse.ok) {
            alert(`Bad status from response by URL ${allRolesResponseURL}: ${allRolesResponse.status} ${allRolesResponse.statusText}`);
        }
        return;
    }

    const tableFields = ['id','name','lastName','age','email'];
    const userDataJSON = await userDataResponse.json();

    tableFields.forEach(tableField=>{form['deleteUserForm-' + tableField].value = userDataJSON[tableField]});

    Object.values(form['deleteUserForm-roles'].options).forEach(optionRole=>{
        optionRole.selected = Object.values(userDataJSON.roles).map(role=>role.id.toString()).includes(optionRole.id);
    });

    $('#deleteUser').modal('show');
}

function Role(id, name) {
    this.id = id;
    this.name = name;
}

function User(id, name, lastName, email, password, age, roles) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.age = age;
    this.roles = roles;
}

function invalidFieldsPresent(validatedFields, form){

    for (let validatedField of validatedFields) {
        if (!form[validatedField].reportValidity()) {return true}
    }

    return false;

}

async function editButtonOnclick(event){

    event.preventDefault();

    const form = document.querySelector('#editUserForm');

    const fieldNames = ['name','lastName','age','email', 'password', 'roles'];

    fieldNames.forEach(fieldName=>{
        form[fieldName].classList.remove("err");
        document.querySelector('#' + fieldName + 'Error').innerHTML = '';
    });

    const validatedFields = ['name','lastName','age','email','password', 'roles'];

    if (invalidFieldsPresent(validatedFields, form)) {
        return;
    }

    const userEditResponseURL = "http://localhost:8085/api/users/";

    const user = new User(form.id.value, form.name.value, form.lastName.value, form.email.value, form.password.value, form.age.value,
        Array.from(form.roles).filter(optionRole=>optionRole.selected).map(optionRole=>new Role(optionRole.id, optionRole.text)));

    const userEditResponse = await fetch(userEditResponseURL, {
        method: "put",
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(user)
        });

    if (!userEditResponse.ok) {

        if (userEditResponse.status == 422) {
            const usersEditResponseJSON = await userEditResponse.json();
            usersEditResponseJSON.errors.forEach(error=>{
                if (!form[error.fieldName].classList.contains("err")) {form[error.fieldName].classList.add("err")};
                document.querySelector('#' + error.fieldName + 'Error').innerHTML += ((document.querySelector('#' + error.fieldName + 'Error').innerHTML == '') ? '' : '\r\n') + error.message;
            })

        } else {
            alert(`Bad status from response by URL ${userEditResponseURL}: ${userEditResponse.status} ${userEditResponse.statusText}`);
        }
        return;
    }

    const userDataJSON = await userEditResponse.json();
    const rowFieldNames = ['name','lastName','age','email', 'roles'];
    const row = document.querySelector('#usersTable').tBodies[0].querySelector('.userId-'+form.id.value);
    rowFieldNames.forEach(fieldName => {row.querySelector('.fieldName-'+fieldName).textContent = (fieldName == 'roles') ? userDataJSON[fieldName].map(role => role["name"]).join(" ") : userDataJSON[fieldName]});

    $('#editUser').modal('hide');
}

async function deleteButtonOnclick(event){

    event.preventDefault();

    const form = document.querySelector('#deleteUserForm');

    const userDeleteResponseURL = "http://localhost:8085/api/users/" + form.id.value;

    const userDeleteResponse = await fetch(userDeleteResponseURL, {
        method: "delete",
    });

    if (!userDeleteResponse.ok) {
        const userDeleteResponseJSON = await userDeleteResponse.json();
        alert(`Bad status from response by URL ${userDeleteResponseURL}: ${userDeleteResponse.status} ${userDeleteResponseJSON.info}`);
        return;
    }

    const tableBody = document.querySelector('#usersTable').tBodies[0];
    const row = tableBody.querySelector('.userId-'+form.id.value);
    tableBody.removeChild(row);

    $('#deleteUser').modal('hide');

    //analyze deletingResult
    const userDeleteResponseJSON = await userDeleteResponse.json();
    if (userDeleteResponseJSON.currentUserDeleted) {
        window.location.replace("http://localhost:8085/login");
    }
}

async function addNewUserButtonOnclick(event){

    event.preventDefault();

    const form = document.querySelector('#addNewUserForm');
    const fieldNames = ['name','lastName','age','email', 'password', 'roles'];

    fieldNames.forEach(fieldName=>{
        form[fieldName].classList.remove("err");
        document.querySelector('#addNewUser-' + fieldName + 'Error').innerHTML = '';
    });

    const validatedFields = ['name','lastName','age','email','password', 'roles'];

    if (invalidFieldsPresent(validatedFields, form)) {
        return;
    }

    const addNewUserResponseURL = "http://localhost:8085/api/users/";

    let user = new User(null, form.name.value, form.lastName.value, form.email.value, form.password.value, form.age.value,
        Array.from(form.roles).filter(optionRole=>optionRole.selected).map(optionRole=>new Role(optionRole.id, optionRole.text)));

    const addNewUserResponse = await fetch(addNewUserResponseURL, {
        method: "post",
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(user)
    });

    if (!addNewUserResponse.ok) {

        if (addNewUserResponse.status == 422) {
            const addNewUserResponseJSON = await addNewUserResponse.json();
            console.log(addNewUserResponseJSON);
            addNewUserResponseJSON.errors.forEach(error=>{
                if (!form[error.fieldName].classList.contains("err")) {form[error.fieldName].classList.add("err")};
                document.querySelector('#addNewUser-' + error.fieldName + 'Error').innerHTML += ((document.querySelector('#addNewUser-' + error.fieldName + 'Error').innerHTML == '') ? '' : '\r\n') + error.message;
            })

        } else {
            alert(`Bad status from response by URL ${addNewUserResponseURL}: ${addNewUserResponse.status} ${addNewUserResponse.statusText}`);
        }
        return;
    }

    user = await addNewUserResponse.json();
    const rowFieldNames = ['id','name','lastName','age','email'];
    const tableBody = document.querySelector('#usersTable').tBodies[0];

    let row = tableBody.insertRow();
    row.setAttribute('class', 'userId-'+user.id)
    rowFieldNames.forEach(tableField => {
        let cell = row.insertCell();
        cell.setAttribute('class', 'fieldName-'+tableField);
        cell.textContent = user[tableField];
    });

    let cell = row.insertCell();
    cell.setAttribute('class', 'fieldName-roles');
    cell.textContent = user.roles.map(role => role["name"]).join(" ");

    let btn = document.createElement('button');
    btn.setAttribute('type', 'button');
    btn.setAttribute('id', '#editUserButton-'+user.id);
    btn.classList.add('btn', 'btn-info');
    btn.textContent = 'Edit';
    //let eventEdit = new CustomEvent('onclickEditButton', {detail: {userId: user.id}});
    btn.addEventListener('click', ((event) => {
        showEditModalForm(user.id);
    }));
    row.insertCell().append(btn);

    btn = document.createElement('button');
    btn.setAttribute('type', 'button');
    btn.setAttribute('id', '#deleteUserButton-'+user.id);
    btn.classList.add('btn', 'btn-danger');
    btn.textContent = 'Delete';
    btn.addEventListener('click', () => {
        showDeleteModalForm(user.id);
    });
    row.insertCell().append(btn);

    fieldNames.forEach(fieldName=> {
        form[fieldName].value = "";
    });

    document.querySelector('#home-tab').setAttribute('aria-selected', "true");
    document.querySelector('#profile-tab').setAttribute('aria-selected', "false");

    document.querySelector('#profile-tab').classList.remove('active');
    document.querySelector('#home-tab').classList.add('active');

    document.querySelector('#new-user').classList.remove('active');
    document.querySelector('#new-user').classList.remove('show');
    document.querySelector('#users-table').classList.add('active');
    document.querySelector('#users-table').classList.add('show');
}

async function fillNecessaryTables(){

    const userRoleCheckURL = "http://localhost:8085/api/currentUserIsAdmin/";

    const userRoleCheckResponse = await fetch(userRoleCheckURL);

    if (!userRoleCheckResponse.ok) {
        alert(`Bad status from response by URL ${userRoleCheckURL}: ${userRoleCheckResponse.status} ${userRoleCheckResponse.statusText}`);

        return;
    }

    const currentUserIsAdmin = await userRoleCheckResponse.json();

    if (currentUserIsAdmin) {
        addRowsToUsersTable();
    }

    updateUserInfoTable();
}

document.addEventListener("DOMContentLoaded", () =>{
    fillNecessaryTables();
});

const editUserForm = document.querySelector('#editUserForm');

editUserForm["editButton"].addEventListener('click', (event)=>{
    editButtonOnclick(event);
})

const deleteUserForm = document.querySelector('#deleteUserForm');

deleteUserForm["deleteButton"].addEventListener('click', (event)=>{
    deleteButtonOnclick(event);
})

const addNewUserForm = document.querySelector('#addNewUserForm');

addNewUserForm["addNewUserButton"].addEventListener('click', (event)=>{
    addNewUserButtonOnclick(event);
})

document.querySelector('#v-pills-profile-tab').addEventListener('click', (event)=>{
    updateUserInfoTable();
})