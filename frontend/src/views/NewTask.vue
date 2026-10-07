
<template>
  <div class="page">

    <!-- HEADER -->
    <header class="page-header">
      <div>
        <div class="eyebrow">Workspace</div>

        <h1 class="page-title">Create a new task</h1>

        <p class="page-subtitle">
          Assign work, set expectations, and keep your team moving.
        </p>
      </div>

      <button
        class="secondary-button"
        @click="router.push('/tasks')"
      >
        ← Back to tasks
      </button>
    </header>


    <!-- FORM -->
    <div class="form-card">

      <form @submit.prevent="createTask">

        <!-- TASK DETAILS -->
        <div class="form-section">

          <div class="section-heading">
            <h2>Task details</h2>

            <p>
              Define what needs to be completed.
            </p>
          </div>


          <div class="form-grid">

            <!-- TITLE -->
            <div class="form-group full">

              <label>Task title</label>

              <input
                v-model="form.title"
                type="text"
                placeholder="e.g. Complete API integration"
                required
              />

            </div>


            <!-- DESCRIPTION -->
            <div class="form-group full">

              <label>Description</label>

              <textarea
                v-model="form.description"
                rows="4"
                placeholder="Add context, requirements, or notes..."
              ></textarea>

            </div>


            <!-- PRIORITY -->
            <div class="form-group">

              <label>Priority</label>

              <select
                v-model="form.priority"
                required
              >
                <option value="HIGH">
                  High
                </option>

                <option value="MEDIUM">
                  Medium
                </option>

                <option value="LOW">
                  Low
                </option>
              </select>

            </div>


            <!-- STATUS -->
            <div class="form-group">

              <label>Status</label>

              <select
                v-model="form.status"
                required
              >
                <option value="PENDING">
                  Pending
                </option>

                <option value="IN_PROGRESS">
                  In progress
                </option>

                <option value="COMPLETED">
                  Completed
                </option>
              </select>

            </div>

          </div>

        </div>


        <!-- SCHEDULE -->
        <div class="form-section">

          <div class="section-heading">

            <h2>Schedule</h2>

            <p>
              Set when the task should be worked on.
            </p>

          </div>


          <div class="form-grid">

            <!-- START DATE -->
            <div class="form-group">

              <label>Start date</label>

              <input
                v-model="form.startDate"
                type="date"
              />

            </div>


            <!-- END DATE -->
            <div class="form-group">

              <label>Due date</label>

              <input
                v-model="form.endDate"
                type="date"
              />

            </div>

          </div>

        </div>


        <!-- ASSIGNMENT -->
        <div class="form-section">

          <div class="section-heading">

            <h2>Assignment</h2>

            <p>
              Select the employee responsible for this task.
            </p>

          </div>


          <div class="form-grid">

            <!-- EMPLOYEE -->
            <div class="form-group">

              <label>Employee</label>

              <select
                v-model="form.employeeId"
                required
                @change="handleEmployeeChange"
              >

                <option
                  value=""
                  disabled
                >
                  Select employee
                </option>

                <option
                  v-for="employee in availableEmployees"
                  :key="employee.id"
                  :value="employee.id"
                >
                  {{ employee.name }}
                </option>

              </select>


              <small
                v-if="!availableEmployees.length"
                class="form-hint"
              >
                No employees available for assignment.
              </small>

            </div>


            <!-- MANAGER -->
            <div class="form-group">

              <label>Manager</label>

              <select
                v-model="form.managerId"
                required
                :disabled="role === 'MANAGER'"
              >

                <option
                  value=""
                  disabled
                >
                  Select manager
                </option>

                <option
                  v-for="manager in availableManagers"
                  :key="manager.id"
                  :value="manager.id"
                >
                  {{ manager.name }}
                </option>

              </select>


              <small
                v-if="role === 'MANAGER'"
                class="form-hint"
              >
                Tasks created by you are assigned under your account.
              </small>

            </div>

          </div>

        </div>


        <!-- PROGRESS -->
        <div class="form-section">

          <div class="section-heading">

            <h2>Progress</h2>

            <p>
              Set the initial completion percentage.
            </p>

          </div>


          <div class="progress-input">

            <input
              v-model.number="form.progress"
              type="range"
              min="0"
              max="100"
            />

            <span>
              {{ form.progress }}%
            </span>

          </div>

        </div>


        <!-- ERROR -->
        <div
          v-if="errorMessage"
          class="form-message error"
        >
          {{ errorMessage }}
        </div>


        <!-- ACTIONS -->
        <div class="form-actions">

          <button
            type="button"
            class="secondary-button"
            @click="router.push('/tasks')"
          >
            Cancel
          </button>


          <button
            type="submit"
            class="primary-button"
            :disabled="loading"
          >
            {{ loading ? 'Creating…' : 'Create task' }}
          </button>

        </div>

      </form>

    </div>

  </div>
