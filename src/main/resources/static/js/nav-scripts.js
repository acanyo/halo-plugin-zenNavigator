/**
 * 导航页面脚本
 */

// 打印作者信息
console.log('%c禅导航-Handsome %chttps://lik.cc/', 
    'background:#35495e; color:#fff; border-radius:3px 0 0 3px; padding:2px 8px;', 
    'background:#41b883; color:#fff; border-radius:0 3px 3px 0; padding:2px 8px;'
);

// 等待DOM加载完成
document.addEventListener('DOMContentLoaded', () => {
  // 初始化加载动画
  initLoading();
  
  // 初始化所有功能
  setupThemeToggle();
  setupBackToTop();
  setupMobileMenu();
  setupDynamicIsland();
  setupBackground();
  setupCategoryToggle();
  setupSiteCards();
  setupTooltips();
  setupLazyLoad();
  
  // 注册事件监听器
  registerEventListeners();
});

/**
 * 初始化加载动画
 */
function initLoading() {
  const loader = document.getElementById('page-loader');
  if (!loader) return;
  
  const hideLoader = () => {
    loader.classList.add('opacity-0');
    setTimeout(() => loader.classList.add('hidden'), 500);
  };
  
  window.addEventListener('load', () => setTimeout(hideLoader, 300));
  if (document.readyState === 'complete') hideLoader();
}

/**
 * 设置主题切换功能
 */
function setupThemeToggle() {
  const themeToggle = document.getElementById('theme-toggle');
  const themeIcon = document.getElementById('theme-icon');
  
  if (!themeToggle) return;
  
  const savedTheme = localStorage.getItem('theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const currentTheme = savedTheme || (window.navigationData?.theme) || (prefersDark ? 'dark' : 'light');
  
  document.documentElement.classList.remove('dark');
  document.body.classList.remove('dark');
  setTimeout(() => applyTheme(currentTheme), 0);
  
  const toggleTheme = () => {
    const newTheme = document.documentElement.classList.contains('dark') ? 'light' : 'dark';
    applyTheme(newTheme);
    localStorage.setItem('theme', newTheme);
    
    if (themeIcon) {
      themeIcon.classList.add('animate-spin');
      setTimeout(() => themeIcon.classList.remove('animate-spin'), 500);
    }
    
    setupBackground();
  };
  
  themeToggle.addEventListener('click', toggleTheme);
  
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    if (!localStorage.getItem('theme')) {
      applyTheme(e.matches ? 'dark' : 'light');
    }
  });
}

/**
 * 应用主题
 */
function applyTheme(theme) {
  const isDark = theme === 'dark';
  document.documentElement.classList.toggle('dark', isDark);
  document.body.classList.toggle('dark', isDark);
  
  updateThemeIcon(theme);
  updateBackgroundOpacity(theme);
  document.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme } }));
}

/**
 * 更新主题图标
 */
function updateThemeIcon(theme) {
  const themeIcon = document.getElementById('theme-icon');
  if (!themeIcon) return;
  
  const icon = theme === 'dark' ? 
    '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" /></svg>' :
    '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" /></svg>';
  
  themeIcon.innerHTML = icon;
}

/**
 * 设置返回顶部按钮
 */
function setupBackToTop() {
  const backToTopButton = document.getElementById('back-to-top');
  if (!backToTopButton) return;
  
  const toggleButton = () => {
    const shouldShow = window.scrollY > 300;
    backToTopButton.classList.toggle('opacity-0', !shouldShow);
    backToTopButton.classList.toggle('pointer-events-none', !shouldShow);
    backToTopButton.classList.toggle('opacity-100', shouldShow);
  };
  
  window.addEventListener('scroll', toggleButton);
  backToTopButton.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
}

/**
 * 设置移动端菜单
 */
