<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- 导航栏 -->
    <Navbar />
    
    <!-- 主内容区域 -->
    <main class="min-h-screen">
      <router-view />
    </main>
    
    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import Navbar from '@/components/layout/Navbar.vue'
import Footer from '@/components/layout/Footer.vue'
</script> 