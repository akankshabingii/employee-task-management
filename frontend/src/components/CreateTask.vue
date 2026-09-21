<template>
  <div class="create-task">

    <h2>Create Task</h2>

    <form @submit.prevent="createTask">

      <div>
        <label>Title</label>
        <input
          v-model="title"
          type="text"
          required
        />
      </div>

      <div>
        <label>Priority</label>
        <select v-model="priority" required>
          <option value="">Select priority</option>
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
        </select>
      </div>

      <div>
        <label>Description</label>
        <textarea
          v-model="description"
        ></textarea>
      </div>

      <div>
        <label>Start Date</label>
        <input
          v-model="startDate"
          type="date"
          required
        />
      </div>

      <div>
        <label>End Date</label>
        <input
          v-model="endDate"
          type="date"
          required
        />
      </div>

      <button type="submit">
        Create Task
      </button>

    </form>

    <p v-if="message">
      {{ message }}
    </p>

  </div>
</template>

<script setup>

import { ref } from 'vue'
import api from '../services/api'

const title = ref('')
const priority = ref('')
const description = ref('')
const startDate = ref('')
const endDate = ref('')

const message = ref('')

async function createTask() {

  try {

    const response = await api.post('/tasks', {

      title: title.value,

      priority: priority.value,

      description: description.value,

      startDate: startDate.value,

      endDate: endDate.value

    })

    console.log('Task created:', response.data)

    message.value = 'Task created successfully!'

    // Clear form
    title.value = ''
    priority.value = ''
    description.value = ''
    startDate.value = ''
    endDate.value = ''

  } catch (error) {

    console.error('Failed to create task:', error)

    message.value = 'Failed to create task.'

  }

}

</script>

<style scoped>

.create-task {
  margin-top: 40px;
  padding: 25px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-width: 500px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
}

input,
select,
textarea {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
}

textarea {
  min-height: 100px;
}

button {
  padding: 10px 15px;
  cursor: pointer;
}

</style>