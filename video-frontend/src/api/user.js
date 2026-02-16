import request from './request'

export const userApi = {
  register(data) {
    return request.post('/users/register', data)
  },
  login(data) {
    return request.post('/users/login', data)
  },
  getUserInfo(userId) {
    return request.get(`/users/${userId}`)
  },
  updateProfile(data) {
    return request.put('/users/profile', data)
  }
}
