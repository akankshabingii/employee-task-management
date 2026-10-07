```vue
<template>
  <div class="register-page">
    <div class="register-card">

      <!-- Brand -->
      <div class="brand">
        <div class="brand-icon">✓</div>
        <div>
          <h1>TaskFlow</h1>
          <p>Employee Task Management</p>
        </div>
      </div>

      <!-- Heading -->
      <div class="heading">
        <h2>Create your account</h2>
        <p>Join your organization and start managing tasks.</p>
      </div>

      <!-- Error -->
      <div v-if="message && !success" class="message error">
        {{ message }}
      </div>

      <!-- Success -->
      <div v-if="success" class="message success">
        {{ message }}
      </div>

      <form @submit.prevent="register">

        <!-- Name -->
        <div class="field">
          <label>Full name</label>
          <input
            v-model="name"
            type="text"
            placeholder="Enter your full name"
            required
          />
        </div>

        <!-- Email -->
        <div class="field">
          <label>Email</label>
          <input
            v-model="email"
            type="email"
            placeholder="you@example.com"
            required
          />
        </div>

        <!-- Role -->
        <div class="field">
          <label>Account type</label>

          <div class="role-options">

            <label
              class="role-option"
              :class="{ selected: role === 'EMPLOYEE' }"
            >
              <input
                type="radio"
                value="EMPLOYEE"
                v-model="role"
              />

              <div>
                <strong>Employee</strong>
                <span>Manage your assigned tasks</span>
              </div>
            </label>

            <label
              class="role-option"
              :class="{ selected: role === 'MANAGER' }"
            >
              <input
                type="radio"
                value="MANAGER"
                v-model="role"
              />

              <div>
                <strong>Manager</strong>
                <span>Manage teams and tasks</span>
              </div>
            </label>

          </div>
        </div>

        <!-- Password -->
        <div class="field">
          <label>Password</label>
          <input
            v-model="password"
            type="password"
            placeholder="Create a password"
            required
          />
        </div>

        <!-- Confirm Password -->
        <div class="field">
          <label>Confirm password</label>
          <input
            v-model="confirmPassword"
            type="password"
            placeholder="Re-enter your password"
            required
          />
        </div>

        <!-- Register -->
        <button
          type="submit"
          class="register-button"
          :disabled="loading"
        >
          {{ loading ? 'Creating account...' : 'Create account' }}
        </button>

      </form>

      <!-- Login -->
      <div class="login-link">
        Already have an account?
        <button @click="goToLogin">
          Sign in
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const role = ref('EMPLOYEE')

const loading = ref(false)
const message = ref('')
const success = ref(false)

async function register() {

  message.value = ''
  success.value = false

  if (password.value !== confirmPassword.value) {
    message.value = 'Passwords do not match.'
    return
  }

  if (password.value.length < 6) {
    message.value = 'Password must be at least 6 characters.'
    return
  }

  loading.value = true

  try {

    await api.post('/auth/register', {
      name: name.value,
      email: email.value,
      password: password.value,
      role: role.value
    })

    success.value = true
    message.value = 'Account created successfully! Redirecting to login...'

    setTimeout(() => {
      router.push('/login')
    }, 1200)

  } catch (e) {

    if (e.response?.status === 400) {
      message.value =
        e.response?.data?.message ||
        'Please check your information and try again.'
    } else if (e.response?.status === 409) {
      message.value = 'An account with this email already exists.'
    } else {
      message.value =
        e.response?.data?.message ||
        'Unable to create account. Please try again.'
    }

  } finally {
    loading.value = false
  }
}

function goToLogin() {
  router.push('/login')
}
</script>

<style scoped>

* {
  box-sizing: border-box;
}

.register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  background: #f5f7f6;
}

.register-card {
  width: 100%;
  max-width: 470px;
  background: white;
  border: 1px solid #e5e9e7;
  border-radius: 18px;
  padding: 38px 40px;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.06);
}

/* Brand */

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
}

.brand-icon {
  width: 42px;
  height: 42px;
  border-radius: 11px;
  background: #1f5138;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 21px;
  font-weight: 700;
}

.brand h1 {
  margin: 0;
  font-size: 21px;
  color: #1d3027;
}

.brand p {
  margin: 3px 0 0;
  color: #7a8580;
  font-size: 12px;
}

/* Heading */

.heading {
  margin-bottom: 25px;
}

.heading h2 {
  margin: 0 0 7px;
  font-size: 27px;
  color: #1d3027;
}

.heading p {
  margin: 0;
  font-size: 14px;
  color: #7a8580;
  line-height: 1.5;
}

/* Fields */

.field {
  margin-bottom: 18px;
}

.field label {
  display: block;
  margin-bottom: 7px;
  font-size: 13px;
  font-weight: 600;
  color: #35443d;
}

.field input[type="text"],
.field input[type="email"],
.field input[type="password"] {
  width: 100%;
  padding: 12px 13px;
  border: 1px solid #dce2df;
  border-radius: 9px;
  font-size: 14px;
  outline: none;
  background: #fff;
  color: #26352e;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.field input:focus {
  border-color: #3d7658;
  box-shadow: 0 0 0 3px rgba(61, 118, 88, 0.1);
}

.field input::placeholder {
  color: #a2aaa6;
}

/* Role */

.role-options {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.role-option {
  display: flex;
  align-items: flex-start;
  gap: 9px;
  padding: 12px;
  border: 1px solid #dce2df;
  border-radius: 9px;
  cursor: pointer;
  transition: 0.2s;
}

.role-option:hover {
  border-color: #9ab5a5;
}

.role-option.selected {
  border-color: #3d7658;
  background: #f3f8f5;
}

.role-option input {
  margin-top: 3px;
  accent-color: #1f5138;
}

.role-option strong {
  display: block;
  color: #304139;
  font-size: 13px;
}

.role-option span {
  display: block;
  margin-top: 3px;
  color: #89928e;
  font-size: 11px;
  line-height: 1.35;
}

/* Messages */

.message {
  padding: 11px 13px;
  border-radius: 8px;
  font-size: 13px;
  margin-bottom: 18px;
}

.message.error {
  background: #fff1f1;
  color: #a43d3d;
  border: 1px solid #f0d2d2;
}

.message.success {
  background: #eef8f1;
  color: #34704c;
  border: 1px solid #cce5d4;
}

/* Button */

.register-button {
  width: 100%;
  border: none;
  border-radius: 9px;
  padding: 13px;
  background: #1f5138;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
  margin-top: 4px;
}

.register-button:hover {
  background: #17432d;
}

.register-button:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* Login */

.login-link {
  text-align: center;
  margin-top: 22px;
  font-size: 13px;
  color: #7a8580;
}

.login-link button {
  border: none;
  background: none;
  color: #1f5138;
  font-weight: 600;
  cursor: pointer;
  padding: 0;
  margin-left: 4px;
}

.login-link button:hover {
  text-decoration: underline;
}

/* Mobile */

@media (max-width: 500px) {

  .register-card {
    padding: 30px 24px;
  }

  .role-options {
    grid-template-columns: 1fr;
  }

  .heading h2 {
    font-size: 24px;
  }
}

</style>
```