function setupMobileMenu() {
  const menuButton = document.getElementById('mobile-menu-button');
  const mobileMenu = document.getElementById('mobile-menu');
  const menuIcon = document.getElementById('menu-icon');
  if (!menuButton || !mobileMenu || !menuIcon) return;
  
  const menuIcons = {
    open: `<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>`,
    close: `<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
           </svg>`
  };
  
  const toggleMenu = (show) => {
    mobileMenu.classList.toggle('hidden', !show);
    setTimeout(() => mobileMenu.classList.toggle('opacity-0', !show), show ? 10 : 0);
    menuIcon.innerHTML = show ? menuIcons.open : menuIcons.close;
  };
  
  menuButton.addEventListener('click', () => {
    const isOpen = !mobileMenu.classList.contains('hidden');
    toggleMenu(!isOpen);
  });
  
  document.addEventListener('click', (event) => {
    if (!mobileMenu.classList.contains('hidden') && 
        !mobileMenu.contains(event.target) && 
        !menuButton.contains(event.target)) {
      toggleMenu(false);
    }
  });
  
  mobileMenu.querySelectorAll('a').forEach(item => {
    item.addEventListener('click', () => toggleMenu(false));
  });
}

/**
 * 获取当前时间的问候语
 */
function getCurrentGreeting() {
    const hour = new Date().getHours();
    let greeting = '';
    
    if (hour >= 5 && hour < 12) {
        greeting = '早上好！开启元气满满的一天 ✨';
    } else if (hour >= 12 && hour < 14) {
        greeting = '中午好！记得小憩一下哦 🌞';
    } else if (hour >= 14 && hour < 18) {
        greeting = '下午好！喝杯咖啡提提神吧 ☕';
    } else if (hour >= 18 && hour < 22) {
        greeting = '晚上好！放松一下享受生活 🌙';
    } else {
        greeting = '夜深了，注意休息哦 🌠';
    }
    
    return {
        greeting,
        hour
    };
}

/**
 * 设置灵动岛功能
 */
function setupDynamicIsland() {
  const island = document.getElementById('dynamic-island');
  const notificationArea = document.getElementById('notification-area');
  const closeNotificationBtn = document.getElementById('close-notification');
  
  if (!island || !notificationArea || !closeNotificationBtn) return;
  if (window.navigationData?.enableSmart === false) {
    island.style.display = 'none';
    return;
  }
  
  island.style.display = 'flex';
  let isAnimating = false;
  let currentAnimation = null;
  
  function showNotification(title, message, icon = 'ri-notification-4-line', useHtml = false) {
    if (isAnimating) return;
    isAnimating = true;
    
    const titleElement = notificationArea.querySelector('.notification-title');
    const contentElement = notificationArea.querySelector('.notification-content');
    const iconElement = notificationArea.querySelector('.notification-icon i');
    
    if (!titleElement || !contentElement || !iconElement) return;
    
    titleElement.textContent = title;
    useHtml ? contentElement.innerHTML = message : contentElement.textContent = message;
    iconElement.className = `${icon} text-primary-500 dark:text-primary-400`;
    
    notificationArea.classList.remove('notification-in', 'notification-out', 'opacity-0', 'pointer-events-none');
    notificationArea.classList.add('opacity-100');
    
    requestAnimationFrame(() => {
      notificationArea.classList.add('notification-in');
      island.classList.add('island-active');
    });
    
    if (!useHtml) {
      currentAnimation = setTimeout(() => closeNotification(), 5000);
    }
  }
  
  function closeNotification() {
    if (!isAnimating) return;
    
    notificationArea.classList.remove('notification-in');
    island.classList.remove('island-active');
    notificationArea.classList.add('notification-out');
    
    setTimeout(() => {
      notificationArea.classList.remove('opacity-100', 'notification-out');
      notificationArea.classList.add('opacity-0', 'pointer-events-none');
      isAnimating = false;
    }, 500);
    
    if (currentAnimation) {
      clearTimeout(currentAnimation);
      currentAnimation = null;
    }
  }
  
  setTimeout(() => {
    try {
      if (window.navigationData?.content?.trim()) {
        showNotification('📢 通知', window.navigationData.content, 'ri-message-3-line', true);
      } else {
        const greeting = getCurrentGreeting();
        showNotification('👋 温馨问候', greeting.greeting, 'ri-heart-smile-line');
      }
    } catch (error) {}
  }, 1000);
  
  island.addEventListener('click', () => {
    if (isAnimating) {
      closeNotification();
    } else {
      try {
        if (window.navigationData?.content?.trim()) {
          showNotification('📢 通知', window.navigationData.content, 'ri-message-3-line', true);
        } else {
          const greeting = getCurrentGreeting();
          showNotification('👋 温馨问候', greeting.greeting, 'ri-heart-smile-line');
        }
      } catch (error) {}
    }
  });
  
  closeNotificationBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    closeNotification();
  });
  
  document.addEventListener('click', (e) => {
    if (isAnimating && !notificationArea.contains(e.target) && !island.contains(e.target)) {
      closeNotification();
    }
  });
  
  window.showNotification = showNotification;
}

