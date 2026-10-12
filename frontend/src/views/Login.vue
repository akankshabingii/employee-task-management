<template>
  <div class="login-page">
    <section class="login-visual">
      <div class="login-brand">
        <div class="login-brand-mark">✓</div>
        <strong>TaskFlow</strong>
      </div>
      <div class="login-copy">
        <span class="tiny">YOUR WORK. IN ONE PLACE.</span>
        <h1>Make work feel<br />a little lighter.</h1>
        <p>
          Plan tasks, keep your team aligned, and see what needs your attention
          — without the clutter.
        </p>
      </div>
      <div class="login-quote">A calmer workspace for better work.</div>
    </section>
    <section class="login-panel">
      <div class="login-card">
        <div class="mobile-login-brand">
          <div class="brand-mark">✓</div>
          <strong>TaskFlow</strong>
        </div>
        <div class="login-header">
          <h2>Welcome back</h2>
          <p>Sign in to continue to your workspace.</p>
        </div>
        <form @submit.prevent="login">
          <div class="form-group">
            <label for="email">EMAIL ADDRESS</label
            ><input
              id="email"
              v-model="email"
              type="email"
              placeholder="you@company.com"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">PASSWORD</label
            ><input
              id="password"
              v-model="password"
              type="password"
              placeholder="Enter your password"
              required
            />
          </div>
          <div class="login-options">
            <label class="remember"
              ><input v-model="rememberMe" type="checkbox" /> Remember me</label
            >
          </div>
          <button class="sign-in-button" :disabled="loading">
            {{ loading ? "Signing you in…" : "Sign in to TaskFlow →" }}
          </button>
        </form>
        <p v-if="message" class="login-message" :class="{ success }">
          {{ message }}
        </p>

        <!-- Demo logins: click a row to fill the form -->
        <div class="demo-box">
          <strong>Demo logins</strong>
          <button
            v-for="d in demoAccounts"
            :key="d.role"
            type="button"
            class="demo-row"
            @click="fillDemo(d)"
          >
            <span>{{ d.role }}</span
            ><span>{{ d.email }}</span>
          </button>
        </div>

        <p class="signup-link">
          New here? <router-link to="/signup">Create an account</router-link>
        </p>
      </div>
    </section>
  </div>
</template>
<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "../services/api";
const router = useRouter(),
  email = ref(""),
  password = ref(""),
  rememberMe = ref(false),
  loading = ref(false),
  message = ref(""),
  success = ref(false);

// TODO: change these to the accounts you actually create
const demoAccounts = [
  { role: "Admin", email: "admin@demo.com", password: "Demo@123" },
  { role: "Manager", email: "manager@demo.com", password: "Demo@123" },
  { role: "Employee", email: "employee@demo.com", password: "Demo@123" },
];
function fillDemo(d) {
  email.value = d.email;
  password.value = d.password;
}

async function login() {
  loading.value = true;
  message.value = "";
  success.value = false;
  try {
    const { data } = await api.post("/auth/login", {
      email: email.value,
      password: password.value,
    });
    const u = data.user || data;
    Object.entries({
      token: data.token,
      role: u.role,
      userId: u.id ?? u.userId,
      userName: u.name ?? u.userName,
      userEmail: u.email,
      team: u.team,
    }).forEach(
      ([k, v]) => v !== undefined && v !== null && localStorage.setItem(k, v),
    );
    success.value = true;
    message.value = "Welcome back!";
    await router.push("/dashboard");
  } catch (e) {
    message.value =
      e.response?.status === 401
        ? "Invalid email or password."
        : e.response
          ? "Login failed. Please try again."
          : "Cannot connect to the server.";
  } finally {
    loading.value = false;
  }
}
</script>
<style scoped>
.signup-link {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: var(--muted);
}
.signup-link a {
  color: var(--green);
  font-weight: 700;
}
.demo-box {
  margin-top: 18px;
  padding: 12px;
  border: 1px dashed var(--line);
  border-radius: 12px;
  font-size: 12px;
  color: var(--muted);
  display: grid;
  gap: 6px;
}
.demo-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
  padding: 8px 10px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: #fbfcfa;
  font-size: 12px;
  cursor: pointer;
  text-align: left;
}
.demo-row:hover {
  border-color: #9db9ab;
}
.demo-row span:first-child {
  font-weight: 700;
  color: var(--green);
}
</style>
