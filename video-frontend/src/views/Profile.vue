<template>
  <div class="profile">
    <!-- 用户信息卡片 -->
    <div class="user-card" v-if="userInfo">
      <AvatarUploader
        :src="userInfo.avatarUrl"
        @upload-success="handleAvatarUpdate"
      />
      <div class="info">
        <h2>{{ userInfo.nickname || userInfo.username }}</h2>
        <p class="username">@{{ userInfo.username }}</p>
        <p class="email">{{ userInfo.email }}</p>
        <p class="bio">{{ userInfo.signature || '这个人很懒，什么都没留下' }}</p>
      </div>
      <div class="stats">
        <div class="stat-item">
          <span class="count">{{ userVideos.length }}</span>
          <span class="label">视频</span>
        </div>
        <div class="stat-item">
          <span class="count">{{ likedVideos.length }}</span>
          <span class="label">获赞</span>
        </div>
        <div class="stat-item">
          <span class="count">{{ collectedVideos.length }}</span>
          <span class="label">收藏</span>
        </div>
      </div>
    </div>

    <!-- 上传按钮 -->
    <div class="upload-section">
      <button @click="goToUpload" class="btn-upload">+ 上传视频</button>
    </div>

    <!-- 选项卡 -->
    <div class="tabs">
      <button :class="{ active: activeTab === 'videos' }" @click="activeTab = 'videos'">我的视频</button>
      <button :class="{ active: activeTab === 'likes' }" @click="activeTab = 'likes'">点赞</button>
      <button :class="{ active: activeTab === 'collects' }" @click="activeTab = 'collects'">收藏</button>
    </div>

    <!-- 选项卡内容 -->
    <div class="tab-content">
      <!-- 我的视频 -->
      <div v-if="activeTab === 'videos'" class="video-grid">
        <VideoCard
          v-for="video in userVideos"
          :key="video.videoId"
          :video="video"
          @click="goToDetail(video.videoId)"
        />
        <div v-if="userVideos.length === 0" class="empty">暂无视频，快去上传吧～</div>
      </div>

      <!-- 点赞视频 -->
      <div v-if="activeTab === 'likes'" class="video-grid">
        <VideoCard
          v-for="video in likedVideos"
          :key="video.videoId"
          :video="video"
          @click="goToDetail(video.videoId)"
        />
        <div v-if="likedVideos.length === 0" class="empty">暂无点赞视频</div>
      </div>

      <!-- 收藏视频 -->
      <div v-if="activeTab === 'collects'" class="video-grid">
        <VideoCard
          v-for="video in collectedVideos"
          :key="video.videoId"
          :video="video"
          @click="goToDetail(video.videoId)"
        />
        <div v-if="collectedVideos.length === 0" class="empty">暂无收藏视频</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import axios from 'axios'
import VideoCard from '@/components/VideoCard.vue'
import { jwtDecode } from 'jwt-decode'
import AvatarUploader from '@/components/AvatarUploader.vue'

const userStore = useUserStore()
const router = useRouter()
const defaultAvatar = 'https://via.placeholder.com/100x100?text=User'

const userInfo = ref(null)
const userVideos = ref([])
const likedVideos = ref([])
const collectedVideos = ref([])
const activeTab = ref('videos')

const handleAvatarUpdate = (newAvatarUrl) => {
  // 可选：重新加载用户信息或更新本地状态
  // 由于 store 已经更新，可以重新获取最新数据
  fetchUserInfo() // 重新获取用户信息（如果需要）
}

const fetchUserInfo = async () => {
  const token = userStore.token
  if (!token) {
    router.push('/login')
    return
  }
  try {
    const decoded = jwtDecode(token)
    const userId = decoded.sub
    const res = await axios.get(`/api/users/${userId}`)
    userInfo.value = res.data
    userStore.setUserInfo(res.data)  // 存入 store
  } catch (e) {
    console.error('获取用户信息失败', e)
    router.push('/login')
  }
}

// 获取用户视频列表
const fetchUserVideos = async () => {
  try {
    const res = await axios.get(`/api/users/${userInfo.value.userId}/videos`)
    userVideos.value = res.data
  } catch (e) {
    console.error('获取视频列表失败', e)
  }
}

// 获取点赞视频
const fetchLikedVideos = async () => {
  try {
    const res = await axios.get(`/api/users/${userInfo.value.userId}/likes`)
    likedVideos.value = res.data
  } catch (e) {
    console.error('获取点赞视频失败', e)
  }
}

// 获取收藏视频
const fetchCollectedVideos = async () => {
  try {
    const res = await axios.get(`/api/users/${userInfo.value.userId}/collects`)
    collectedVideos.value = res.data
  } catch (e) {
    console.error('获取收藏视频失败', e)
  }
}

onMounted(async () => {
  await fetchUserInfo()
  if (userInfo.value) {
    await Promise.all([
      fetchUserVideos(),
      fetchLikedVideos(),
      fetchCollectedVideos()
    ])
  }
})

const goToUpload = () => {
  router.push('/upload')
}

const goToDetail = (id) => {
  router.push(`/video/${id}`)
}
</script>

<style scoped>
.profile {
  max-width: 1000px;
  margin: 0 auto;
  padding: 1rem;
}

.user-card {
  background: white;
  border-radius: 24px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  flex-wrap: wrap;
}

.avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.info {
  flex: 1;
}

.info h2 {
  font-size: 1.8rem;
  margin-bottom: 0.25rem;
  color: #1a202c;
}

.username {
  color: #718096;
  margin-bottom: 0.5rem;
}

.email {
  color: #4a5568;
  margin-bottom: 0.5rem;
}

.bio {
  color: #2d3748;
  font-size: 0.95rem;
  max-width: 500px;
}

.stats {
  display: flex;
  gap: 2rem;
  background: #f7fafc;
  padding: 1rem 2rem;
  border-radius: 40px;
}

.stat-item {
  text-align: center;
}

.stat-item .count {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.stat-item .label {
  font-size: 0.875rem;
  color: #718096;
}

.upload-section {
  text-align: right;
  margin-bottom: 1.5rem;
}

.btn-upload {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  color: white;
  padding: 0.6rem 2rem;
  border: none;
  border-radius: 9999px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-upload:hover {
  opacity: 0.9;
}

.tabs {
  display: flex;
  gap: 1rem;
  border-bottom: 2px solid #e2e8f0;
  margin-bottom: 2rem;
}

.tabs button {
  padding: 0.5rem 1rem;
  font-weight: 500;
  color: #718096;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  cursor: pointer;
  transition: all 0.2s;
}

.tabs button.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

.tab-content {
  min-height: 300px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 3rem;
  color: #a0aec0;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .user-card {
    flex-direction: column;
    text-align: center;
  }
  .stats {
    width: 100%;
    justify-content: center;
  }
}
</style>
