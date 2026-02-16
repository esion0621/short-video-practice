<template>
  <div id="app">
    <NavBar v-if="!$route.meta.hideNavbar" />
    <main class="main-content" :class="{ 'fullscreen': $route.meta.fullscreen }">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
</script>

<style>
:root {
  --primary: #3b82f6;
  --primary-dark: #2563eb;
  --bg: #f8f9fa;
  --surface: #ffffff;
  --text: #1e293b;
  --text-light: #64748b;
  --border: #e2e8f0;
  --radius: 16px;
  --shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background-color: var(--bg);
  color: var(--text);
  line-height: 1.5;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 1rem 1rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.main-content.fullscreen {
  padding: 0;
  max-width: none;
  margin: 0;
}

@media (min-width: 640px) {
  .main-content:not(.fullscreen) {
    padding: 1.5rem 2rem;
  }
}

a {
  text-decoration: none;
  color: inherit;
}

button {
  cursor: pointer;
  border: none;
  background: none;
  font-family: inherit;
}

/* 简单过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
