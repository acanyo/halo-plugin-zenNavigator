<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import {
  VPageHeader,
  VCard,
  VButton,
  VSpace,
  VEmpty,
  VLoading,
  VPagination,
  Dialog,
  Toast,
  VAvatar,
  VStatusDot,
} from "@halo-dev/components";
import { useRouter } from "vue-router";
import { apiClient } from "@/utils/api-client";
import RiAddLine from "~icons/ri/add-line";
import RiEdit2Line from "~icons/ri/edit-2-line";
import RiDeleteBin5Line from "~icons/ri/delete-bin-5-line";
import RiSettings3Line from "~icons/ri/settings-3-line";
import RiExternalLinkLine from "~icons/ri/external-link-line";
import RiRefreshLine from "~icons/ri/refresh-line";

const router = useRouter();
const categories = ref<any[]>([]);
const loading = ref(true);
const refreshing = ref(false);
const total = ref(0);
const page = ref(1);
const size = ref(10);
const searchKeyword = ref("");

const filteredCategories = computed(() => {
  if (!searchKeyword.value) return categories.value;
  
  return categories.value.filter(category => 
    category.spec.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    (category.spec.icon && category.spec.icon.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  );
});

onMounted(async () => {
  await fetchCategories();
});

const fetchCategories = async () => {
  loading.value = true;
  try {
    const { data } = await apiClient.get("/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories", {
      params: {
        page: page.value - 1,
        size: size.value,
      },
    });
    categories.value = data.items || [];
    total.value = data.total || 0;
  } catch (e) {
    console.error("获取分类失败", e);
    Toast.error("获取分类失败");
  } finally {
    loading.value = false;
  }
};

const refreshCategories = async () => {
  refreshing.value = true;
  try {
    await fetchCategories();
    Toast.success("刷新成功");
  } catch (e) {
    console.error("刷新失败", e);
  } finally {
    refreshing.value = false;
  }
};

const handlePageChange = async (pagination: { page: number; size: number }) => {
  page.value = pagination.page;
  size.value = pagination.size;
  await fetchCategories();
};

const handleCreateCategory = () => {
  router.push("/navigator/categories/create");
};

const handleEditCategory = (category: any) => {
  router.push(`/navigator/categories/edit/${category.metadata.name}`);
};

const handleDeleteCategory = async (category: any) => {
  Dialog.warning({
    title: "确认删除",
    description: `确定要删除分类 "${category.spec.name}" 吗？此操作不可恢复，删除后该分类下的所有子分类和链接也将被删除。`,
    confirmText: "删除",
    cancelText: "取消",
    onConfirm: async () => {
      try {
        await apiClient.delete(`/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories/${category.metadata.name}`);
        Toast.success("删除成功");
        await fetchCategories();
      } catch (e) {
        console.error("删除失败", e);
        Toast.error("删除失败");
      }
    },
  });
};

const goToSettings = () => {
  router.push("/navigator/settings");
};

const viewFrontend = () => {
  window.open("/", "_blank");
};

const getIconClass = (icon: string) => {
  return icon || "ri-folder-line";
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
  <div class="navigator-view">
    <VPageHeader title="网址导航管理">
      <template #icon>
        <i class="ri-navigation-fill"></i>
      </template>
      <template #actions>
        <VSpace>
          <VButton type="default" @click="viewFrontend">
            <template #icon>
              <RiExternalLinkLine />
            </template>
            访问前台
          </VButton>
          <VButton type="default" @click="goToSettings">
            <template #icon>
              <RiSettings3Line />
            </template>
            设置
          </VButton>
          <VButton type="secondary" @click="handleCreateCategory">
            <template #icon>
              <RiAddLine />
            </template>
            新增分类
          </VButton>
        </VSpace>
      </template>
    </VPageHeader>

    <div class="p-4">
      <VCard :body-class="['p-0']">
        <template #header>
          <div class="flex items-center justify-between p-4">
            <div class="flex items-center">
              <span class="font-medium mr-2">分类列表</span>
              <VStatusDot v-if="!loading" :status="'success'" :text="`共 ${total} 个分类`" />
            </div>
            <div class="flex items-center space-x-2">
              <div class="relative">
                <input
                  v-model="searchKeyword"
                  type="text"
                  placeholder="搜索分类..."
                  class="pl-8 pr-4 py-1.5 rounded-lg border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-800 dark:text-white"
                />
                <i class="ri-search-line absolute left-2.5 top-2 text-gray-400"></i>
              </div>
              <VButton
                type="default"
                size="sm"
                :loading="refreshing"
                @click="refreshCategories"
              >
                <template #icon>
                  <RiRefreshLine />
                </template>
                刷新
              </VButton>
            </div>
          </div>
        </template>

        <VLoading v-if="loading" />

        <VEmpty
          v-else-if="categories.length === 0"
          title="暂无分类"
          message="点击右上角按钮新增分类"
        >
          <template #actions>
            <VButton type="secondary" @click="handleCreateCategory">
              <template #icon>
                <RiAddLine />
              </template>
              新增分类
            </VButton>
          </template>
        </VEmpty>

        <div v-else-if="filteredCategories.length === 0" class="py-16 text-center">
          <div class="text-gray-500 dark:text-gray-400">
            <i class="ri-search-line text-4xl mb-2"></i>
            <p>没有找到匹配的分类</p>
          </div>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
          <VCard
            v-for="category in filteredCategories"
            :key="category.metadata.name"
            :body-class="['p-0']"
            class="hover:shadow-md transition-shadow duration-200"
          >
            <div class="p-4">
              <div class="flex items-center mb-4">
                <div v-if="category.spec.icon && category.spec.icon.startsWith('ri-')" class="mr-3">
                  <VAvatar
                    :alt="category.spec.name"
                    size="lg"
                    :class="getRandomColor(category.spec.name)"
                  >
                    <i :class="getIconClass(category.spec.icon)" class="text-xl"></i>
                  </VAvatar>
                </div>
                <div v-else class="mr-3">
                  <VAvatar
                    :alt="category.spec.name"
                    size="lg"
                    :class="getRandomColor(category.spec.name)"
                  >
                    {{ category.spec.name.charAt(0).toUpperCase() }}
                  </VAvatar>
                </div>
                <div class="flex-1 min-w-0">
                  <h3 class="text-lg font-medium text-gray-900 dark:text-gray-100 truncate">
                    {{ category.spec.name }}
                  </h3>
                  <div class="flex items-center text-sm text-gray-500 dark:text-gray-400">
                    <span class="truncate">{{ category.metadata.name }}</span>
                  </div>
                </div>
              </div>
              
              <div class="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400 mb-4">
                <div class="flex items-center">
                  <span class="mr-4">
                    <i class="ri-folder-line mr-1"></i>
                    {{ category.spec.subcategories?.length || 0 }} 个子分类
                  </span>
                  <span>
                    <i class="ri-link mr-1"></i>
                    {{ category.status?.linkCount || 0 }} 个链接
                  </span>
                </div>
                <div>
                  <span v-if="category.spec.inTopMenu" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200">
                    <i class="ri-menu-line mr-1"></i> 顶部菜单
                  </span>
                </div>
              </div>
              
              <div class="flex justify-end border-t border-gray-100 dark:border-gray-800 pt-3">
                <VSpace>
                  <VButton
                    size="sm"
                    type="secondary"
                    @click="handleEditCategory(category)"
                  >
                    <template #icon>
                      <RiEdit2Line />
                    </template>
                    编辑
                  </VButton>
                  <VButton
                    size="sm"
                    type="danger"
                    @click="handleDeleteCategory(category)"
                  >
                    <template #icon>
                      <RiDeleteBin5Line />
                    </template>
                    删除
                  </VButton>
                </VSpace>
              </div>
            </div>
          </VCard>
        </div>

        <div v-if="!loading && categories.length > 0" class="flex justify-end p-4 border-t border-gray-100 dark:border-gray-800">
          <VPagination
            v-model:page="page"
            v-model:size="size"
            :total="total"
            :size-options="[10, 20, 50]"
            @change="handlePageChange"
          />
        </div>
      </VCard>
    </div>
  </div>
</template>

<style scoped>
.navigator-view {
  min-height: calc(100vh - 60px);
}
</style> 
