<template>
  <div class="auth-page">
    <!-- 动态背景层（fixed 覆盖全屏） -->
    <div class="bg">
      <div class="bg-gradient"></div>
      <div class="bg-orb orb1"></div>
      <div class="bg-orb orb2"></div>
      <div class="bg-orb orb3"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="auth-card">
      <h2>创建账号</h2>
      <p class="subtitle">加入快视，发现更多精彩</p>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="email" type="email" placeholder="请输入邮箱" required />
        </div>
        <div class="form-group">
          <label>昵称</label>
          <input v-model="nickname" type="text" placeholder="请输入昵称" required />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" required />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input v-model="confirmPassword" type="password" placeholder="请再次输入密码" required />
        </div>
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="auth-footer">
        已有账号？ <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const username = ref('')
const email = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)

const handleSubmit = async () => {
  if (password.value !== confirmPassword.value) {
    alert('两次输入的密码不一致')
    return
  }
  loading.value = true
  try {
    await userStore.register({
      username: username.value,
      email: email.value,
      nickname: nickname.value,
      password: password.value
    })
    router.push('/')
  } catch (e) {
    alert('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
}

.bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 50%, #ff7eb3, #ff758c, #f5b8ea, #a18cd1, #fbc2eb);
  background-size: 200% 200%;
  animation: gradientMove 15s ease infinite;
  opacity: 0.8;
}

@keyframes gradientMove {
  0% { transform: translate(0, 0); }
  25% { transform: translate(-10%, -5%); }
  50% { transform: translate(-20%, 0); }
  75% { transform: translate(-10%, 5%); }
  100% { transform: translate(0, 0); }
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(40px);
  filter: blur(40px);
  animation: float 20s infinite alternate ease-in-out;
}

.orb1 {
  width: 40vmax;
  height: 40vmax;
  top: -10vmax;
  left: -10vmax;
  background: rgba(173, 216, 230, 0.4);
  animation-duration: 25s;
}

.orb2 {
  width: 50vmax;
  height: 50vmax;
  bottom: -15vmax;
  right: -10vmax;
  background: rgba(255, 182, 193, 0.4);
  animation-duration: 30s;
  animation-delay: -5s;
}

.orb3 {
  width: 30vmax;
  height: 30vmax;
  top: 30%;
  left: 40%;
  background: rgba(216, 191, 216, 0.3);
  animation-duration: 22s;
  animation-delay: -2s;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(5%, 8%) scale(1.1); }
}

.auth-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 32px;
  padding: 2.5rem 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.3) inset;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  animation: cardAppear 0.6s ease-out;
}

.auth-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 30px 50px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.5) inset;
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 字体颜色优化 ===== */
.auth-card h2 {
  font-size: 2.2rem;
  font-weight: 700;
  color: #2d3748;               /* 深灰色标题 */
  margin-bottom: 0.5rem;
  text-align: center;
  text-shadow: none;             /* 移除原白色阴影 */
}

.subtitle {
  color: #4a5568;                /* 中深灰副标题 */
  text-align: center;
  margin-bottom: 2rem;
  font-weight: 400;
  text-shadow: none;
  opacity: 1;
}

.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #2d3748;                /* 深灰色标签 */
  letter-spacing: 0.5px;
  text-shadow: none;
}

.form-group input {
  width: 100%;
  padding: 0.9rem 1.2rem;
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 40px;
  font-size: 1rem;
  color: #1a202c;                /* 输入文字深色 */
  outline: none;
  transition: all 0.2s;
}

.form-group input::placeholder {
  color: #718096;                /* 占位符中灰色 */
  font-weight: 300;
}

.form-group input:focus {
  background: rgba(255, 255, 255, 0.35);
  border-color: white;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.4);
}

.btn-submit {
  width: 100%;
  padding: 0.9rem;
  background: linear-gradient(135deg, #ff7eb3, #ff758c, #f5b8ea);
  border: none;
  border-radius: 40px;
  font-size: 1.1rem;
  font-weight: 600;
  color: white;                  /* 按钮文字保持白色 */
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(255, 120, 150, 0.5);
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-submit:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 12px 25px rgba(255, 120, 150, 0.7);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 2rem;
  text-align: center;
  color: #4a5568;                /* 页脚文字深灰 */
  font-weight: 400;
  text-shadow: none;
}

.auth-footer a {
  color: #2d3748;                /* 链接深灰色 */
  font-weight: 600;
  text-decoration: none;
  border-bottom: 1px solid rgba(45, 55, 72, 0.3); /* 下划线匹配文字颜色 */
  transition: border-color 0.2s;
}

.auth-footer a:hover {
  border-bottom-color: #2d3748;
}
</style>