/**
 * 设置分类折叠功能
 */
function setupCategoryToggle() {
  document.querySelectorAll('.category-toggle').forEach(toggle => {
    toggle.addEventListener('click', () => {
      const category = toggle.closest('div[class*="bg-white"]');
      if (!category) return;
      
      const content = category.querySelector('.category-content');
      const icon = toggle.querySelector('svg');
      
      if (!content || !icon) return;
      
      if (content.style.display === 'none') {
        content.style.display = '';
        icon.style.transform = 'rotate(180deg)';
      } else {
        content.style.display = 'none';
        icon.style.transform = 'rotate(0deg)';
      }
    });
  });
}

/**
 * 设置站点卡片交互效果
 */
function setupSiteCards() {
  const siteCards = document.querySelectorAll('.site-card');
  
  siteCards.forEach(card => {
    card.addEventListener('keydown', (event) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        const url = card.getAttribute('data-url');
        if (url) window.open(url, '_blank');
      }
    });
    
    card.addEventListener('mousedown', () => {
      card.classList.add('scale-95');
    });
    
    card.addEventListener('mouseup', () => {
      card.classList.remove('scale-95');
    });
    
    card.addEventListener('mouseleave', () => {
      card.classList.remove('scale-95');
    });
  });
}

/**
 * 设置工具提示
 */
function setupTooltips() {
  const tooltipElements = document.querySelectorAll('[data-tooltip]');
  
  tooltipElements.forEach(element => {
    const tooltipText = element.getAttribute('data-tooltip');
    if (!tooltipText) return;
    
    const tooltip = document.createElement('div');
    tooltip.className = 'absolute z-50 px-2 py-1 text-xs text-white bg-gray-800 rounded shadow-lg opacity-0 pointer-events-none transition-opacity duration-200 whitespace-nowrap';
    tooltip.textContent = tooltipText;
    
    element.appendChild(tooltip);
    
    element.addEventListener('mouseenter', () => {
      tooltip.classList.remove('opacity-0');
      tooltip.classList.add('opacity-100');
      
      const rect = element.getBoundingClientRect();
      tooltip.style.bottom = 'calc(100% + 5px)';
      tooltip.style.left = '50%';
      tooltip.style.transform = 'translateX(-50%)';
    });
    
    element.addEventListener('mouseleave', () => {
      tooltip.classList.remove('opacity-100');
      tooltip.classList.add('opacity-0');
    });
  });
}

/**
 * 设置背景图片或视频
 */
