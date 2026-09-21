<template>
  <div class="login-page">
    <div class="login-card">

      <h1>Employee Task Management</h1>
      <p class="subtitle">Login to your account</p>

      <form @submit.prevent="handleLogin">

        <div class="form-group">
          <label>Email</label>

          <input
            type="email"
            v-model="email"
            placeholder="Enter your email"
            required
          />
        </div>

        <div class="form-group">
          <label>Password</label>

          <input
            type="password"
            v-model="password"
            placeholder="Enter your password"
            required
          />
        </div>

        <button type="submit">
          Login
        </button>

      </form>

      <p class="signup-text">
        Don't have an account?
        <a href="/signup">Sign up</a>
      </p>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../services/api'

const email = ref('')
const password = ref('')

async function handleLogin() {
  console.log('LOGIN BUTTON CLICKED')

  try {
    const response = await api.post('/auth/login', {
      email: email.value,
      password: password.value
    })

    console.log('Login successful:', response.data)

    localStorage.setItem('token', response.data.token)
    localStorage.setItem('user', JSON.stringify(response.data.user))

    window.location.href = '/dashboard'

  } catch (error) {
    console.error('Login failed:', error)
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f4f6f8;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.login-card h1 {
  text-align: center;
  margin-bottom: 8px;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
}

.form-group input {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 6px;
}

button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 6px;
  background: #333;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.signup-text {
  text-align: center;
  margin-top: 20px;
}
</style>