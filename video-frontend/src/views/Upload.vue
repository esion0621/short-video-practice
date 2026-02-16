<template>
  <div class="upload-page">
    <h1>上传视频</h1>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>标题</label>
        <input v-model="form.title" required placeholder="给视频起个标题" />
      </div>
      <div class="form-group">
        <label>描述</label>
        <textarea v-model="form.desc" placeholder="介绍一下视频内容" rows="3"></textarea>
      </div>
      <div class="form-group">
        <label>分类</label>
        <select v-model="form.categoryId">
          <option value="1">娱乐</option>
          <option value="2">音乐</option>
          <option value="3">生活</option>
          <option value="4">知识</option>
          <option value="5">游戏</option>
          <option value="6">影视</option>
        </select>
      </div>
      <div class="form-group">
        <label>视频文件</label>
        <input type="file" accept="video/*" @change="onVideoChange" required />
        <div v-if="videoFile" class="file-info">{{ videoFile.name }} ({{ (videoFile.size / 1024 / 1024).toFixed(2) }} MB)</div>
      </div>
      <div class="form-group">
        <label>封面（可选，不传则自动截取第3秒）</label>
        <input type="file" accept="image/*" @change="onCoverChange" />
        <div v-if="coverFile" class="file-info">{{ coverFile.name }}</div>
      </div>
      <button type="submit" :disabled="uploading" class="btn-submit">
        {{ uploading ? '上传中...' : '发布视频' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import axios from 'axios'

const userStore = useUserStore()
const router = useRouter()
const uploading = ref(false)

const form = ref({
  title: '',
  desc: '',
  categoryId: 1,
  duration: 0,
  format: ''
})

const videoFile = ref(null)
const coverFile = ref(null)

const onVideoChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  videoFile.value = file

  // 获取视频时长
  const video = document.createElement('video')
  video.preload = 'metadata'
  video.onloadedmetadata = () => {
    form.value.duration = Math.round(video.duration)
    form.value.format = file.name.split('.').pop()
    URL.revokeObjectURL(video.src)
  }
  video.src = URL.createObjectURL(file)
}

const onCoverChange = (e) => {
  coverFile.value = e.target.files[0]
}

const handleSubmit = async () => {
  if (!videoFile.value) {
    alert('请选择视频文件')
    return
  }
  if (!form.value.title) {
    alert('请输入标题')
    return
  }

  uploading.value = true

  const formData = new FormData()
  formData.append('video', videoFile.value)
  if (coverFile.value) {
    formData.append('cover', coverFile.value)
  }
  formData.append('data', new Blob([JSON.stringify({
    title: form.value.title,
    desc: form.value.desc,
    userId: userStore.userInfo.userId,
    categoryId: form.value.categoryId,
    duration: form.value.duration,
    format: form.value.format
  })], { type: 'application/json' }))

  try {
    await axios.post('/api/videos/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    alert('上传成功！')
    router.push('/profile')
  } catch (err) {
    console.error(err)
    alert('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.upload-page {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.upload-page h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #1a202c;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #2d3748;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.1);
}

.file-info {
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #718096;
}

.btn-submit {
  width: 100%;
  padding: 0.9rem;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  color: white;
  border: none;
  border-radius: 40px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-submit:hover:not(:disabled) {
  opacity: 0.9;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
