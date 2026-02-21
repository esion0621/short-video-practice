import { defineStore } from 'pinia'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(credentials) {
      const response = await userApi.login(credentials)
      this.token = response.token
      this.userInfo = response.user
      localStorage.setItem('token', response.token)
    },
    async register(userData) {
      const response = await userApi.register(userData) 
      this.token = response.token
      this.userInfo = response.user
      localStorage.setItem('token', response.token)
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
