<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { VPageHeader, VCard, VButton, VSpace, VLoading, Dialog, Toast, VAvatar } from "@halo-dev/components";
import { apiClient } from "@/utils/api-client";
import RiSaveLine from "~icons/ri/save-line";
import RiArrowLeftLine from "~icons/ri/arrow-left-line";
import RiAddLine from "~icons/ri/add-line";
import RiDeleteBin5Line from "~icons/ri/delete-bin-5-line";
import RiExternalLinkLine from "~icons/ri/external-link-line";
import RiEyeLine from "~icons/ri/eye-line";

const router = useRouter();
const loading = ref(true);
const saving = ref(false);
const previewLogo = ref("");

const formState = reactive({
  siteName: "导航站",
  logo: "",
  theme: "light",
  transparency: 0.1,
  enableSmart: true,
  bgImages: [] as string[],
  Iconfont: "",
  content: "",
});

onMounted(async () => {
  await fetchSettings();
});

const fetchSettings = async () => {
  loading.value = true;
  try {
    const { data } = await apiClient.get("/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/settings");
    if (data) {
      formState.siteName = data.siteName || "导航站";
      formState.logo = data.logo || "";
      formState.theme = data.theme || "light";
      formState.transparency = data.transparency || 0.1;
      formState.enableSmart = data.enableSmart !== false;
      formState.bgImages = data.bgImages || [];
      formState.Iconfont = data.Iconfont || "";
      formState.content = data.content || "";
      updatePreviewLogo();
    }
  } catch (e) {
    console.error("获取设置失败", e);
    Toast.error("获取设置失败");
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formState.siteName) {
    Toast.error("网站名称不能为空");
    return;
  }
  
  saving.value = true;
  try {
    await apiClient.put("/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/settings", formState);
    Toast.success("保存成功");
  } catch (e) {
    console.error("保存失败", e);
    Toast.error("保存失败");
  } finally {
    saving.value = false;
  }
};

const addBgImage = () => {
  formState.bgImages.push("");
};

const removeBgImage = (index: number) => {
  Dialog.warning({
    title: "确认删除",
    description: "确定要删除此背景图片吗？",
    confirmText: "删除",
    cancelText: "取消",
    onConfirm: () => {
      formState.bgImages.splice(index, 1);
      Toast.success("删除成功");
    },
  });
};

const updatePreviewLogo = () => {
  previewLogo.value = formState.logo;
};

const viewFrontend = () => {
  window.open("/", "_blank");
};
</script>

