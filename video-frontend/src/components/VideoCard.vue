<template>
  <div class="video-card" @click="$emit('click')">
    <div class="cover">
      <img :src="getFileUrl(video.coverUrl) || defaultCover" :alt="video.videoTitle" />
      <span class="duration">{{ formatDuration(video.duration) }}</span>
    </div>
    <div class="info">
      <div class="user">
        <img :src="video.avatarUrl || defaultAvatar" class="avatar" />
        <span class="username">{{ video.nickname || '用户' }}</span>
      </div>
      <h3 class="title">{{ video.videoTitle }}</h3>
      <div class="stats">
        <span>❤️ {{ video.likeCount || 0 }}</span>
        <span>👁️ {{ video.viewCount || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  video: { type: Object, required: true }
})
defineEmits(['click'])

const defaultCover = 'https://via.placeholder.com/300x169?text=No+Cover' // 匹配16:9
const defaultAvatar = 'https://via.placeholder.com/40x40?text=User'

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getFileUrl = (path) => {
  if (!path) return null;
  // 如果已经是完整 URL，则直接返回（例如默认图片）
  if (path.startsWith('http')) return path;
  // 否则添加后端文件接口前缀
  return `/api/files?path=${encodeURIComponent(path)}`;
}
</script>

<style scoped>
.video-card {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--radius, 16px);
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s, background 0.2s;
}
.video-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.5);
}

.cover {
  position: relative;
  aspect-ratio: 16 / 9; /* 从 9/16 改为 16/9，卡片变扁 */
  background: rgba(0,0,0,0.1);
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.video-card:hover .cover img {
  transform: scale(1.05);
}
.duration {
  position: absolute;
  bottom: 6px;
  right: 6px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  color: white;
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 4px;
}

.info {
  padding: 0.75rem;
}

.user {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}
.avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  background: #e2e8f0;
}
.username {
  font-size: 0.8rem;
  color: #2d3748;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.title {
  font-size: 0.95rem;
  font-weight: 600;
  line-height: 1.3;
  margin-bottom: 6px;
  color: #1a202c;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.stats {
  display: flex;
  gap: 0.75rem;
  font-size: 0.75rem;
  color: #4a5568;
}
</style>
