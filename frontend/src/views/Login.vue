<template>
  <div class="login-page">
    <section class="login-visual">
      <div class="login-brand"><div class="login-brand-mark">✓</div><strong>TaskFlow</strong></div>
      <div class="login-copy"><span class="tiny">YOUR WORK. IN ONE PLACE.</span><h1>Make work feel<br/>a little lighter.</h1><p>Plan tasks, keep your team aligned, and see what needs your attention — without the clutter.</p></div>
      <div class="login-quote">A calmer workspace for better work.</div>
    </section>
    <section class="login-panel">
      <div class="login-card">
        <div class="mobile-login-brand"><div class="brand-mark">✓</div><strong>TaskFlow</strong></div>
        <div class="login-header"><h2>Welcome back</h2><p>Sign in to continue to your workspace.</p></div>
        <form @submit.prevent="login">
          <div class="form-group"><label for="email">EMAIL ADDRESS</label><input id="email" v-model="email" type="email" placeholder="you@company.com" required /></div>
          <div class="form-group"><label for="password">PASSWORD</label><input id="password" v-model="password" type="password" placeholder="Enter your password" required /></div>
          <div class="login-options"><label class="remember"><input v-model="rememberMe" type="checkbox"/> Remember me</label><a href="#" @click.prevent>Forgot password?</a></div>
          <button class="sign-in-button" :disabled="loading">{{ loading ? 'Signing you in…' : 'Sign in to TaskFlow →' }}</button>
        </form>
        <p v-if="message" class="login-message" :class="{success}">{{ message }}</p>
        <p class="login-message" style="color:var(--muted)">New here? <router-link to="/signup" style="color:var(--green);font-weight:700">Create an account</router-link></p>
      </div>
    </section>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'
const router=useRouter(),email=ref(''),password=ref(''),rememberMe=ref(false),loading=ref(false),message=ref(''),success=ref(false)
async function login(){loading.value=true;message.value='';success.value=false;try{const {data}=await api.post('/auth/login',{email:email.value,password:password.value});const u=data.user||data;Object.entries({token:data.token,role:u.role,userId:u.id??u.userId,userName:u.name??u.userName,userEmail:u.email,team:u.team}).forEach(([k,v])=>v!==undefined&&v!==null&&localStorage.setItem(k,v));success.value=true;message.value='Welcome back!';await router.push('/dashboard')}catch(e){message.value=e.response?.status===401?'Invalid email or password.':e.response?'Login failed. Please try again.':'Cannot connect to the server.'}finally{loading.value=false}}
</script>