<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { VPageHeader, VCard, VButton, VSpace, VLoading, Dialog, Toast, VAvatar } from "@halo-dev/components";
import RiSaveLine from "~icons/ri/save-line";
import RiArrowLeftLine from "~icons/ri/arrow-left-line";
import RiAddLine from "~icons/ri/add-line";
import RiDeleteBin5Line from "~icons/ri/delete-bin-5-line";
import RiEditLine from "~icons/ri/edit-line";
import RiExternalLinkLine from "~icons/ri/external-link-line";
import RiSearchLine from "~icons/ri/search-line";
import RiRefreshLine from "~icons/ri/refresh-line";
import { apiClient } from "@/utils/api-client";

// 定义链接接口
interface Site {
  id: string;
  name: string;
  url: string;
  icon: string;
  description: string;
}

// 定义编辑站点的接口类型
interface EditingSite {
  index: number;
  isNew: boolean;
}

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const saving = ref(false);
const categoryName = computed(() => route.params.categoryName as string);
const subcategoryIndex = computed(() => parseInt(route.params.subcategoryIndex as string));
const subcategoryName = computed(() => route.query.subcategoryName as string || "");
const searchKeyword = ref("");
const category = ref<any>(null);
const sites = ref<Site[]>([]);
const editingSite = ref<EditingSite | null>(null);
const showSiteModal = ref(false);

const emptySite: Site = {
  id: "",
  name: "",
  url: "",
  icon: "",
  description: "",
};

const currentSite = reactive({...emptySite});

// 过滤后的链接列表
const filteredSites = computed(() => {
  if (!searchKeyword.value) return sites.value;
  
  const keyword = searchKeyword.value.toLowerCase();
  return sites.value.filter(site => 
    site.name.toLowerCase().includes(keyword) || 
    site.url.toLowerCase().includes(keyword) || 
    site.description.toLowerCase().includes(keyword)
  );
});

onMounted(async () => {
  // 确保categoryName参数有效
  if (!categoryName.value) {
    Toast.error("分类参数无效");
    router.push("/navigator");
    return;
  }
  
  await fetchCategory();
});

const fetchCategory = async () => {
  loading.value = true;
  try {
    const { data } = await apiClient.get(`/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories/${categoryName.value}`);
    category.value = data;
    
    // 获取子分类下的链接
    const subcategory = data.spec.subcategories[subcategoryIndex.value];
    if (subcategory) {
      sites.value = subcategory.sites || [];
    } else {
      Toast.error("未找到指定的子分类");
      router.push("/navigator");
    }
  } catch (e) {
    console.error("获取分类失败", e);
    Toast.error("获取分类失败");
    router.push("/navigator");
  } finally {
    loading.value = false;
  }
};

const addSite = () => {
  resetCurrentSite();
  editingSite.value = { index: -1, isNew: true };
  showSiteModal.value = true;
};

const editSite = (index: number) => {
  const site = sites.value[index];
  Object.assign(currentSite, site);
  editingSite.value = { index, isNew: false };
  showSiteModal.value = true;
};

const resetCurrentSite = () => {
  Object.assign(currentSite, emptySite);
  currentSite.id = `site-${Date.now()}`;
};

const saveSite = async () => {
  if (!currentSite.name) {
    Toast.error("链接名称不能为空");
    return;
  }
  if (!currentSite.url) {
    Toast.error("链接URL不能为空");
    return;
  }

  if (!editingSite.value) return;
  
  saving.value = true;
  
  try {
    const { index, isNew } = editingSite.value;
    
    // 更新本地数据
    if (isNew) {
      sites.value.push({...currentSite});
    } else {
      Object.assign(sites.value[index], currentSite);
    }
    
    // 更新子分类中的链接
    if (category.value) {
      category.value.spec.subcategories[subcategoryIndex.value].sites = sites.value;
      
      // 保存到服务器
      await apiClient.put(`/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories/${categoryName.value}`, {
        spec: category.value.spec
      });
      
      Toast.success(isNew ? "添加成功" : "更新成功");
      showSiteModal.value = false;
    }
  } catch (e) {
    console.error("保存失败", e);
    Toast.error("保存失败");
  } finally {
    saving.value = false;
  }
};

