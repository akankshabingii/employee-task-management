<template>
  <aside class="sidebar">
    <div class="brand-row">
      <div class="brand-mark"><span>✓</span></div>
      <div>
        <strong>TaskFlow</strong>
        <small>WORKSPACE</small>
      </div>
    </div>

    <div class="workspace-pill">
      <div class="workspace-dot"></div>
      <div>
        <span>Personal workspace</span>
        <small>Online</small>
      </div>
      <span class="chevron">⌄</span>
    </div>

    <p class="nav-label">OVERVIEW</p>

    <nav class="navigation">
      <router-link to="/dashboard" class="nav-item">
        <span class="nav-icon">⌂</span>
        <span>Dashboard</span>
      </router-link>

      <router-link to="/tasks" class="nav-item">
        <span class="nav-icon">✓</span>
        <span>My tasks</span>
      </router-link>

      <router-link to="/team" class="nav-item">
        <span class="nav-icon">♧</span>
        <span>Team</span>
      </router-link>
    </nav>

    <!-- ADMIN ONLY -->
    <template v-if="isAdmin">
      <p class="nav-label second">ADMINISTRATION</p>

      <nav class="navigation">
        <router-link to="/users" class="nav-item">
          <span class="nav-icon">♙</span>
          <span>Users</span>
        </router-link>
      </nav>
    </template>

    <p class="nav-label second">WORKSPACE</p>

    <nav class="navigation">
      <router-link to="/settings" class="nav-item">
        <span class="nav-icon">⚙</span>
        <span>Settings</span>
      </router-link>

      <router-link to="/profile" class="nav-item">
        <span class="nav-icon">◉</span>
        <span>My profile</span>
      </router-link>
    </nav>

    <div class="sidebar-spacer"></div>

    <div class="help-card">
      <div class="help-symbol">?</div>
      <div>
        <strong>Need a hand?</strong>
        <span>Everything is under control.</span>
      </div>
    </div>

    <div class="sidebar-user">
      <div class="avatar avatar-sm">{{ initial }}</div>

      <div class="sidebar-user-copy">
        <strong>{{ userName }}</strong>
        <span>{{ role }}</span>
      </div>

      <button
        class="logout-mini"
        title="Sign out"
        @click="logout"
      >
        ↪
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userName = computed(
  () => localStorage.getItem('userName') || 'User'
)

const role = computed(
  () => localStorage.getItem('role') || 'EMPLOYEE'
)

const isAdmin = computed(
  () => role.value === 'ADMIN'
)

const initial = computed(
  () => userName.value.charAt(0).toUpperCase()
)

function logout() {
  ;[
    'token',
    'role',
    'userId',
    'userName',
    'userEmail',
    'team'
  ].forEach(k => localStorage.removeItem(k))

  router.push('/login')
}
</script>