<template>
  <div class="settings-view">
    <VPageHeader title="网址导航设置">
      <template #icon>
        <i class="ri-settings-3-line"></i>
      </template>
      <template #actions>
        <VSpace>
          <VButton
            type="default"
            @click="viewFrontend"
          >
            <template #icon>
              <RiExternalLinkLine />
            </template>
            访问前台
          </VButton>
          <VButton
            type="default"
            @click="router.push('/navigator')"
          >
            <template #icon>
              <RiArrowLeftLine />
            </template>
            返回
          </VButton>
          <VButton
            type="primary"
            :loading="saving"
            @click="handleSubmit"
          >
            <template #icon>
              <RiSaveLine />
            </template>
            保存
          </VButton>
        </VSpace>
      </template>
    </VPageHeader>

    <div class="p-4">
      <VLoading v-if="loading" />

      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div class="lg:col-span-2 space-y-4">
          <VCard title="基本设置" :body-class="['p-5']">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  网站名称 <span class="text-red-500">*</span>
                </label>
                <input 
                  v-model="formState.siteName" 
                  type="text" 
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                  placeholder="请输入网站名称"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Logo URL
                </label>
                <div class="flex">
                  <input 
                    v-model="formState.logo" 
                    type="text" 
                    class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-l-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                    placeholder="请输入Logo URL"
                    @input="updatePreviewLogo"
                  />
                  <button 
                    class="px-3 py-2 bg-gray-100 dark:bg-gray-700 border border-l-0 border-gray-300 dark:border-gray-600 rounded-r-md hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
                    @click="updatePreviewLogo"
                  >
                    <i class="ri-eye-line"></i>
                  </button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  阿里图标库
                </label>
                <input 
                  v-model="formState.Iconfont" 
                  type="text" 
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                  placeholder="请输入阿里图标库链接"
                />
                <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                  例如：//at.alicdn.com/t/font_1234567_abcdefg.css
                </p>
              </div>
            </div>
          </VCard>

          <VCard title="外观设置" :body-class="['p-5']">
            <div class="space-y-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">
                  默认主题
                </label>
                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  <label class="relative flex flex-col items-center bg-white dark:bg-gray-800 p-4 rounded-lg border border-gray-200 dark:border-gray-700 cursor-pointer hover:border-primary-500 dark:hover:border-primary-500 transition-colors">
                    <input 
                      type="radio" 
                      v-model="formState.theme" 
                      value="light" 
                      class="sr-only"
                    />
                    <div class="w-full h-24 bg-gray-50 dark:bg-gray-900 rounded-md mb-3 flex items-center justify-center">
                      <i class="ri-sun-fill text-3xl text-yellow-500"></i>
                    </div>
                    <span class="text-sm font-medium">浅色模式</span>
                    <div v-if="formState.theme === 'light'" class="absolute -top-2 -right-2 w-6 h-6 bg-primary-500 rounded-full flex items-center justify-center text-white">
                      <i class="ri-check-line"></i>
                    </div>
                  </label>
                  
                  <label class="relative flex flex-col items-center bg-white dark:bg-gray-800 p-4 rounded-lg border border-gray-200 dark:border-gray-700 cursor-pointer hover:border-primary-500 dark:hover:border-primary-500 transition-colors">
                    <input 
                      type="radio" 
                      v-model="formState.theme" 
                      value="dark" 
                      class="sr-only"
                    />
                    <div class="w-full h-24 bg-gray-800 rounded-md mb-3 flex items-center justify-center">
                      <i class="ri-moon-fill text-3xl text-blue-400"></i>
                    </div>
                    <span class="text-sm font-medium">深色模式</span>
                    <div v-if="formState.theme === 'dark'" class="absolute -top-2 -right-2 w-6 h-6 bg-primary-500 rounded-full flex items-center justify-center text-white">
                      <i class="ri-check-line"></i>
                    </div>
                  </label>
                  
                  <label class="relative flex flex-col items-center bg-white dark:bg-gray-800 p-4 rounded-lg border border-gray-200 dark:border-gray-700 cursor-pointer hover:border-primary-500 dark:hover:border-primary-500 transition-colors">
                    <input 
                      type="radio" 
                      v-model="formState.theme" 
                      value="auto" 
                      class="sr-only"
                    />
                    <div class="w-full h-24 bg-gradient-to-r from-gray-50 to-gray-800 rounded-md mb-3 flex items-center justify-center">
                      <div class="flex">
                        <i class="ri-sun-fill text-3xl text-yellow-500 mr-2"></i>
                        <i class="ri-moon-fill text-3xl text-blue-400"></i>
                      </div>
                    </div>
                    <span class="text-sm font-medium">跟随系统</span>
                    <div v-if="formState.theme === 'auto'" class="absolute -top-2 -right-2 w-6 h-6 bg-primary-500 rounded-full flex items-center justify-center text-white">
                      <i class="ri-check-line"></i>
                    </div>
                  </label>
                </div>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  背景透明度
                </label>
                <div class="flex items-center gap-4">
                  <input 
                    v-model="formState.transparency" 
                    type="range" 
                    min="0" 
                    max="1" 
                    step="0.05" 
                    class="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer dark:bg-gray-700"
                  />
                  <span class="text-sm font-medium w-12 text-center">{{ Math.round(formState.transparency * 100) }}%</span>
                </div>
                <div class="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-1">
                  <span>透明</span>
                  <span>不透明</span>
                </div>
              </div>
              
              <div>
                <div class="flex items-center justify-between mb-1">
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                    背景图片
                  </label>
                  <VButton size="sm" type="secondary" @click="addBgImage">
                    <template #icon>
                      <RiAddLine />
                    </template>
                    添加图片
                  </VButton>
                </div>
                <div v-if="formState.bgImages.length === 0" class="text-center py-8 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-dashed border-gray-300 dark:border-gray-700">
                  <div class="text-gray-500 dark:text-gray-400">
                    <i class="ri-image-add-line text-4xl mb-2"></i>
                    <p>暂无背景图片</p>
                    <p class="text-xs mt-1">添加图片URL以设置网站背景</p>
                  </div>
                </div>
                <div v-else class="space-y-3">
                  <div v-for="(image, index) in formState.bgImages" :key="index" class="flex items-center gap-2 p-2 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700">
                    <div class="w-12 h-12 bg-gray-200 dark:bg-gray-700 rounded overflow-hidden flex-shrink-0">
                      <img 
                        :src="image" 
                        class="w-full h-full object-cover"
                        alt="背景图片预览"
                        @error="$event.target.src = 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'%23cccccc\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3E%3Crect x=\'3\' y=\'3\' width=\'18\' height=\'18\' rx=\'2\' ry=\'2\'%3E%3C/rect%3E%3Ccircle cx=\'8.5\' cy=\'8.5\' r=\'1.5\'%3E%3C/circle%3E%3Cpolyline points=\'21 15 16 10 5 21\'%3E%3C/polyline%3E%3C/svg%3E'"
                      />
                    </div>
                    <input 
                      v-model="formState.bgImages[index]" 
                      type="text" 
                      class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                      placeholder="请输入图片URL"
                    />
                    <VButton size="sm" type="danger" @click="removeBgImage(index)">
                      <template #icon>
                        <RiDeleteBin5Line />
                      </template>
                    </VButton>
                  </div>
                </div>
              </div>
              
              <div>
                <div class="flex items-center">
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mr-2">
                    启用智能模式
                  </label>
                  <div class="relative inline-block w-10 mr-2 align-middle select-none">
                    <input 
                      type="checkbox" 
                      v-model="formState.enableSmart" 
                      class="toggle-checkbox absolute block w-6 h-6 rounded-full bg-white border-4 appearance-none cursor-pointer"
                    />
                    <label 
                      class="toggle-label block overflow-hidden h-6 rounded-full bg-gray-300 dark:bg-gray-700 cursor-pointer"
                    ></label>
                  </div>
                </div>
                <p class="mt-1 text-xs text-gray-500 dark:text-gray-400 ml-0.5">
                  智能模式会根据时间自动切换主题和问候语
                </p>
              </div>
            </div>
          </VCard>

          <VCard title="自定义内容" :body-class="['p-5']">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                自定义HTML内容
              </label>
              <textarea 
                v-model="formState.content" 
                rows="5" 
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white font-mono"
                placeholder="请输入自定义HTML内容，将显示在页面底部"
              ></textarea>
              <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                支持HTML代码，将显示在页面底部
              </p>
            </div>
          </VCard>
        </div>
        
        <div class="space-y-4">
          <VCard title="预览" :body-class="['p-5']">
            <div class="flex flex-col items-center">
              <div class="mb-4 text-center">
                <p class="text-sm text-gray-500 dark:text-gray-400 mb-2">网站Logo预览</p>
                <div v-if="previewLogo" class="w-24 h-24 mx-auto bg-gray-100 dark:bg-gray-800 rounded-lg p-2 flex items-center justify-center">
                  <img 
                    :src="previewLogo" 
                    class="max-w-full max-h-full object-contain"
                    alt="Logo预览"
                    @error="$event.target.src = 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'%23cccccc\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3E%3Crect x=\'3\' y=\'3\' width=\'18\' height=\'18\' rx=\'2\' ry=\'2\'%3E%3C/rect%3E%3Ccircle cx=\'8.5\' cy=\'8.5\' r=\'1.5\'%3E%3C/circle%3E%3Cpolyline points=\'21 15 16 10 5 21\'%3E%3C/polyline%3E%3C/svg%3E'"
                  />
                </div>
                <div v-else class="w-24 h-24 mx-auto bg-gray-100 dark:bg-gray-800 rounded-lg flex items-center justify-center">
                  <i class="ri-navigation-fill text-4xl text-primary-500"></i>
                </div>
                <h3 class="text-lg font-medium mt-2">{{ formState.siteName }}</h3>
              </div>
              
              <div class="w-full mt-4">
                <div class="text-sm text-gray-500 dark:text-gray-400 mb-2 text-center">主题预览</div>
                <div class="grid grid-cols-1 gap-4">
                  <div class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4">
                    <div class="flex items-center justify-between mb-2">
                      <div class="flex items-center">
                        <div class="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-500">
                          <i class="ri-navigation-fill"></i>
                        </div>
                        <span class="ml-2 font-medium">{{ formState.siteName }}</span>
                      </div>
                      <div class="flex items-center space-x-2">
                        <div class="w-4 h-4 rounded-full bg-red-500"></div>
                        <div class="w-4 h-4 rounded-full bg-yellow-500"></div>
                        <div class="w-4 h-4 rounded-full bg-green-500"></div>
                      </div>
                    </div>
                    <div class="h-32 bg-gray-100 dark:bg-gray-700 rounded-md flex items-center justify-center">
                      <div class="text-center">
                        <i class="ri-landscape-line text-3xl text-gray-400 dark:text-gray-500 mb-1"></i>
                        <p class="text-sm text-gray-500 dark:text-gray-400">背景图片区域</p>
                      </div>
                    </div>
                    <div class="mt-2 grid grid-cols-3 gap-2">
                      <div v-for="i in 3" :key="i" class="bg-gray-50 dark:bg-gray-700 p-2 rounded">
                        <div class="w-full h-8 bg-gray-200 dark:bg-gray-600 rounded mb-1"></div>
                        <div class="w-3/4 h-3 bg-gray-200 dark:bg-gray-600 rounded"></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </VCard>
          
          <VCard title="帮助" :body-class="['p-5']">
            <div class="space-y-4">
              <div>
                <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">背景图片</h3>
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  您可以添加多张背景图片，系统会随机选择一张作为网站背景。建议使用高清图片，尺寸不小于1920x1080。
                </p>
              </div>
              <div>
                <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">智能模式</h3>
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  启用智能模式后，系统会根据用户的访问时间自动切换主题和问候语。例如，晚上会自动切换为深色模式，并显示"晚上好"的问候语。
                </p>
              </div>
              <div>
                <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">自定义内容</h3>
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  您可以在自定义内容中添加HTML代码，这些代码将显示在页面底部。您可以添加统计代码、友情链接等内容。
                </p>
              </div>
            </div>
          </VCard>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.toggle-checkbox:checked {
  right: 0;
  border-color: #3b82f6;
}
.toggle-checkbox:checked + .toggle-label {
  background-color: #3b82f6;
}
.toggle-label {
  transition: background-color 0.2s ease;
}
</style> 