function setupBackground() {
  const bgContainer = document.getElementById('bg-container');
  if (!bgContainer) return;
  
  bgContainer.innerHTML = '';
  let bgMedia = [];
  
  if (window.navigationData && window.navigationData.bgImages) {
    if (Array.isArray(window.navigationData.bgImages) && window.navigationData.bgImages.length > 0) {
      bgMedia = window.navigationData.bgImages;
    } else if (typeof window.navigationData.bgImages === 'string') {
      bgMedia = [window.navigationData.bgImages];
    }
  } else {
    return;
  }
  
  if (bgMedia.length === 0) return;
  
  const randomIndex = Math.floor(Math.random() * bgMedia.length);
  const selectedMedia = bgMedia[randomIndex];
  
  const isVideo = selectedMedia.toLowerCase().endsWith('.mp4') || 
                  selectedMedia.toLowerCase().endsWith('.webm') || 
                  selectedMedia.toLowerCase().endsWith('.ogg');
  
  isVideo ? setupVideoBackground(selectedMedia, bgContainer) : setupImageBackground(selectedMedia, bgContainer);
}

/**
 * 设置视频背景
 */
function setupVideoBackground(videoUrl, container) {
  const video = document.createElement('video');
  video.className = 'absolute inset-0 w-full h-full object-cover opacity-0 transition-opacity duration-1000 bg-video';
  video.autoplay = true;
  video.loop = true;
  video.muted = true;
  video.playsInline = true;
  
  const source = document.createElement('source');
  source.src = videoUrl;
  source.type = `video/${videoUrl.split('.').pop().toLowerCase()}`;
  
  video.appendChild(source);
  container.appendChild(video);
  
  video.addEventListener('loadeddata', () => {
    updateMediaOpacity(video);
  });
  
  video.addEventListener('error', () => {
    if (!videoUrl.startsWith('http') && !videoUrl.startsWith('/')) {
      source.src = '/' + videoUrl;
      video.load();
    }
  });
}

/**
 * 设置图片背景
 */
function setupImageBackground(imageUrl, container) {
  const bgImage = document.createElement('div');
  bgImage.className = 'absolute inset-0 bg-cover bg-center opacity-0 transition-opacity duration-1000';
  bgImage.style.backgroundImage = `url(${imageUrl})`;
  container.appendChild(bgImage);
  
  const img = new Image();
  img.onload = () => {
    updateMediaOpacity(bgImage);
  };
  
  img.onerror = () => {
    if (!imageUrl.startsWith('http') && !imageUrl.startsWith('/')) {
      const altImage = '/' + imageUrl;
      bgImage.style.backgroundImage = `url(${altImage})`;
      const altImg = new Image();
      altImg.onload = () => updateMediaOpacity(bgImage);
      altImg.src = altImage;
    }
  };
  
  img.src = imageUrl;
}

/**
 * 更新背景图片/视频透明度
 */
function updateBackgroundOpacity(theme) {
  const bgContainer = document.getElementById('bg-container');
  if (!bgContainer) return;
  
  const bgImage = bgContainer.querySelector('div');
  const bgVideo = bgContainer.querySelector('video');
  
  if (bgImage) updateMediaOpacity(bgImage, theme);
  if (bgVideo) updateMediaOpacity(bgVideo, theme);
}

/**
 * 更新媒体元素的透明度
 */
function updateMediaOpacity(mediaElement) {
    const transparency = window.navigationData?.transparency || 10;
    mediaElement.style.opacity = transparency / 100;
}

/**
 * 设置图片懒加载
 */
function setupLazyLoad() {
  if ('IntersectionObserver' in window) {
    const lazyImages = document.querySelectorAll('img[loading="lazy"]');
    
    const imageObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target;
          const src = img.getAttribute('data-src');
          
          if (src) {
            img.src = src;
            img.removeAttribute('data-src');
          }
          
          observer.unobserve(img);
        }
      });
    });
    
    lazyImages.forEach(img => {
      imageObserver.observe(img);
    });
  }
}

/**
 * 注册全局事件监听器
 */
function registerEventListeners() {
  const darkModeMediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  darkModeMediaQuery.addEventListener('change', (e) => {
    const savedTheme = localStorage.getItem('theme');
    if (!savedTheme) {
      applyTheme(e.matches ? 'dark' : 'light');
    }
  });
} 