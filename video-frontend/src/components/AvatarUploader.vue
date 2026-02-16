<template>
  <div class="avatar-uploader" @click="triggerFileInput">
    <img :src="avatarSrc" alt="avatar" class="avatar" />
    <div class="overlay" v-if="uploading">
      <span>上传中...</span>
    </div>
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleFileChange"
    />
    <button class="edit-btn">更换头像</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { getFileUrl } from '@/utils/file'

const props = defineProps({
  src: { type: String, default: '' }
})
const emit = defineEmits(['upload-success'])

const userStore = useUserStore()
const fileInput = ref(null)
const uploading = ref(false)

const avatarSrc = computed(() => {
  return props.src ? getFileUrl(props.src) : '/default-avatar.png'
})

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('userId', userStore.userInfo.userId)
  formData.append('file', file)

  uploading.value = true
  try {
    const res = await axios.post('/api/users/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    // 更新 store 中的用户信息
    userStore.setUserInfo(res.data)
    emit('upload-success', res.data.avatarUrl)
  } catch (err) {
    console.error('头像上传失败', err)
    alert('上传失败，请重试')
  } finally {
    uploading.value = false
    // 清空 input 以便再次选择同一文件
    fileInput.value.value = ''
  }
}
</script>

<style scoped>
.avatar-uploader {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.edit-btn {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 0.8rem;
  cursor: pointer;
  white-space: nowrap;
}

.edit-btn:hover {
  background: #2563eb;
}
</style>
