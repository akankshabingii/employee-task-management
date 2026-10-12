import { createRouter, createWebHistory } from "vue-router";

import Login from "../views/Login.vue";
import Signup from "../views/Signup.vue";
import Dashboard from "../views/Dashboard.vue";
import Tasks from "../views/Tasks.vue";
import TaskDetails from "../views/TaskDetails.vue";
import Profile from "../views/Profile.vue";
import Team from "../views/Team.vue";
import Settings from "../views/Settings.vue";
import Users from "../views/Users.vue";
import Teams from "../views/Teams.vue";
import NewTask from "../views/NewTask.vue";

const routes = [
  { path: "/", redirect: "/dashboard" },

  {
    path: "/login",
    name: "Login",
    component: Login,
    meta: { guestOnly: true },
  },
  {
    path: "/signup",
    name: "Signup",
    component: Signup,
    meta: { guestOnly: true },
  },

  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard,
    meta: { requiresAuth: true },
  },

  {
    path: "/tasks",
    name: "Tasks",
    component: Tasks,
    meta: { requiresAuth: true },
  },

  {
    path: "/tasks/new",
    name: "NewTask",
    component: NewTask,
    meta: { requiresAuth: true },
  },

  {
    path: "/tasks/:id",
    name: "TaskDetails",
    component: TaskDetails,
    meta: { requiresAuth: true },
  },

  {
    path: "/profile",
    name: "Profile",
    component: Profile,
    meta: { requiresAuth: true },
  },

  {
    path: "/team",
    name: "Team",
    component: Team,
    meta: { requiresAuth: true },
  },

  {
    path: "/settings",
    name: "Settings",
    component: Settings,
    meta: { requiresAuth: true },
  },

  {
    path: "/users",
    name: "Users",
    component: Users,
    meta: { requiresAuth: true, adminOnly: true },
  },

  {
    path: "/teams",
    name: "Teams",
    component: Teams,
    meta: { requiresAuth: true, adminOnly: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (to.meta.requiresAuth && !token) return "/login";
  if (to.meta.guestOnly && token) return "/dashboard";
  if (to.meta.adminOnly && role !== "ADMIN") return "/dashboard";

  return true;
});

export default router;
