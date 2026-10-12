<template>
  <div class="page">
    <header class="page-header">
      <div>
        <div class="eyebrow">Administration</div>
        <h1 class="page-title">Teams</h1>
        <p class="page-subtitle">Create teams so managers and employees can be assigned to them.</p>
      </div>
    </header>

    <div class="card team-form">
      <form @submit.prevent="createTeam">
        <input v-model.trim="newName" type="text" placeholder="New team name, e.g. Engineering" required />
        <button class="t-btn primary" :disabled="saving">{{ saving ? 'Saving…' : 'Create team' }}</button>
      </form>
      <p v-if="message" class="t-message" :class="{ ok: success }">{{ message }}</p>
    </div>

    <div class="card table-card">
      <table class="table">
        <thead>
          <tr><th style="width:80px">ID</th><th>Team name</th><th>Members</th><th style="width:220px">Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="t in teams" :key="t.id">
            <td>{{ t.id }}</td>
            <td>
              <input v-if="editingId === t.id" v-model.trim="editName" class="edit-input" @keyup.enter="saveEdit(t)" />
              <strong v-else>{{ t.name }}</strong>
            </td>
            <td>{{ memberCount[t.id] ?? 0 }}</td>
            <td class="actions">
              <template v-if="editingId === t.id">
                <button class="t-btn primary" @click="saveEdit(t)">Save</button>
                <button class="t-btn" @click="editingId = null">Cancel</button>
              </template>
              <template v-else>
                <button class="t-btn" @click="startEdit(t)">Rename</button>
                <button class="t-btn danger" @click="removeTeam(t)">Delete</button>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!teams.length" class="empty">
        <strong>No teams yet</strong>
        <span>Create your first team above.</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const teams = ref([])
const memberCount = ref({})
const newName = ref('')
const saving = ref(false)
const message = ref('')
const success = ref(false)
const editingId = ref(null)
const editName = ref('')

function errorText(e, fallback) {
  const d = e.response?.data
  return (typeof d === 'string' && d) || d?.message || fallback
}

async function load() {
  try {
    teams.value = (await api.get('/teams')).data || []
  } catch (e) {
    message.value = errorText(e, 'Could not load teams.')
    success.value = false
    return
  }
  // Member counts (best effort, the page still works without them)
  try {
    const users = (await api.get('/users')).data || []
    const counts = {}
    users.forEach(u => { if (u.teamId != null) counts[u.teamId] = (counts[u.teamId] || 0) + 1 })
    memberCount.value = counts
  } catch (e) {
    memberCount.value = {}
  }
}

async function createTeam() {
  if (!newName.value) return
  saving.value = true; message.value = ''
  try {
    await api.post('/teams', { name: newName.value })
    newName.value = ''
    success.value = true
    message.value = 'Team created.'
    await load()
  } catch (e) {
    success.value = false
    message.value = errorText(e, 'Could not create the team.')
  } finally {
    saving.value = false
  }
}

function startEdit(t) { editingId.value = t.id; editName.value = t.name }

async function saveEdit(t) {
  if (!editName.value) return
  message.value = ''
  try {
    await api.put(`/teams/${t.id}`, { name: editName.value })
    editingId.value = null
    success.value = true
    message.value = 'Team renamed.'
    await load()
  } catch (e) {
    success.value = false
    message.value = errorText(e, 'Could not rename the team.')
  }
}

async function removeTeam(t) {
  if (!window.confirm(`Delete team "${t.name}"?`)) return
  message.value = ''
  try {
    await api.delete(`/teams/${t.id}`)
    success.value = true
    message.value = 'Team deleted.'
    await load()
  } catch (e) {
    success.value = false
    message.value = errorText(e, 'Could not delete the team. Remove or move its members first.')
  }
}

onMounted(load)
</script>

<style scoped>
.team-form { padding: 16px; margin-bottom: 18px; }
.team-form form { display: flex; gap: 10px; flex-wrap: wrap; }
.team-form input, .edit-input {
  flex: 1; min-width: 200px; height: 42px; padding: 0 13px;
  border: 1px solid var(--line); border-radius: 12px; background: #fbfcfa; outline: none; font-size: 13px;
}
.edit-input { height: 36px; min-width: 140px; width: 100%; }
.team-form input:focus, .edit-input:focus { border-color: #9db9ab; box-shadow: 0 0 0 3px #eaf2ed; }
.t-btn {
  height: 36px; padding: 0 14px; border-radius: 10px; border: 1px solid var(--line);
  background: #fff; cursor: pointer; font-size: 12px; font-weight: 600; margin-right: 6px;
}
.t-btn.primary { background: var(--green); border-color: var(--green); color: #fff; height: 42px; }
.table .t-btn.primary { height: 36px; }
.t-btn.danger { color: #b3261e; border-color: #f0c9c6; }
.t-btn:disabled { opacity: .6; cursor: default; }
.t-message { margin: 12px 2px 0; font-size: 12px; color: #b3261e; }
.t-message.ok { color: var(--green); }
.actions { white-space: nowrap; }
</style>