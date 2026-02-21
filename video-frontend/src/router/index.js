import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import VideoDetail from '@/views/VideoDetail.vue'
import Profile from '@/views/Profile.vue'
import Upload from '@/views/Upload.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login, meta: { hideNavbar: true } },
    { path: '/register', component: Register, meta: { hideNavbar: true } },
    { path: '/video/:id', component: VideoDetail },
    { path: '/profile', component: Profile, meta: { requiresAuth: true } },
    { path: '/upload', component: Upload, meta: { requiresAuth: true } }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
