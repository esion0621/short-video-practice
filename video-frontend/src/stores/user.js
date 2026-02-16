import { defineStore } from 'pinia'
import { userApi } from '@/api/user'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  actions: {
    async login(credentials) {
      const token = await userApi.login(credentials)
      this.token = token
      localStorage.setItem('token', token)
      // 获取用户信息（需要解析 token 获取 userId，或后端返回用户信息）
      // 假设登录接口返回 token 后，我们使用另一个接口获取用户信息
      // 这里简单起见，我们调用 /api/users/profile 接口（需后端实现）
      // 如果没有，可以暂时用假数据，或者登录接口直接返回用户信息
      // 我们暂时先不实现，Profile 页面会单独请求用户信息
    },
    async register(userData) {
      const token = await userApi.register(userData)
      this.token = token
      localStorage.setItem('token', token)
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    },
    setUserInfo(info) {
      this.userInfo = info
    }
  }
})
