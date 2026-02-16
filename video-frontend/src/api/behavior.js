import request from './request'

export const behaviorApi = {
  report(data) {
    return request.post('/behaviors', data)
  }
}
