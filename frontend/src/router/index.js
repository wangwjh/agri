import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomePage.vue')
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/SearchPage.vue')
    },
    {
      path: '/upload',
      name: 'upload',
      component: () => import('@/views/UploadPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/transactions',
      name: 'transactions',
      component: () => import('@/views/TransactionPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/account',
      name: 'account',
      component: () => import('@/views/AccountPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginPage.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterPage.vue')
    },
    {
      path: '/asset/:id',
      name: 'asset-detail',
      component: () => import('@/views/AssetDetailPage.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfilePage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/admin/AdminDashboard.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin) {
    // 检查是否有管理员权限
    const user = JSON.parse(sessionStorage.getItem('user') || '{}')
    const adminRoles = ['REVIEWER', 'PUBLISHER', 'OPERATOR', 'SUPER_ADMIN']
    
    if (!user.role || !adminRoles.includes(user.role)) {
      alert('权限不足，无法访问管理后台')
      next('/')
      return
    }
    next()
  } else {
    next()
  }
})

export default router 