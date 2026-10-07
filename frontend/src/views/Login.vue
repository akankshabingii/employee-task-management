
<template>
  <div class="login-page">

    <!-- LEFT VISUAL PANEL -->
    <section class="login-visual">

      <div class="login-brand">
        <div class="login-brand-mark">✓</div>
        <strong>TaskFlow</strong>
      </div>

      <div class="login-copy">
        <span class="tiny">YOUR WORK. IN ONE PLACE.</span>

        <h1>
          Make work feel<br />
          a little lighter.
        </h1>

        <p>
          Plan tasks, keep your team aligned, and see what needs your attention —
          without the clutter.
        </p>
      </div>

      <div class="login-quote">
        A calmer workspace for better work.
      </div>

    </section>


    <!-- RIGHT LOGIN PANEL -->
    <section class="login-panel">

      <div class="login-card">

        <!-- Mobile brand -->
        <div class="mobile-login-brand">
          <div class="brand-mark">✓</div>
          <strong>TaskFlow</strong>
        </div>


        <!-- Header -->
        <div class="login-header">
          <h2>Welcome back</h2>
          <p>Sign in to continue to your workspace.</p>
        </div>


        <!-- Login form -->
        <form @submit.prevent="login">

          <div class="form-group">
            <label for="email">EMAIL ADDRESS</label>

            <input
              id="email"
              v-model="email"
              type="email"
              placeholder="you@company.com"
              required
            />
          </div>


          <div class="form-group">
            <label for="password">PASSWORD</label>

            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="Enter your password"
              required
            />
          </div>


          <div class="login-options">

            <label class="remember">
              <input
                v-model="rememberMe"
                type="checkbox"
              />

              Remember me
            </label>

            <a
              href="#"
              @click.prevent
            >
              Forgot password?
            </a>

          </div>


          <button
            class="sign-in-button"
            :disabled="loading"
          >
            {{ loading ? 'Signing you in…' : 'Sign in to TaskFlow →' }}
          </button>

        </form>


        <!-- Login message -->
        <p
          v-if="message"
          class="login-message"
          :class="{ success }"
        >
          {{ message }}
        </p>


        <!-- Signup -->
        <div class="signup-link">

          <span>Don't have an account?</span>

          <button
            type="button"
            @click="goToRegister"
          >
            Create account
          </button>

        </div>


        <!-- Divider -->
        <div class="divider">
          <span></span>
          <p>OR</p>
          <span></span>
        </div>


        <!-- Demo section -->
        <div class="demo-section">

          <div class="demo-heading">
            <strong>Explore TaskFlow</strong>
            <span>Try the app without creating an account.</span>
          </div>


          <div class="demo-buttons">

            <button
              class="demo-button"
              :disabled="demoLoading"
              @click="demoLogin('ADMIN')"
            >
              <span class="demo-icon">A</span>

              <span class="demo-text">
                <strong>Admin</strong>
                <small>Full access</small>
              </span>

              <span class="demo-arrow">→</span>
            </button>


            <button
              class="demo-button"
              :disabled="demoLoading"
              @click="demoLogin('MANAGER')"
            >
              <span class="demo-icon">M</span>

              <span class="demo-text">
                <strong>Manager</strong>
                <small>Team workspace</small>
              </span>

              <span class="demo-arrow">→</span>
            </button>


            <button
              class="demo-button"
              :disabled="demoLoading"
              @click="demoLogin('EMPLOYEE')"
            >
              <span class="demo-icon">E</span>

              <span class="demo-text">
                <strong>Employee</strong>
                <small>Personal workspace</small>
              </span>

              <span class="demo-arrow">→</span>
            </button>

          </div>

        </div>

      </div>

    </section>

  </div>
</template>


<script setup>

import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()

const email = ref('')
const password = ref('')
const rememberMe = ref(false)

const loading = ref(false)
const demoLoading = ref(false)

const message = ref('')
const success = ref(false)


/* =========================
   NORMAL LOGIN
   ========================= */

async function login() {

  loading.value = true
  message.value = ''
  success.value = false

  try {

    const { data } = await api.post('/auth/login', {
      email: email.value,
      password: password.value
    })

    saveUserSession(data)

    success.value = true
    message.value = 'Welcome back!'

    await router.push('/dashboard')

  } catch (e) {

    message.value =
      e.response?.status === 401
        ? 'Invalid email or password.'
        : e.response
          ? 'Login failed. Please try again.'
          : 'Cannot connect to the server.'

  } finally {

    loading.value = false

  }
}


