
<template>
  <div
    class="app-shell"
    :class="{ 'auth-shell': isAuthPage }"
  >

    <!-- Show sidebar only for authenticated application pages -->
    <Sidebar v-if="!isAuthPage" />

    <main
      class="main-content"
      :class="{ 'full-width': isAuthPage }"
    >
      <router-view />
    </main>

  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'

export default {
  components: {
    Sidebar
  },

  computed: {
    isAuthPage() {
      return (
        this.$route.name === 'Login' ||
        this.$route.name === 'Register'
      )
    }
  }
}
</script>

