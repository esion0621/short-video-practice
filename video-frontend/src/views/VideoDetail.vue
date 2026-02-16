<template>
  <div class="video-detail">
    <div v-if="video" class="detail-container">
      <div class="video-section">
        <div class="video-player">
          <video
            ref="videoPlayer"
            :src="getFileUrl(video.videoUrl)"
            controls
            @timeupdate="onTimeUpdate"
            @ended="onEnded"
          ></video>
        </div>
        <div class="video-meta">
          <h1 class="title">{{ video.videoTitle }}</h1>
          <div class="meta-row">
            <span class="author">👤 {{ video.nickname || '用户' }}</span>
            <span class="date">{{ formatDate(video.createTime) }}</span>
          </div>
          <p class="desc">{{ video.videoDesc }}</p>
        </div>
      </div>

      <div class="action-sidebar">
        <div class="action-buttons">
          <button
            class="action-btn"
            :class="{ active: liked }"
            @click="handleLike"
            :disabled="!userStore.token"
          >
            <span class="icon">❤️</span>
            <span class="count">{{ video.likeCount }}</span>
          </button>
          <button
            class="action-btn"
            :class="{ active: collected }"
            @click="handleCollect"
            :disabled="!userStore.token"
          >
            <span class="icon">📌</span>
            <span class="count">{{ video.collectCount }}</span>
          </button>
          <button class="action-btn" @click="handleShare" :disabled="!userStore.token">
            <span class="icon">↪️</span>
            <span class="count">{{ video.shareCount }}</span>
          </button>
        </div>

        <div class="recommend-section">
          <h3>相关推荐</h3>
          <div class="recommend-list">
            <div
              v-for="rec in recommendations"
              :key="rec.videoId"
              class="rec-item"
              @click="goToDetail(rec.videoId)"
            >
              <img :src="rec.coverUrl || defaultCover" class="rec-cover" />
              <div class="rec-info">
                <p class="rec-title">{{ rec.videoTitle }}</p>
                <span class="rec-views">👁️ {{ rec.viewCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="loading-state">加载中...</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { videoApi } from '@/api/video'
import { behaviorApi } from '@/api/behavior'
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'
import { getFileUrl } from '@/utils/file'

const props = defineProps({ video: Object })
const coverSrc = computed(() => {
  return getFileUrl(props.video.coverUrl) || defaultCover
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const video = ref(null)
const liked = ref(false)
const collected = ref(false)
const videoPlayer = ref(null)
const watchStartTime = ref(null)
const recommendations = ref([])
const defaultCover = 'https://via.placeholder.com/80x120'

const videoId = route.params.id

onMounted(async () => {
  try {
    video.value = await videoApi.getVideoDetail(videoId)
    watchStartTime.value = Date.now()

    // 获取相关推荐（简单示例：调用热门列表）
    recommendations.value = await videoApi.getHotVideos()
  } catch (e) {
    console.error('加载视频失败', e)
  }
})

onUnmounted(() => {
  if (video.value && userStore.token) {
    // 上报观看时长
    const duration = Math.floor((Date.now() - watchStartTime.value) / 1000)
    behaviorApi.report({
      userId: userStore.userInfo?.userId,
      videoId: video.value.videoId,
      actionType: 1,
      duration,
      progress: videoPlayer.value ? videoPlayer.value.currentTime / video.value.duration : 0,
      deviceType: 'web'
    }).catch(() => {})
  }
})

const onTimeUpdate = () => {
  // 可以定期上报进度
}

const onEnded = () => {
  // 播放结束自动上报一次
  if (video.value && userStore.token) {
    behaviorApi.report({
      userId: userStore.userInfo?.userId,
      videoId: video.value.videoId,
      actionType: 1,
      duration: video.value.duration,
      progress: 1.0,
      deviceType: 'web'
    }).catch(() => {})
  }
}

const handleLike = async () => {
  if (!userStore.token) return
  await behaviorApi.report({
    userId: userStore.userInfo?.userId,
    videoId: video.value.videoId,
    actionType: 2,
    deviceType: 'web'
  })
  liked.value = true
  video.value.likeCount++
}

const handleCollect = async () => {
  if (!userStore.token) return
  await behaviorApi.report({
    userId: userStore.userInfo?.userId,
    videoId: video.value.videoId,
    actionType: 6,
    deviceType: 'web'
  })
  collected.value = true
  video.value.collectCount++
}

const handleShare = async () => {
  if (!userStore.token) return
  await behaviorApi.report({
    userId: userStore.userInfo?.userId,
    videoId: video.value.videoId,
    actionType: 4,
    sharePlatform: 'web',
    deviceType: 'web'
  })
  video.value.shareCount++
}

const goToDetail = (id) => {
  router.push(`/video/${id}`)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${d.getMonth()+1}-${d.getDate()}`
}
</script>

<style scoped>
.video-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;
}

.detail-container {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
}

.video-section {
  flex: 1 1 600px;
  min-width: 0;
}

.video-player {
  background: #000;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 1.5rem;
}
.video-player video {
  width: 100%;
  max-height: 70vh;
  display: block;
}

.video-meta {
  padding: 0 0.5rem;
}
.title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}
.meta-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: var(--text-light);
  margin-bottom: 1rem;
}
.desc {
  color: var(--text);
  line-height: 1.6;
}

.action-sidebar {
  flex: 0 0 280px;
}

.action-buttons {
  background: var(--surface);
  border-radius: 24px;
  padding: 0.75rem 1rem;
  display: flex;
  justify-content: space-around;
  margin-bottom: 2rem;
  box-shadow: var(--shadow);
}
.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: var(--text-light);
  transition: all 0.2s;
  padding: 0.5rem 0.75rem;
  border-radius: 9999px;
}
.action-btn:hover:not(:disabled) {
  background: #f1f5f9;
  color: var(--primary);
}
.action-btn.active {
  color: var(--primary);
}
.action-btn.active .icon {
  transform: scale(1.1);
}
.icon {
  font-size: 1.5rem;
}
.count {
  font-size: 0.8rem;
  font-weight: 500;
}
.action-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.recommend-section {
  background: var(--surface);
  border-radius: 16px;
  padding: 1rem;
  box-shadow: var(--shadow);
}
.recommend-section h3 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
}
.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.rec-item {
  display: flex;
  gap: 0.75rem;
  cursor: pointer;
  transition: background 0.2s;
  padding: 0.5rem;
  border-radius: 8px;
}
.rec-item:hover {
  background: #f1f5f9;
}
.rec-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  background: #e2e8f0;
}
.rec-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.rec-title {
  font-size: 0.9rem;
  font-weight: 600;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.rec-views {
  font-size: 0.8rem;
  color: var(--text-light);
}

.loading-state {
  text-align: center;
  padding: 3rem;
  color: var(--text-light);
}

@media (max-width: 768px) {
  .detail-container {
    flex-direction: column;
  }
  .action-sidebar {
    flex: auto;
  }
  .action-buttons {
    margin-bottom: 1rem;
  }
}
</style>