</template>


<script setup>

import {
  ref,
  computed,
  onMounted
} from 'vue'

import {
  useRouter
} from 'vue-router'

import api from '../services/api'


const router = useRouter()


/*
 * Logged-in user's role.
 */
const role =
  localStorage.getItem('role')


const currentUserId =
  Number(localStorage.getItem('userId'))


const loading =
  ref(false)


const errorMessage =
  ref('')


const users =
  ref([])


/*
 * Form state.
 */
const form = ref({

  title: '',

  priority: 'MEDIUM',

  progress: 0,

  status: 'PENDING',

  description: '',

  startDate: '',

  endDate: '',

  employeeId: '',

  managerId: ''

})


/*
 * ADMIN:
 * all managers from /users
 *
 * MANAGER:
 * only the logged-in manager
 */
const availableManagers =
  computed(() => {

    if (role === 'ADMIN') {

      return users.value.filter(
        user =>
          user.role === 'MANAGER'
      )

    }

    if (role === 'MANAGER') {

      return users.value.filter(
        user =>
          Number(user.id) === currentUserId
      )

    }

    return []

  })


/*
 * Employees available for assignment.
 */
const availableEmployees =
  computed(() => {

    return users.value.filter(
      user =>
        user.role === 'EMPLOYEE'
    )

  })


/*
 * ADMIN:
 *
 * GET /users
 *
 * gives all users.
 *
 *
 * MANAGER:
 *
 * GET /users/{managerId}/employees
 *
 * gives only that manager's employees.
 */
async function loadUsers() {

  try {

    if (role === 'ADMIN') {

      const response =
        await api.get('/users')

      users.value =
        response.data || []

      return
    }


    if (role === 'MANAGER') {

      /*
       * Get current manager.
       */
      const managerResponse =
        await api.get(
          `/users/${currentUserId}`
        )


      /*
       * Get employees belonging
       * to this manager.
       */
      const employeesResponse =
        await api.get(
          `/users/${currentUserId}/employees`
        )


      /*
       * Store manager + employees
       * together so the existing
       * computed properties work.
       */
      users.value = [

        managerResponse.data,

        ...(employeesResponse.data || [])

      ]


      /*
       * Manager automatically
       * becomes task manager.
       */
      form.value.managerId =
        currentUserId

      return
    }


  } catch (error) {

    console.error(
      'Failed to load assignment data:',
      error
    )

    errorMessage.value =
      error.response?.status === 403

        ? 'You do not have permission to load assignment data.'

        : 'Unable to load employees. Please try again.'

  }

}


/*
 * ADMIN:
 *
 * When an employee is selected,
 * automatically select their manager.
 */
function handleEmployeeChange() {

  if (role !== 'ADMIN') {
    return
  }


  const employee =
    users.value.find(
      user =>
        Number(user.id) ===
        Number(form.value.employeeId)
    )


  if (
    employee &&
    employee.managerId
  ) {

    form.value.managerId =
      employee.managerId

  }

}


