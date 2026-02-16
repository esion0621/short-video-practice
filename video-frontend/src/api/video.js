import request from './request'

export const videoApi = {
  getVideoList(params) {
    return request.get('/videos/list', { params })
  },
  getVideoDetail(videoId) {
    return request.get(`/videos/${videoId}`)
  },
  getRecommendations(userId, page = 1, size = 10) {
    return request.get(`/recommends/${userId}`, { params: { page, size } })
  },
  getHotVideos() {
    return request.get('/recommends/hot')
  }
}
