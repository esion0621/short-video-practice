<template>
  <nav class="navbar">
    <div class="nav-container">
      <div class="logo">
        <router-link to="/">快视</router-link>
      </div>

      <div class="search">
        <span class="search-icon">🔍</span>
        <input type="text" placeholder="搜索视频..." />
      </div>

      <div class="nav-links">
        <template v-if="!token">
          <router-link to="/login" class="btn-link">登录</router-link>
          <router-link to="/register" class="btn btn-primary">注册</router-link>
        </template>
        <template v-else>
          <router-link to="/profile" class="btn-link">个人中心</router-link>
          <a @click="logout" class="btn-link">退出</a>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const token = computed(() => userStore.token)

const logout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 50;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.75rem 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}

.logo a {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.search {
  flex: 1;
  max-width: 400px;
  display: flex;
  align-items: center;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 9999px;
  padding: 0.4rem 1rem;
  transition: box-shadow 0.2s;
}

.search:focus-within {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
  border-color: var(--primary);
}

.search-icon {
  color: var(--text-light);
  margin-right: 0.5rem;
  font-size: 1rem;
}

.search input {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: 0.95rem;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.btn-link {
  color: var(--text);
  font-weight: 500;
  transition: color 0.2s;
  cursor: pointer;
}
.btn-link:hover {
  color: var(--primary);
}

.btn-primary {
  background: var(--primary);
  color: white;
  padding: 0.5rem 1.2rem;
  border-radius: 9999px;
  font-weight: 500;
  transition: background 0.2s;
}
.btn-primary:hover {
  background: var(--primary-dark);
}

@media (max-width: 640px) {
  .nav-container {
    flex-direction: column;
    align-items: stretch;
  }
  .search {
    max-width: none;
  }
  .nav-links {
    justify-content: center;
  }
}
</style>