/*
 * Create task.
 */
async function createTask() {

  errorMessage.value = ''


  if (!form.value.title.trim()) {

    errorMessage.value =
      'Task title is required.'

    return

  }


  if (!form.value.employeeId) {

    errorMessage.value =
      'Please select an employee.'

    return

  }


  if (!form.value.managerId) {

    errorMessage.value =
      'Please select a manager.'

    return

  }


  /*
   * Validate dates.
   */
  if (
    form.value.startDate &&
    form.value.endDate &&
    form.value.endDate <
    form.value.startDate
  ) {

    errorMessage.value =
      'Due date cannot be before the start date.'

    return

  }


  loading.value = true


  try {

    await api.post(
      '/tasks',
      {

        title:
          form.value.title.trim(),

        priority:
          form.value.priority,

        progress:
          form.value.progress,

        status:
          form.value.status,

        description:
          form.value.description.trim()
            || null,

        startDate:
          form.value.startDate
            || null,

        endDate:
          form.value.endDate
            || null,

        employeeId:
          Number(
            form.value.employeeId
          ),

        managerId:
          Number(
            form.value.managerId
          )

      }
    )


    /*
     * Success → return
     * to task list.
     */
    router.push('/tasks')


  } catch (error) {

    console.error(
      'Task creation failed:',
      error
    )


    errorMessage.value =
      error.response?.data?.message ||
      'Unable to create task. Please try again.'


  } finally {

    loading.value = false

  }

}


onMounted(() => {

  /*
   * Employees should never
   * create tasks.
   *
   * Backend enforces this too.
   */
  if (role === 'EMPLOYEE') {

    router.replace('/tasks')

    return

  }


  loadUsers()

})

</script>


<style scoped>

.form-card {
  background: #ffffff;
  border: 1px solid #e8ece8;
  border-radius: 20px;
  padding: 30px;
  max-width: 900px;
  box-shadow: 0 8px 30px rgba(30, 60, 40, 0.05);
}

.form-section {
  padding: 8px 0 28px;
  margin-bottom: 28px;
  border-bottom: 1px solid #edf0ed;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.section-heading {
  margin-bottom: 20px;
}

.section-heading h2 {
  margin: 0 0 5px;
  font-size: 18px;
  color: #1d2a20;
}

.section-heading p {
  margin: 0;
  font-size: 13px;
  color: #7a857d;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group.full {
  grid-column: 1 / -1;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #354238;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #dfe5df;
  border-radius: 10px;
  padding: 12px 13px;
  font: inherit;
  color: #243027;
  background: #fbfcfb;
  outline: none;
  transition: 0.2s;
}

.form-group textarea {
  resize: vertical;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #78947d;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(90, 120, 95, 0.08);
}

.form-group select:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.form-hint {
  font-size: 12px;
  color: #7c887f;
}

.progress-input {
  display: flex;
  align-items: center;
  gap: 18px;
}

.progress-input input {
  flex: 1;
  accent-color: #496b52;
}

.progress-input span {
  min-width: 48px;
  font-weight: 700;
  color: #35533d;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.form-message {
  padding: 12px 14px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 13px;
}

.form-message.error {
  background: #fff1f0;
  border: 1px solid #f2cfcb;
  color: #a23d35;
}

.primary-button,
.secondary-button {
  border-radius: 10px;
  padding: 11px 17px;
  font: inherit;
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.primary-button {
  background: #304f38;
  color: white;
}

.primary-button:hover {
  background: #263f2d;
}

.primary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.secondary-button {
  background: #f2f5f2;
  color: #354238;
  border: 1px solid #e0e6e0;
}

.secondary-button:hover {
  background: #e9eee9;
}

@media (max-width: 700px) {

  .form-card {
    padding: 20px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-group.full {
    grid-column: auto;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions button {
    width: 100%;
  }

}

</style>

