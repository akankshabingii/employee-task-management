<template>
  <div class="task-section">

    <h2>Tasks</h2>

    <p v-if="loading">Loading tasks...</p>

    <p v-if="error">
      {{ error }}
    </p>

    <table v-if="!loading && tasks.length > 0">

      <thead>
        <tr>
          <th>Title</th>
          <th>Priority</th>
          <th>Status</th>
          <th>Progress</th>
          <th>Start Date</th>
          <th>End Date</th>
        </tr>
      </thead>

      <tbody>

        <tr
          v-for="task in tasks"
          :key="task.id"
        >
          <td>{{ task.title }}</td>
          <td>{{ task.priority }}</td>
          <td>{{ task.status }}</td>
          <td>{{ task.progress }}%</td>
          <td>{{ task.startDate }}</td>
          <td>{{ task.endDate }}</td>
        </tr>

      </tbody>

    </table>

    <p v-if="!loading && tasks.length === 0">
      No tasks found.
    </p>

  </div>
</template>

<script setup>

import { ref, onMounted } from 'vue'
import api from '../services/api'

const tasks = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {

  try {

    const response = await api.get('/tasks')

    console.log('Tasks:', response.data)

    tasks.value = response.data

  } catch (err) {

    console.error('Failed to load tasks:', err)

    error.value = 'Failed to load tasks.'

  } finally {

    loading.value = false

  }

})

</script>

<style scoped>

.task-section {
  margin-top: 40px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th,
td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

th {
  background: #f4f4f4;
}

</style>