import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/Login.vue'
import Signup from '../views/Signup.vue'
import Dashboard from '../views/Dashboard.vue'
import Tasks from '../views/Tasks.vue'
import TaskDetails from '../views/TaskDetails.vue'
import Profile from '../views/Profile.vue'
import Team from '../views/Team.vue'
import Settings from '../views/Settings.vue'
const routes = [
  {
    path: '/',
    redirect: '/login'
  },

  {
    path: '/login',
    name: 'Login',
    component: Login
  },

  {
    path: '/signup',
    name: 'Signup',
    component: Signup
  },

  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },

  {
    path: '/tasks',
    name: 'Tasks',
    component: Tasks
  },

  {
    path: '/tasks/new',
    redirect: '/tasks'
  },

  {
    path: '/tasks/:id',
    name: 'TaskDetails',
    component: TaskDetails
  },

  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/team',
    name: 'Team',
    component: Team
  },

  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router