const removeSite = (index: number) => {
  Dialog.warning({
    title: "确认删除",
    description: "确定要删除此链接吗？此操作不可恢复。",
    confirmText: "删除",
    cancelText: "取消",
    onConfirm: async () => {
      try {
        // 从本地数据中删除
        sites.value.splice(index, 1);
        
        // 更新子分类中的链接
        if (category.value) {
          category.value.spec.subcategories[subcategoryIndex.value].sites = sites.value;
          
          // 保存到服务器
          await apiClient.put(`/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories/${categoryName.value}`, {
            spec: category.value.spec
          });
          
          Toast.success("删除成功");
        }
      } catch (e) {
        console.error("删除失败", e);
        Toast.error("删除失败");
      }
    },
  });
};

const refreshData = async () => {
  await fetchCategory();
  Toast.success("刷新成功");
};

const getDefaultIcon = (url: string) => {
  try {
    const hostname = new URL(url).hostname;
    return `https://www.google.com/s2/favicons?domain=${hostname}&sz=64`;
  } catch (e) {
    return '';
  }
};

const getRandomColor = (name: string) => {
  const colors = [
    "bg-blue-100 text-blue-600",
    "bg-green-100 text-green-600",
    "bg-purple-100 text-purple-600",
    "bg-pink-100 text-pink-600",
    "bg-yellow-100 text-yellow-600",
    "bg-indigo-100 text-indigo-600",
    "bg-red-100 text-red-600",
    "bg-orange-100 text-orange-600",
  ];
  
  // 使用名称的字符码总和来确定颜色索引
  const sum = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
  return colors[sum % colors.length];
};
</script>

