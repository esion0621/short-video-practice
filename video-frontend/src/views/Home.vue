<<template>
  <div class="home">
    <!-- 动态背景层（fixed覆盖全屏） -->
    <div class="bg">
      <div class="bg-gradient"></div>
      <div class="bg-orb orb1"></div>
      <div class="bg-orb orb2"></div>
      <div class="bg-orb orb3"></div>
      <div class="bg-orb orb4"></div>
    </div>

    <!-- 内容层 -->
    <div class="content">
      <header class="page-header">
        <h1 class="title">🔥 最新视频</h1>   <!-- 标题改为“最新视频” -->
        <p class="subtitle">看看大家刚刚上传了什么</p>
      </header>

      <div v-if="loading" class="skeleton-grid">
        <div v-for="n in 6" :key="n" class="skeleton-card"></div>
      </div>

      <div v-else class="video-grid">
        <VideoCard
          v-for="video in videos"
          :key="video.videoId"
          :video="video"
          @click="goToDetail(video.videoId)"
        />
      </div>

      <div class="load-more">
        <button @click="loadMore" :disabled="loadingMore" class="btn-load">
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi } from '@/api/video'
import VideoCard from '@/components/VideoCard.vue'

const videos = ref([])
const loading = ref(true)
const loadingMore = ref(false)
const page = ref(1)
const router = useRouter()

onMounted(async () => {
  try {
    // 改为获取最新视频列表（分页，第一页10条）
    const res = await videoApi.getVideoList({ page: 1, size: 10 })
    // 根据后端返回格式调整，可能 res.data.records 或直接 res.records
    // 请根据实际后端响应结构调整
    videos.value = res.records || res.data?.records || res
  } catch (e) {
    console.error('获取视频失败', e)
  } finally {
    loading.value = false
  }
})

const goToDetail = (id) => {
  router.push(`/video/${id}`)
}

const loadMore = async () => {
  loadingMore.value = true
  page.value++
  try {
    const res = await videoApi.getVideoList({ page: page.value, size: 10 })
    const newVideos = res.records || res.data?.records || res
    videos.value = [...videos.value, ...newVideos]
  } catch (e) {
    console.error('加载更多失败', e)
  } finally {
    loadingMore.value = false
  }
}
</script>
<style scoped>
.home {
  position: relative;
  min-height: 100vh;
  padding-bottom: 2rem;
}

/* 动态背景层 - fixed 覆盖整个视口，位于内容下方 */
.bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
}

/* 鲜艳的移动渐变背景 */
.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 40%, #ff6b6b, #feca57, #48dbfb, #ff9ff3, #f368e0, #54a0ff);
  background-size: 300% 300%;
  animation: gradientFlow 20s ease infinite;
  opacity: 0.9;
}

@keyframes gradientFlow {
  0% { transform: translate(0, 0); }
  25% { transform: translate(-10%, -5%); }
  50% { transform: translate(-15%, 0); }
  75% { transform: translate(-5%, 5%); }
  100% { transform: translate(0, 0); }
}

/* 浮动光晕 */
.bg-orb {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(50px);
  filter: blur(50px);
  animation: float 25s infinite alternate ease-in-out;
}

.orb1 {
  width: 60vmax;
  height: 60vmax;
  top: -20vmax;
  left: -10vmax;
  background: rgba(255, 107, 107, 0.3);
  animation-duration: 30s;
}

.orb2 {
  width: 70vmax;
  height: 70vmax;
  bottom: -30vmax;
  right: -20vmax;
  background: rgba(72, 219, 251, 0.3);
  animation-duration: 35s;
  animation-delay: -5s;
}

.orb3 {
  width: 40vmax;
  height: 40vmax;
  top: 30%;
  left: 30%;
  background: rgba(255, 159, 243, 0.3);
  animation-duration: 28s;
  animation-delay: -2s;
}

.orb4 {
  width: 50vmax;
  height: 50vmax;
  top: 10%;
  right: 5%;
  background: rgba(254, 202, 87, 0.3);
  animation-duration: 32s;
  animation-delay: -10s;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(8%, 10%) scale(1.2); }
}

/* 内容层 - 相对定位，z-index 高于背景 */
.content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 1.5rem 2rem;
}

.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 50%, #48dbfb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: inline-block;
  text-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.subtitle {
  color: #2d3748;
  margin-top: 0.25rem;
  font-weight: 400;
}

/* 视频卡片网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .video-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1024px) {
  .video-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 骨架屏动画 */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}
@media (min-width: 768px) {
  .skeleton-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (min-width: 1024px) {
  .skeleton-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
.skeleton-card {
  aspect-ratio: 9 / 16;
  background: linear-gradient(90deg, rgba(255,255,255,0.3) 25%, rgba(255,255,255,0.5) 50%, rgba(255,255,255,0.3) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 16px;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.3);
}
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.load-more {
  margin-top: 2.5rem;
  display: flex;
  justify-content: center;
}
.btn-load {
  background: rgba(255,255,255,0.25);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.4);
  padding: 0.6rem 2rem;
  border-radius: 9999px;
  font-weight: 500;
  color: #2d3748;
  transition: all 0.2s;
  cursor: pointer;
}
.btn-load:hover:not(:disabled) {
  background: rgba(255,255,255,0.4);
  transform: scale(1.05);
  border-color: white;
}
.btn-load:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
