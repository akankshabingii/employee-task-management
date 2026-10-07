<template>
  <div class="users-page">

    <!-- Header -->
    <div class="page-header">
      <div>
        <p class="eyebrow">ADMINISTRATION</p>
        <h1>Users</h1>
        <p class="subtitle">
          Manage workspace users, roles and team assignments.
        </p>
      </div>

      <button class="add-btn" @click="openCreateModal">
        <span>+</span>
        Add user
      </button>
    </div>

    <!-- Stats -->
    <div class="stats-grid">

      <div class="stat-card">
        <div class="stat-icon">♙</div>
        <div>
          <span>Total users</span>
          <strong>{{ users.length }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">◆</div>
        <div>
          <span>Admins</span>
          <strong>{{ adminCount }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">◇</div>
        <div>
          <span>Managers</span>
          <strong>{{ managerCount }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">○</div>
        <div>
          <span>Employees</span>
          <strong>{{ employeeCount }}</strong>
        </div>
      </div>

    </div>

    <!-- Search + Filter -->
    <div class="toolbar">

      <div class="search-box">
        <span>⌕</span>

        <input
          v-model="search"
          type="text"
          placeholder="Search users..."
        />
      </div>

      <select v-model="roleFilter">
        <option value="ALL">All roles</option>
        <option value="ADMIN">Admins</option>
        <option value="MANAGER">Managers</option>
        <option value="EMPLOYEE">Employees</option>
      </select>

    </div>

    <!-- Error -->
    <div v-if="error" class="error-box">
      {{ error }}
    </div>

    <!-- Loading -->
    <div v-if="loading" class="state-box">
      Loading users...
    </div>

    <!-- Empty -->
    <div v-else-if="filteredUsers.length === 0" class="state-box">
      <div class="empty-icon">♙</div>

      <h3>No users found</h3>

      <p>
        Try changing your search or role filter.
      </p>
    </div>

    <!-- Users Table -->
    <div v-else class="table-card">

      <table>

        <thead>
          <tr>
            <th>USER</th>
            <th>ROLE</th>
            <th>ORGANIZATION</th>
            <th>ACTIONS</th>
          </tr>
        </thead>

        <tbody>

          <tr
            v-for="user in filteredUsers"
            :key="user.id"
          >

            <!-- User -->
            <td>

              <div class="user-cell">

                <div class="avatar">
                  {{ getInitials(user.name) }}
                </div>

                <div class="user-info">

                  <strong>
                    {{ user.name }}
                  </strong>

                  <span>
                    {{ user.email }}
                  </span>

                  <small>
                    ID: {{ user.id }}
                  </small>

                </div>

              </div>

            </td>

            <!-- Role -->
            <td>

              <span
                class="role-badge"
                :class="getRoleClass(user.role)"
              >
                {{ user.role }}
              </span>

            </td>

            <!-- Organization -->
            <td>

              <div class="organization">

                <!-- ADMIN -->
                <template v-if="user.role === 'ADMIN'">

                  <span class="no-org">
                    —
                  </span>

                </template>

                <!-- MANAGER / EMPLOYEE -->
                <template v-else>

                  <div
                    v-if="user.teamId"
                    class="org-line"
                  >
                    <span class="org-label">
                      Team
                    </span>

                    <span class="org-value">
                      ID: {{ user.teamId }}
                    </span>
                  </div>

                  <div
                    v-if="
                      user.role === 'EMPLOYEE' &&
                      user.managerId
                    "
                    class="org-line"
                  >
                    <span class="org-label">
                      Manager
                    </span>

                    <span class="org-value">
                      ID: {{ user.managerId }}
                    </span>
                  </div>

                  <span
                    v-if="
                      !user.teamId &&
                      !user.managerId
                    "
                    class="no-org"
                  >
                    Not assigned
                  </span>

                </template>

              </div>

            </td>

            <!-- Actions -->
            <td>

              <div class="actions">

                <button
                  class="action-btn edit"
                  title="Edit user"
                  @click="openEditModal(user)"
                >
                  ✎
                </button>

                <button
                  class="action-btn delete"
                  title="Delete user"
                  @click="deleteUser(user)"
                >
                  ×
                </button>

              </div>

            </td>

          </tr>

        </tbody>

      </table>

    </div>


    <!-- ADD / EDIT MODAL -->

    <div
      v-if="showModal"
      class="modal-overlay"
      @click.self="closeModal"
    >

      <div class="modal">

        <div class="modal-header">

          <div>

            <p class="eyebrow">
              {{ editingUser ? 'EDIT USER' : 'NEW USER' }}
            </p>

            <h2>
              {{ editingUser ? 'Edit user' : 'Add user' }}
            </h2>

          </div>

          <button
            class="close-btn"
            @click="closeModal"
          >
            ×
          </button>

        </div>


        <form @submit.prevent="saveUser">

          <!-- Name -->
          <div class="form-group">

            <label>Name</label>

            <input
              v-model="form.name"
              type="text"
              placeholder="Enter full name"
              required
            />

          </div>


          <!-- Email -->
          <div class="form-group">

            <label>Email</label>

            <input
              v-model="form.email"
              type="email"
              placeholder="Enter email address"
              required
            />

          </div>


          <!-- Password -->
          <div class="form-group">

            <label>Password</label>

            <input
              v-model="form.password"
              type="password"
              placeholder="Enter password"
              required
            />

          </div>


          <!-- Role -->
          <div class="form-group">

            <label>Role</label>

            <select
              v-model="form.role"
              required
            >

              <option value="ADMIN">
                ADMIN
              </option>

              <option value="MANAGER">
                MANAGER
              </option>

              <option value="EMPLOYEE">
                EMPLOYEE
              </option>

            </select>

          </div>


          <!-- Team -->
          <div class="form-group">

            <label>Team ID</label>

            <input
              v-model.number="form.teamId"
              type="number"
              placeholder="Example: 13"
            />

            <small>
              Required for managers and employees.
            </small>

          </div>


          <!-- Manager -->
          <div
            v-if="form.role === 'EMPLOYEE'"
            class="form-group"
          >

            <label>Manager ID</label>

            <input
              v-model.number="form.managerId"
              type="number"
              placeholder="Example: 17"
            />

            <small>
              Required for employees.
            </small>

          </div>


          <!-- Form error -->
          <div
            v-if="formError"
            class="form-error"
          >
            {{ formError }}
          </div>


          <!-- Buttons -->
          <div class="modal-actions">

            <button
              type="button"
              class="cancel-btn"
              @click="closeModal"
            >
              Cancel
            </button>

            <button
              type="submit"
              class="save-btn"
              :disabled="saving"
            >
              {{
                saving
                  ? 'Saving...'
                  : editingUser
                    ? 'Save changes'
                    : 'Create user'
              }}
            </button>

          </div>

        </form>

      </div>

    </div>

  </div>
</template>


<script setup>

import {
  computed,
  onMounted,
  reactive,
  ref
} from 'vue'

import api from '../services/api'


/* -------------------------
   State
------------------------- */

const users = ref([])

const loading = ref(false)

const saving = ref(false)

const error = ref('')

const formError = ref('')

const search = ref('')

const roleFilter = ref('ALL')

const showModal = ref(false)

const editingUser = ref(null)


/* -------------------------
   Form
------------------------- */

const form = reactive({

  name: '',

  email: '',

  password: '',

  role: 'EMPLOYEE',

  teamId: null,

  managerId: null

})


/* -------------------------
   Statistics
------------------------- */

const adminCount = computed(() => {

  return users.value.filter(
    user => user.role === 'ADMIN'
  ).length

})


const managerCount = computed(() => {

  return users.value.filter(
    user => user.role === 'MANAGER'
  ).length

})


const employeeCount = computed(() => {

  return users.value.filter(
    user => user.role === 'EMPLOYEE'
  ).length

})


/* -------------------------
   Search / Filter
------------------------- */

const filteredUsers = computed(() => {

  const query =
    search.value
      .trim()
      .toLowerCase()


  return users.value.filter(user => {

    const matchesSearch =
      !query ||
      user.name
        ?.toLowerCase()
        .includes(query) ||
      user.email
        ?.toLowerCase()
        .includes(query) ||
      String(user.id)
        .includes(query)


    const matchesRole =
      roleFilter.value === 'ALL' ||
      user.role === roleFilter.value


    return (
      matchesSearch &&
      matchesRole
    )

  })

})


/* -------------------------
   Load users
------------------------- */

async function loadUsers() {

  loading.value = true

  error.value = ''

  try {

    const response =
      await api.get('/users')

    users.value =
      response.data

  } catch (e) {

    console.error(
      'Failed to load users:',
      e
    )

    if (e.response?.status === 403) {

      error.value =
        'You do not have permission to view users.'

    } else {

      error.value =
        'Failed to load users. Please try again.'

    }

  } finally {

    loading.value = false

  }

}


/* -------------------------
   Reset form
------------------------- */

function resetForm() {

  form.name = ''

  form.email = ''

  form.password = ''

  form.role = 'EMPLOYEE'

  form.teamId = null

  form.managerId = null

  formError.value = ''

}


/* -------------------------
   Create user
------------------------- */

function openCreateModal() {

  editingUser.value = null

  resetForm()

  showModal.value = true

}


/* -------------------------
   Edit user
------------------------- */

function openEditModal(user) {

  editingUser.value = user

  form.name = user.name

  form.email = user.email

  form.password = ''

  form.role = user.role

  form.teamId =
    user.teamId ?? null

  form.managerId =
    user.managerId ?? null

  formError.value = ''

  showModal.value = true

}


/* -------------------------
   Close modal
------------------------- */

function closeModal() {

  if (saving.value) {
    return
  }

  showModal.value = false

  editingUser.value = null

  formError.value = ''

}


/* -------------------------
   Save user
------------------------- */

async function saveUser() {

  saving.value = true

  formError.value = ''

  try {

    const payload = {

      name: form.name,

      email: form.email,

      password: form.password,

      role: form.role,

      teamId:
        form.teamId || null,

      managerId:
        form.role === 'EMPLOYEE'
          ? form.managerId || null
          : null

    }


    if (editingUser.value) {

      await api.put(
        `/users/${editingUser.value.id}`,
        payload
      )

    } else {

      await api.post(
        '/users',
        payload
      )

    }


    closeModal()

    await loadUsers()

  } catch (e) {

    console.error(
      'Failed to save user:',
      e
    )

    formError.value =
      e.response?.data?.message ||
      e.response?.data ||
      'Unable to save user. Please check the entered details.'

  } finally {

    saving.value = false

  }

}


/* -------------------------
   Delete user
------------------------- */

async function deleteUser(user) {

  const confirmed =
    window.confirm(
      `Are you sure you want to delete ${user.name}?`
    )


  if (!confirmed) {
    return
  }


  try {

    await api.delete(
      `/users/${user.id}`
    )

    await loadUsers()

  } catch (e) {

    console.error(
      'Failed to delete user:',
      e
    )

    alert(
      e.response?.data?.message ||
      'Unable to delete this user.'
    )

  }

}


/* -------------------------
   Helpers
------------------------- */

function getInitials(name) {

  if (!name) {
    return '?'
  }


  return name
    .split(' ')
    .map(part => part.charAt(0))
    .slice(0, 2)
    .join('')
    .toUpperCase()

}


function getRoleClass(role) {

  return {

    admin:
      role === 'ADMIN',

    manager:
      role === 'MANAGER',

    employee:
      role === 'EMPLOYEE'

  }

}


/* -------------------------
   Initial load
------------------------- */

onMounted(() => {

  loadUsers()

})

</script>


<style scoped>

.users-page {

  padding: 36px 42px 50px;

  min-height: 100vh;

  background: #f7f8f6;

}


/* Header */

.page-header {

  display: flex;

  justify-content: space-between;

  align-items: flex-end;

  margin-bottom: 30px;

}

.eyebrow {

  margin: 0 0 7px;

  font-size: 11px;

  font-weight: 700;

  letter-spacing: 1.8px;

  color: #8a918b;

}

h1 {

  margin: 0;

  font-size: 32px;

  line-height: 1.1;

  color: #20251f;

  letter-spacing: -0.8px;

}

.subtitle {

  margin: 9px 0 0;

  color: #858b85;

  font-size: 14px;

}

.add-btn {

  border: none;

  background: #263b2c;

  color: white;

  padding: 12px 18px;

  border-radius: 11px;

  font-size: 13px;

  font-weight: 600;

  cursor: pointer;

  display: flex;

  align-items: center;

  gap: 8px;

}

.add-btn:hover {

  background: #1d3023;

}

.add-btn span {

  font-size: 20px;

}


/* Stats */

.stats-grid {

  display: grid;

  grid-template-columns:
    repeat(4, 1fr);

  gap: 16px;

  margin-bottom: 24px;

}

.stat-card {

  background: white;

  border: 1px solid #e7ebe6;

  border-radius: 15px;

  padding: 19px;

  display: flex;

  align-items: center;

  gap: 14px;

}

.stat-icon {

  width: 42px;

  height: 42px;

  border-radius: 11px;

  display: flex;

  align-items: center;

  justify-content: center;

  background: #eef2ed;

  color: #304634;

  font-size: 18px;

}

.stat-card span {

  display: block;

  color: #8a908a;

  font-size: 12px;

  margin-bottom: 4px;

}

.stat-card strong {

  color: #20251f;

  font-size: 22px;

}


/* Toolbar */

.toolbar {

  display: flex;

  justify-content: space-between;

  gap: 14px;

  margin-bottom: 16px;

}

.search-box {

  width: 360px;

  height: 43px;

  background: white;

  border: 1px solid #e3e7e2;

  border-radius: 10px;

  display: flex;

  align-items: center;

  padding: 0 13px;

  gap: 9px;

}

.search-box span {

  color: #929892;

  font-size: 20px;

}

.search-box input {

  border: none;

  outline: none;

  width: 100%;

  font-size: 13px;

  color: #303630;

}

.toolbar select {

  height: 43px;

  min-width: 145px;

  padding: 0 12px;

  border: 1px solid #e3e7e2;

  border-radius: 10px;

  background: white;

  color: #555c55;

  outline: none;

}


/* Table */

.table-card {

  background: white;

  border: 1px solid #e5e9e4;

  border-radius: 15px;

  overflow: hidden;

}

table {

  width: 100%;

  border-collapse: collapse;

}

thead {

  background: #fafbf9;

}

th {

  text-align: left;

  padding: 14px 20px;

  font-size: 10px;

  letter-spacing: 1.2px;

  color: #939993;

  font-weight: 700;

}

td {

  padding: 17px 20px;

  border-top: 1px solid #edf0ec;

  vertical-align: middle;

}

tbody tr:hover {

  background: #fcfdfb;

}


/* User */

.user-cell {

  display: flex;

  align-items: center;

  gap: 12px;

}

.avatar {

  width: 39px;

  height: 39px;

  border-radius: 50%;

  background: #e4ebe3;

  color: #314936;

  display: flex;

  align-items: center;

  justify-content: center;

  font-size: 12px;

  font-weight: 700;

  flex-shrink: 0;

}

.user-info strong {

  display: block;

  color: #252a25;

  font-size: 13px;

  margin-bottom: 3px;

}

.user-info span {

  display: block;

  color: #777e77;

  font-size: 12px;

}

.user-info small {

  display: block;

  color: #a0a6a0;

  font-size: 10px;

  margin-top: 3px;

}


/* Role */

.role-badge {

  display: inline-flex;

  align-items: center;

  padding: 6px 9px;

  border-radius: 7px;

  font-size: 10px;

  font-weight: 700;

  letter-spacing: .5px;

}

.role-badge.admin {

  background: #eee9f8;

  color: #70559a;

}

.role-badge.manager {

  background: #e8f0f6;

  color: #4f718c;

}

.role-badge.employee {

  background: #edf3e9;

  color: #56714d;

}


/* Organization */

.organization {

  display: flex;

  flex-direction: column;

  gap: 5px;

}

.org-line {

  display: flex;

  align-items: center;

  gap: 9px;

}

.org-label {

  min-width: 55px;

  color: #969d96;

  font-size: 11px;

}

.org-value {

  color: #3c463c;

  font-size: 12px;

  font-weight: 600;

}

.no-org {

  color: #a0a6a0;

  font-size: 12px;

}


/* Actions */

.actions {

  display: flex;

  gap: 7px;

}

.action-btn {

  width: 32px;

  height: 32px;

  border: 1px solid #e4e8e3;

  background: white;

  border-radius: 8px;

  cursor: pointer;

  font-size: 14px;

}

.action-btn.edit {

  color: #516253;

}

.action-btn.edit:hover {

  background: #edf2ed;

}

.action-btn.delete {

  color: #a36b6b;

}

.action-btn.delete:hover {

  background: #f8eeee;

}


/* States */

.state-box {

  background: white;

  border: 1px solid #e5e9e4;

  border-radius: 15px;

  padding: 60px 20px;

  text-align: center;

  color: #858b85;

}

.empty-icon {

  font-size: 30px;

  color: #9ba39b;

  margin-bottom: 10px;

}

.state-box h3 {

  margin: 0 0 5px;

  color: #343a34;

  font-size: 16px;

}

.state-box p {

  margin: 0;

  font-size: 13px;

}

.error-box {

  background: #fff3f3;

  border: 1px solid #f0d8d8;

  color: #a45e5e;

  padding: 12px 15px;

  border-radius: 10px;

  margin-bottom: 16px;

  font-size: 13px;

}


/* Modal */

.modal-overlay {

  position: fixed;

  inset: 0;

  background: rgba(24, 30, 25, 0.42);

  display: flex;

  align-items: center;

  justify-content: center;

  padding: 20px;

  z-index: 1000;

}

.modal {

  width: 480px;

  max-width: 100%;

  max-height: 90vh;

  overflow-y: auto;

  background: white;

  border-radius: 18px;

  padding: 27px;

  box-shadow:
    0 25px 70px rgba(0, 0, 0, .16);

}

.modal-header {

  display: flex;

  justify-content: space-between;

  align-items: flex-start;

  margin-bottom: 25px;

}

.modal-header h2 {

  margin: 0;

  font-size: 22px;

  color: #242a24;

}

.close-btn {

  border: none;

  background: #f2f4f1;

  width: 32px;

  height: 32px;

  border-radius: 8px;

  cursor: pointer;

  font-size: 19px;

  color: #697069;

}


/* Form */

.form-group {

  margin-bottom: 17px;

}

.form-group label {

  display: block;

  margin-bottom: 7px;

  color: #454b45;

  font-size: 12px;

  font-weight: 600;

}

.form-group input,
.form-group select {

  width: 100%;

  box-sizing: border-box;

  height: 42px;

  border: 1px solid #dfe4de;

  border-radius: 9px;

  padding: 0 12px;

  outline: none;

  font-size: 13px;

  color: #343934;

  background: white;

}

.form-group input:focus,
.form-group select:focus {

  border-color: #728672;

  box-shadow:
    0 0 0 3px
    rgba(114, 134, 114, .09);

}

.form-group small {

  display: block;

  color: #999f99;

  font-size: 10px;

  margin-top: 5px;

}

.form-error {

  background: #fff2f2;

  color: #a45c5c;

  border: 1px solid #efd7d7;

  border-radius: 8px;

  padding: 10px 12px;

  font-size: 12px;

  margin-bottom: 15px;

}


/* Modal buttons */

.modal-actions {

  display: flex;

  justify-content: flex-end;

  gap: 9px;

  margin-top: 25px;

}

.cancel-btn,
.save-btn {

  height: 40px;

  padding: 0 16px;

  border-radius: 9px;

  font-size: 12px;

  font-weight: 600;

  cursor: pointer;

}

.cancel-btn {

  background: white;

  border: 1px solid #dfe4de;

  color: #626a62;

}

.save-btn {

  border: none;

  background: #263b2c;

  color: white;

}

.save-btn:hover {

  background: #1d3023;

}

.save-btn:disabled {

  opacity: .6;

  cursor: not-allowed;

}


/* Responsive */

@media (max-width: 900px) {

  .stats-grid {

    grid-template-columns:
      repeat(2, 1fr);

  }

  .users-page {

    padding: 25px;

  }

  .table-card {

    overflow-x: auto;

  }

  table {

    min-width: 750px;

  }

}

@media (max-width: 600px) {

  .page-header {

    align-items: flex-start;

    flex-direction: column;

    gap: 18px;

  }

  .stats-grid {

    grid-template-columns: 1fr;

  }

  .toolbar {

    flex-direction: column;

  }

  .search-box {

    width: auto;

  }

}

</style>