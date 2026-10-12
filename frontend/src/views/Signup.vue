<template>
  <div class="login-page">
    <section class="login-visual">
      <div class="login-brand">
        <div class="login-brand-mark">✓</div>
        <strong>TaskFlow</strong>
      </div>
      <div class="login-copy">
        <span class="tiny">JOIN YOUR WORKSPACE.</span>
        <h1>Create your<br />account.</h1>
        <p>
          Sign up as an employee, manager or admin and start organising work
          with your team.
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
          <h2>Create account</h2>
          <p>Fill in your details to get started.</p>
        </div>
        <form @submit.prevent="signup">
          <div class="form-group">
            <label for="name">FULL NAME</label
            ><input
              id="name"
              v-model="form.name"
              type="text"
              placeholder="Your name"
              required
            />
          </div>
          <div class="form-group">
            <label for="email">EMAIL ADDRESS</label
            ><input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="you@company.com"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">PASSWORD</label
            ><input
              id="password"
              v-model="form.password"
              type="password"
              minlength="6"
              placeholder="At least 6 characters"
              required
            />
          </div>
          <div class="form-group">
            <label for="role">ROLE</label>
            <select id="role" v-model="form.role" class="signup-select">
              <option value="EMPLOYEE">Employee</option>
              <option value="MANAGER">Manager</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <div v-if="form.role !== 'ADMIN'" class="form-group">
            <label for="team">TEAM (OPTIONAL)</label>
            <select id="team" v-model="form.teamId" class="signup-select">
              <option :value="null">
                {{
                  teams.length
                    ? "Skip for now (admin can assign later)"
                    : "No teams yet. Skip for now"
                }}
              </option>
              <option v-for="t in teams" :key="t.id" :value="t.id">
                {{ t.name }}
              </option>
            </select>
          </div>
          <div v-if="form.role === 'EMPLOYEE'" class="form-group">
            <label for="manager">MANAGER (OPTIONAL)</label>
            <select id="manager" v-model="form.managerId" class="signup-select">
              <option :value="null">
                {{
                  teamManagers.length
                    ? "Skip for now (admin can assign later)"
                    : "No managers yet. Skip for now"
                }}
              </option>
              <option v-for="m in teamManagers" :key="m.id" :value="m.id">
                {{ managerLabel(m) }}
              </option>
            </select>
          </div>
          <div v-if="form.role === 'ADMIN'" class="form-group">
            <label for="code">ADMIN SIGNUP CODE (IF REQUIRED)</label
            ><input
              id="code"
              v-model="form.adminCode"
              type="password"
              placeholder="Leave empty if no code is set"
            />
          </div>
          <button class="sign-in-button" :disabled="loading">
            {{ loading ? "Creating account…" : "Create account →" }}
          </button>
        </form>
        <p v-if="message" class="login-message" :class="{ success }">
          {{ message }}
        </p>
        <p class="signup-switch">
          Already have an account?
          <router-link to="/login">Sign in</router-link>
        </p>
      </div>
    </section>
  </div>
</template>
<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../services/api";

const router = useRouter();
const form = reactive({
  name: "",
  email: "",
  password: "",
  role: "EMPLOYEE",
  teamId: null,
  managerId: null,
  adminCode: "",
});
const teams = ref([]),
  managers = ref([]);
const loading = ref(false),
  message = ref(""),
  success = ref(false);

// With a team picked, only that team's managers are listed; otherwise all managers.
const teamManagers = computed(() =>
  form.teamId
    ? managers.value.filter((m) => m.teamId === form.teamId)
    : managers.value,
);

function managerLabel(m) {
  const team = teams.value.find((t) => t.id === m.teamId);
  return team && !form.teamId ? `${m.name} (${team.name})` : m.name;
}

// Changing the team clears a manager who belongs to a different team.
watch(
  () => form.teamId,
  (id) => {
    const m = managers.value.find((x) => x.id === form.managerId);
    if (m && m.teamId !== id) form.managerId = null;
  },
);

// Picking a manager first fills in that manager's team.
watch(
  () => form.managerId,
  (id) => {
    if (id && form.teamId == null) {
      const m = managers.value.find((x) => x.id === id);
      if (m && m.teamId != null) form.teamId = m.teamId;
    }
  },
);

watch(
  () => form.role,
  () => {
    form.managerId = null;
    if (form.role === "ADMIN") form.teamId = null;
  },
);

onMounted(async () => {
  try {
    const [t, m] = await Promise.all([
      api.get("/auth/teams"),
      api.get("/auth/managers"),
    ]);
    teams.value = t.data || [];
    managers.value = m.data || [];
  } catch (e) {
    message.value = "Cannot load teams. Is the server running?";
  }
});

async function signup() {
  loading.value = true;
  message.value = "";
  success.value = false;
  try {
    const payload = { ...form };
    if (form.role === "ADMIN") {
      payload.teamId = null;
      payload.managerId = null;
    } else {
      payload.adminCode = null;
    }
    if (!payload.adminCode) payload.adminCode = null;
    if (form.role !== "EMPLOYEE") payload.managerId = null;
    const { data } = await api.post("/auth/signup", payload);
    const u = data.user || data;
    const teamName = teams.value.find((t) => t.id === form.teamId)?.name;
    Object.entries({
      token: data.token,
      role: u.role,
      userId: u.id ?? u.userId,
      userName: u.name ?? u.userName,
      userEmail: u.email,
      team: teamName,
    }).forEach(
      ([k, v]) => v !== undefined && v !== null && localStorage.setItem(k, v),
    );
    success.value = true;
    message.value = "Account created!";
    await router.push("/dashboard");
  } catch (e) {
    const d = e.response?.data;
    message.value = e.response
      ? d?.message ||
        (d && Object.values(d)[0]) ||
        "Signup failed. Please try again."
      : "Cannot connect to the server.";
  } finally {
    loading.value = false;
  }
}
</script>
<style scoped>
.signup-select {
  width: 100%;
  height: 45px;
  border: 1px solid var(--line);
  background: #fbfcfa;
  border-radius: 12px;
  padding: 0 13px;
  outline: none;
  font-size: 12px;
}
.signup-select:focus {
  border-color: #9db9ab;
  box-shadow: 0 0 0 3px #eaf2ed;
}
.signup-switch {
  text-align: center;
  font-size: 11px;
  color: var(--muted);
  margin-top: 18px;
}
.signup-switch a {
  color: var(--green);
  font-weight: 700;
}
</style>