/* =========================
   DEMO LOGIN
   ========================= */

async function demoLogin(role) {

  demoLoading.value = true
  message.value = ''
  success.value = false

  try {

    const { data } = await api.post('/auth/demo', {
      role
    })

    saveUserSession(data)

    success.value = true
    message.value = `Opening ${role.toLowerCase()} demo…`

    await router.push('/dashboard')

  } catch (e) {

    message.value =
      e.response?.data?.message ||
      'Demo access is currently unavailable.'

  } finally {

    demoLoading.value = false

  }
}


/* =========================
   SAVE SESSION
   ========================= */

function saveUserSession(data) {

  const user = data.user

  localStorage.setItem('token', data.token)
  localStorage.setItem('role', user.role)
  localStorage.setItem('userId', user.id)
  localStorage.setItem('userName', user.name)
  localStorage.setItem('userEmail', user.email)

}


/* =========================
   REGISTER
   ========================= */

function goToRegister() {
  router.push('/register')
}

</script>


<style scoped>

* {
  box-sizing: border-box;
}


/* =========================
   PAGE
   ========================= */

.login-page {
  min-height: 100vh;

  display: flex;

  background: #f5f7f6;
}


/* =========================
   LEFT VISUAL
   ========================= */

.login-visual {
  width: 52%;
  min-height: 100vh;

  padding: 48px 60px;

  display: flex;
  flex-direction: column;
  justify-content: space-between;

  background: #1f5138;

  color: white;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 12px;

  font-size: 21px;
}

.login-brand-mark {
  width: 42px;
  height: 42px;

  border-radius: 11px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: white;
  color: #1f5138;

  font-size: 21px;
  font-weight: 700;
}

.login-copy {
  max-width: 520px;
}

.tiny {
  display: block;

  margin-bottom: 18px;

  font-size: 11px;
  font-weight: 700;

  letter-spacing: 2px;

  opacity: 0.65;
}

.login-copy h1 {
  margin: 0 0 22px;

  font-size: clamp(42px, 4vw, 64px);

  line-height: 1.05;

  letter-spacing: -2px;
}

.login-copy p {
  max-width: 460px;

  margin: 0;

  font-size: 15px;

  line-height: 1.7;

  color: rgba(255, 255, 255, 0.72);
}

.login-quote {
  font-size: 13px;

  color: rgba(255, 255, 255, 0.55);
}


/* =========================
   RIGHT PANEL
   ========================= */

.login-panel {
  flex: 1;

  min-height: 100vh;

  display: flex;

  align-items: center;
  justify-content: center;

  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 430px;
}


/* =========================
   MOBILE BRAND
   ========================= */

.mobile-login-brand {
  display: none;

  align-items: center;
  gap: 10px;

  margin-bottom: 30px;

  font-size: 20px;

  color: #1d3027;
}

.brand-mark {
  width: 40px;
  height: 40px;

  border-radius: 10px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #1f5138;

  color: white;

  font-weight: 700;
}


/* =========================
   HEADER
   ========================= */

.login-header {
  margin-bottom: 32px;
}

.login-header h2 {
  margin: 0 0 8px;

  font-size: 30px;

  color: #1d3027;

  letter-spacing: -0.6px;
}

.login-header p {
  margin: 0;

  font-size: 14px;

  color: #7a8580;
}


/* =========================
   FORM
   ========================= */

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;

  margin-bottom: 8px;

  font-size: 11px;

  font-weight: 700;

  letter-spacing: 1px;

  color: #52615a;
}

.form-group input {
  width: 100%;

  padding: 14px 15px;

  border: 1px solid #dce2df;

  border-radius: 9px;

  outline: none;

  background: white;

  color: #26352e;

  font-size: 14px;

  transition: 0.2s;
}

.form-group input:focus {
  border-color: #3d7658;

  box-shadow:
    0 0 0 3px rgba(61, 118, 88, 0.1);
}

.form-group input::placeholder {
  color: #a2aaa6;
}


/* =========================
   OPTIONS
   ========================= */