<template>
  <div class="link-manager-view">
    <VPageHeader :title="`${subcategoryName} - 链接管理`">
      <template #icon>
        <i class="ri-link"></i>
      </template>
      <template #actions>
        <VSpace>
          <VButton
            type="default"
            @click="router.push(`/navigator/categories/edit/${categoryName}`)"
          >
            <template #icon>
              <RiArrowLeftLine />
            </template>
            返回
          </VButton>
          <VButton
            type="primary"
            @click="addSite"
          >
            <template #icon>
              <RiAddLine />
            </template>
            添加链接
          </VButton>
        </VSpace>
      </template>
    </VPageHeader>

    <div class="p-4">
      <VLoading v-if="loading" />

      <div v-else>
        <VCard :body-class="['p-0']">
          <template #header>
            <div class="flex items-center justify-between p-4">
              <div class="flex items-center">
                <span class="font-medium mr-2">链接列表</span>
                <span class="text-sm text-gray-500 dark:text-gray-400">
                  共 {{ sites.length }} 个链接
                </span>
              </div>
              <div class="flex items-center space-x-2">
                <div class="relative">
                  <input
                    v-model="searchKeyword"
                    type="text"
                    placeholder="搜索链接..."
                    class="pl-8 pr-4 py-1.5 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-800 dark:text-white"
                  />
                  <RiSearchLine class="absolute left-2.5 top-2 text-gray-400 w-4 h-4" />
                </div>
                <VButton
                  type="default"
                  size="sm"
                  @click="refreshData"
                >
                  <template #icon>
                    <RiRefreshLine />
                  </template>
                  刷新
                </VButton>
              </div>
            </div>
          </template>

          <div v-if="sites.length === 0" class="text-center py-16">
            <div class="text-gray-500 dark:text-gray-400 mb-4">
              <i class="ri-link-m text-4xl mb-2"></i>
              <p>暂无链接</p>
            </div>
            <VButton type="secondary" @click="addSite">
              <template #icon>
                <RiAddLine />
              </template>
              添加链接
            </VButton>
          </div>

          <div v-else-if="filteredSites.length === 0" class="text-center py-16">
            <div class="text-gray-500 dark:text-gray-400">
              <i class="ri-search-line text-4xl mb-2"></i>
              <p>没有找到匹配的链接</p>
            </div>
          </div>

          <div v-else class="p-4">
            <!-- 网格布局的链接列表 -->
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              <div 
                v-for="(site, index) in filteredSites" 
                :key="site.id" 
                class="relative group flex flex-col items-center p-4 rounded-lg bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 hover:border-primary-300 dark:hover:border-primary-600 hover:shadow-md transition-all duration-200 hover:-translate-y-1"
              >
                <!-- 操作按钮 -->
                <div class="absolute top-2 right-2 flex space-x-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button 
                    class="p-1 rounded-full bg-primary-100 text-primary-600 hover:bg-primary-200 dark:bg-primary-900/30 dark:text-primary-400 dark:hover:bg-primary-800/40"
                    @click="editSite(index)"
                    title="编辑"
                  >
                    <RiEditLine class="w-4 h-4" />
                  </button>
                  <button 
                    class="p-1 rounded-full bg-red-100 text-red-600 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-800/40"
                    @click="removeSite(index)"
                    title="删除"
                  >
                    <RiDeleteBin5Line class="w-4 h-4" />
                  </button>
                </div>
                
                <!-- 链接图标 -->
                <div class="mb-3 rounded-lg overflow-hidden shadow-sm w-16 h-16 flex items-center justify-center bg-gray-100 dark:bg-gray-700">
                  <img 
                    :src="site.icon || getDefaultIcon(site.url)" 
                    class="w-16 h-16 object-cover" 
                    :alt="site.name + ' 图标'" 
                    loading="lazy" 
                    onerror="this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 24 24\' fill=\'none\' stroke=\'%23cccccc\' stroke-width=\'2\' stroke-linecap=\'round\' stroke-linejoin=\'round\'%3E%3Crect x=\'3\' y=\'3\' width=\'18\' height=\'18\' rx=\'2\' ry=\'2\'%3E%3C/rect%3E%3Ccircle cx=\'8.5\' cy=\'8.5\' r=\'1.5\'%3E%3C/circle%3E%3Cpolyline points=\'21 15 16 10 5 21\'%3E%3C/polyline%3E%3C/svg%3E';"
                  >
                </div>
                
                <!-- 链接信息 -->
                <div class="text-center w-full">
                  <div class="font-medium text-gray-800 dark:text-gray-200 text-base truncate" :title="site.name">
                    {{ site.name }}
                  </div>
                  <div class="text-sm text-gray-500 dark:text-gray-400 mt-1 truncate" :title="site.url">
                    {{ site.url }}
                  </div>
                  <div v-if="site.description" class="text-xs text-gray-500 dark:text-gray-400 mt-1 line-clamp-2" :title="site.description">
                    {{ site.description }}
                  </div>
                </div>
                
                <!-- 预览链接按钮 -->
                <a 
                  :href="site.url" 
                  target="_blank" 
                  rel="noopener noreferrer" 
                  class="mt-3 text-xs text-primary-500 hover:text-primary-600 dark:text-primary-400 dark:hover:text-primary-300 flex items-center"
                >
                  <RiExternalLinkLine class="w-3 h-3 mr-1" />
                  访问链接
                </a>
              </div>
            </div>
          </div>
        </VCard>
      </div>
    </div>
    
    <!-- 链接编辑弹窗 -->
    <Dialog
      v-model:visible="showSiteModal"
      :title="editingSite?.isNew ? '添加链接' : '编辑链接'"
      :width="500"
      @close="showSiteModal = false"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            名称 <span class="text-red-500">*</span>
          </label>
          <input 
            v-model="currentSite.name" 
            type="text" 
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
            placeholder="请输入链接名称"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            URL <span class="text-red-500">*</span>
          </label>
          <input 
            v-model="currentSite.url" 
            type="text" 
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
            placeholder="请输入链接地址"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            图标
          </label>
          <input 
            v-model="currentSite.icon" 
            type="text" 
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
            placeholder="请输入图标URL"
          />
          <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
            留空将自动获取网站图标
          </p>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            描述
          </label>
          <input 
            v-model="currentSite.description" 
            type="text" 
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
            placeholder="请输入链接描述"
          />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end space-x-2">
          <VButton type="default" @click="showSiteModal = false">取消</VButton>
          <VButton type="primary" :loading="saving" @click="saveSite">保存</VButton>
        </div>
      </template>
    </Dialog>
  </div>
</template>

<style scoped>
.link-manager-view {
  min-height: calc(100vh - 60px);
}
</style> 