.login-options {
  display: flex;

  justify-content: space-between;

  align-items: center;

  margin: 5px 0 24px;

  font-size: 12px;
}

.remember {
  display: flex;

  align-items: center;

  gap: 7px;

  color: #69756f;
}

.remember input {
  accent-color: #1f5138;
}

.login-options a {
  color: #1f5138;

  text-decoration: none;

  font-weight: 600;
}

.login-options a:hover {
  text-decoration: underline;
}


/* =========================
   SIGN IN
   ========================= */

.sign-in-button {
  width: 100%;

  padding: 14px;

  border: none;

  border-radius: 9px;

  background: #1f5138;

  color: white;

  font-size: 14px;

  font-weight: 600;

  cursor: pointer;

  transition: 0.2s;
}

.sign-in-button:hover {
  background: #17432d;
}

.sign-in-button:disabled {
  opacity: 0.65;

  cursor: not-allowed;
}


/* =========================
   MESSAGE
   ========================= */

.login-message {
  margin: 16px 0 0;

  padding: 11px 13px;

  border-radius: 8px;

  font-size: 13px;

  background: #fff1f1;

  color: #a43d3d;

  border: 1px solid #f0d2d2;
}

.login-message.success {
  background: #eef8f1;

  color: #34704c;

  border-color: #cce5d4;
}


/* =========================
   SIGNUP
   ========================= */

.signup-link {
  display: flex;

  justify-content: center;

  align-items: center;

  gap: 4px;

  margin-top: 22px;

  font-size: 13px;

  color: #7a8580;
}

.signup-link button {
  padding: 0;

  border: none;

  background: transparent;

  color: #1f5138;

  font-size: 13px;

  font-weight: 600;

  cursor: pointer;
}

.signup-link button:hover {
  text-decoration: underline;
}


/* =========================
   DIVIDER
   ========================= */

.divider {
  display: flex;

  align-items: center;

  gap: 12px;

  margin: 24px 0 20px;
}

.divider span {
  flex: 1;

  height: 1px;

  background: #e2e7e4;
}

.divider p {
  margin: 0;

  font-size: 10px;

  font-weight: 700;

  letter-spacing: 1px;

  color: #a0aaa5;
}


/* =========================
   DEMO
   ========================= */

.demo-section {
  width: 100%;
}

.demo-heading {
  margin-bottom: 12px;
}

.demo-heading strong {
  display: block;

  margin-bottom: 4px;

  font-size: 13px;

  color: #35443d;
}

.demo-heading span {
  font-size: 11px;

  color: #89928e;
}

.demo-buttons {
  display: flex;

  flex-direction: column;

  gap: 8px;
}

.demo-button {
  width: 100%;

  display: flex;

  align-items: center;

  gap: 11px;

  padding: 10px 12px;

  border: 1px solid #dfe5e2;

  border-radius: 9px;

  background: white;

  cursor: pointer;

  text-align: left;

  transition: 0.2s;
}

.demo-button:hover {
  border-color: #9ab5a5;

  background: #f8faf9;

  transform: translateY(-1px);
}

.demo-button:disabled {
  opacity: 0.6;

  cursor: not-allowed;

  transform: none;
}

.demo-icon {
  width: 31px;
  height: 31px;

  flex-shrink: 0;

  display: flex;

  align-items: center;
  justify-content: center;

  border-radius: 8px;

  background: #eef4f0;

  color: #1f5138;

  font-size: 11px;

  font-weight: 700;
}

.demo-text {
  flex: 1;
}

.demo-text strong {
  display: block;

  margin-bottom: 2px;

  font-size: 12px;

  color: #35443d;
}

.demo-text small {
  display: block;

  font-size: 10px;

  color: #8b9590;
}

.demo-arrow {
  color: #9aa49f;

  font-size: 14px;
}

.demo-button:hover .demo-arrow {
  color: #1f5138;
}


/* =========================
   RESPONSIVE
   ========================= */

@media (max-width: 850px) {

  .login-visual {
    display: none;
  }

  .login-panel {
    min-height: 100vh;

    padding: 30px 22px;
  }

  .mobile-login-brand {
    display: flex;
  }

}

@media (max-width: 500px) {

  .login-panel {
    padding: 24px 20px;
  }

  .login-header h2 {
    font-size: 27px;
  }

}

</style>

