<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { VPageHeader, VCard, VButton, VSpace, VLoading, Dialog, Toast, VAvatar } from "@halo-dev/components";
import RiSaveLine from "~icons/ri/save-line";
import RiArrowLeftLine from "~icons/ri/arrow-left-line";
import RiAddLine from "~icons/ri/add-line";
import RiDeleteBin5Line from "~icons/ri/delete-bin-5-line";
import RiDraggable from "~icons/ri/draggable";
import RiEyeLine from "~icons/ri/eye-line";
import RiLinkM from "~icons/ri/link-m";
import { apiClient } from "@/utils/api-client";

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const saving = ref(false);
const isEdit = computed(() => !!route.params.name);
const previewIcon = ref("");

const formState = reactive({
  name: "",
  icon: "",
  inTopMenu: true,
  subcategories: [] as any[],
});

onMounted(async () => {
  if (isEdit.value) {
    await fetchCategory();
    
    // 检查URL参数，如果有goToLinks参数，则自动跳转到链接管理页面
    const goToLinks = route.query.goToLinks === "true";
    const subcategoryIndexParam = route.query.subcategoryIndex;
    
    if (goToLinks && subcategoryIndexParam) {
      const subcategoryIndex = parseInt(subcategoryIndexParam as string);
      const subcategory = formState.subcategories[subcategoryIndex];
      
      if (subcategory) {
        // 等待一小段时间，确保页面已经加载完成
        setTimeout(() => {
          goToLinkManager(subcategoryIndex);
        }, 500);
      }
    }
  }
});

const fetchCategory = async () => {
  loading.value = true;
  try {
    const { data } = await apiClient.get(`/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories/${route.params.name}`);
    formState.name = data.spec.name;
    formState.icon = data.spec.icon;
    formState.inTopMenu = data.spec.inTopMenu;
    formState.subcategories = data.spec.subcategories || [];
    updatePreviewIcon();
  } catch (e) {
    console.error("获取分类失败", e);
    Toast.error("获取分类失败");
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formState.name) {
    Toast.error("分类名称不能为空");
    return;
  }
  
  // 验证子分类
  for (const subcategory of formState.subcategories) {
    if (!subcategory.name) {
      Toast.error("子分类名称不能为空");
      return;
    }
  }
  
  saving.value = true;
  try {
    const payload = {
      spec: {
        name: formState.name,
        icon: formState.icon,
        inTopMenu: formState.inTopMenu,
        subcategories: formState.subcategories,
      },
    };

    if (isEdit.value) {
      await apiClient.put(`/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories/${route.params.name}`, payload);
      Toast.success("更新成功");
    } else {
      const metadata = {
        name: formState.name.toLowerCase().replace(/\s+/g, "-"),
        generateName: "category-",
      };
      await apiClient.post("/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories", {
        ...payload,
        metadata,
      });
      Toast.success("创建成功");
    }
    router.push("/navigator");
  } catch (e) {
    console.error("保存失败", e);
    Toast.error("保存失败");
  } finally {
    saving.value = false;
  }
};

const addSubcategory = () => {
  formState.subcategories.push({
    id: `subcategory-${Date.now()}`,
    name: "",
    icon: "ri-folder-line",
    sites: [],
  });
};

const removeSubcategory = (index: number) => {
  Dialog.warning({
    title: "确认删除",
    description: "确定要删除此子分类吗？此操作不可恢复，删除后该子分类下的所有链接也将被删除。",
    confirmText: "删除",
    cancelText: "取消",
    onConfirm: () => {
      formState.subcategories.splice(index, 1);
      Toast.success("删除成功");
    },
  });
};

const goToLinkManager = (subcategoryIndex: number) => {
  // 如果是新建分类，需要先保存
  if (!isEdit.value) {
    Dialog.warning({
      title: "需要先保存分类",
      description: "您需要先保存分类信息，然后才能管理链接。是否立即保存分类？",
      confirmText: "保存分类",
      cancelText: "取消",
      onConfirm: async () => {
        // 验证分类名称
        if (!formState.name) {
          Toast.error("分类名称不能为空");
          return;
        }
        
        // 验证子分类
        for (const subcategory of formState.subcategories) {
          if (!subcategory.name) {
            Toast.error("子分类名称不能为空");
            return;
          }
        }
        
        saving.value = true;
        try {
          const payload = {
            spec: {
              name: formState.name,
              icon: formState.icon,
              inTopMenu: formState.inTopMenu,
              subcategories: formState.subcategories,
            },
          };

          const metadata = {
            name: formState.name.toLowerCase().replace(/\s+/g, "-"),
            generateName: "category-",
          };
          
          const { data } = await apiClient.post("/apis/api.plugin.halo.run/v1alpha1/plugins/zenNavigator/categories", {
            ...payload,
            metadata,
          });
          
          Toast.success("保存成功");
          
          // 保存成功后跳转到编辑页面，然后再跳转到链接管理页面
          router.push({
            name: "EditCategory",
            params: {
              name: data.metadata.name,
            },
            query: {
              goToLinks: "true",
              subcategoryIndex: subcategoryIndex.toString(),
            }
          });
        } catch (e) {
          console.error("保存失败", e);
          Toast.error("保存失败");
        } finally {
          saving.value = false;
        }
      }
    });
    return;
  }
  
  const subcategory = formState.subcategories[subcategoryIndex];
  const categoryName = route.params.name as string;
  
  // 跳转到链接管理页面，传递分类名称和子分类索引
  router.push({
    name: "LinkManager",
    params: {
      categoryName,
      subcategoryIndex: subcategoryIndex.toString(),
    },
    query: {
      subcategoryName: subcategory.name,
    },
  });
};

const updatePreviewIcon = () => {
  previewIcon.value = formState.icon;
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

const getSiteCount = (subcategory: any) => {
  return subcategory.sites?.length || 0;
};
</script>

<template>
  <div class="category-edit-view">
    <VPageHeader :title="isEdit ? '编辑分类' : '新增分类'">
      <template #icon>
        <i class="ri-folder-line"></i>
      </template>
      <template #actions>
        <VSpace>
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

      <div v-else>
        <VCard title="基本信息" :body-class="['p-5']">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="md:col-span-2">
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  分类名称 <span class="text-red-500">*</span>
                </label>
                <input 
                  v-model="formState.name" 
                  type="text" 
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                  placeholder="请输入分类名称"
                />
              </div>
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  图标 <span class="text-red-500">*</span>
                </label>
                <div class="flex">
                  <input 
                    v-model="formState.icon" 
                    type="text" 
                    class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-l-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                    placeholder="请输入图标类名，如：ri-folder-line"
                    @input="updatePreviewIcon"
                  />
                  <button 
                    class="px-3 py-2 bg-gray-100 dark:bg-gray-700 border border-l-0 border-gray-300 dark:border-gray-600 rounded-r-md hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
                    @click="updatePreviewIcon"
                  >
                    <i class="ri-eye-line"></i>
                  </button>
                </div>
                <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                  支持 <a href="https://remixicon.com/" target="_blank" class="text-primary-500 hover:text-primary-600">Remix Icon</a> 图标，格式为 ri-xxx-xxx
                </p>
              </div>
              <div class="flex items-center">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mr-2">
                  显示在顶部菜单
                </label>
                <div class="relative inline-block w-10 mr-2 align-middle select-none">
                  <input 
                    type="checkbox" 
                    v-model="formState.inTopMenu" 
                    class="toggle-checkbox absolute block w-6 h-6 rounded-full bg-white border-4 appearance-none cursor-pointer"
                  />
                  <label 
                    class="toggle-label block overflow-hidden h-6 rounded-full bg-gray-300 dark:bg-gray-700 cursor-pointer"
                  ></label>
                </div>
                <span class="text-xs text-gray-500 dark:text-gray-400">
                  (显示在网站顶部导航栏)
                </span>
              </div>
            </div>
            <div class="flex flex-col items-center justify-center">
              <div class="text-center mb-2">
                <p class="text-sm text-gray-500 dark:text-gray-400 mb-2">图标预览</p>
                <div class="inline-flex items-center justify-center">
                  <VAvatar
                    :alt="formState.name || '预览'"
                    size="lg"
                    :class="getRandomColor(formState.name || '预览')"
                  >
                    <i v-if="previewIcon" :class="previewIcon" class="text-2xl"></i>
                    <template v-else>{{ (formState.name || '预览').charAt(0).toUpperCase() }}</template>
                  </VAvatar>
                </div>
              </div>
            </div>
          </div>
        </VCard>

        <VCard class="mt-4" title="子分类管理" :body-class="['p-5']">
          <div v-if="formState.subcategories.length === 0" class="text-center py-8">
            <div class="text-gray-500 dark:text-gray-400 mb-4">
              <i class="ri-folder-add-line text-4xl mb-2"></i>
              <p>暂无子分类</p>
            </div>
            <VButton type="secondary" @click="addSubcategory">
              <template #icon>
                <RiAddLine />
              </template>
              添加子分类
            </VButton>
          </div>

          <div v-else>
            <div v-for="(subcategory, index) in formState.subcategories" :key="subcategory.id" class="mb-6 p-4 border border-gray-200 dark:border-gray-700 rounded-lg bg-gray-50 dark:bg-gray-800/30">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center">
                  <i class="ri-draggable text-gray-400 dark:text-gray-500 mr-2"></i>
                  <h3 class="text-lg font-medium">子分类 {{ index + 1 }}</h3>
                </div>
                <VButton size="sm" type="danger" @click="removeSubcategory(index)">
                  <template #icon>
                    <RiDeleteBin5Line />
                  </template>
                  删除
                </VButton>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    名称 <span class="text-red-500">*</span>
                  </label>
                  <input 
                    v-model="subcategory.name" 
                    type="text" 
                    class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                    placeholder="请输入子分类名称"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                    图标 <span class="text-red-500">*</span>
                  </label>
                  <div class="flex">
                    <input 
                      v-model="subcategory.icon" 
                      type="text" 
                      class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-l-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-800 dark:text-white"
                      placeholder="请输入图标类名，如：ri-folder-line"
                    />
                    <div class="px-3 py-2 bg-gray-100 dark:bg-gray-700 border border-l-0 border-gray-300 dark:border-gray-600 rounded-r-md">
                      <i :class="subcategory.icon || 'ri-folder-line'"></i>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 链接管理按钮 -->
              <div class="mt-4 flex items-center justify-between p-4 bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">
                <div class="flex items-center">
                  <i class="ri-link mr-2 text-gray-500"></i>
                  <div>
                    <h4 class="font-medium">链接管理</h4>
                    <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      当前共有 {{ getSiteCount(subcategory) }} 个链接
                    </p>
                  </div>
                </div>
                <VButton type="secondary" @click="goToLinkManager(index)">
                  <template #icon>
                    <RiLinkM />
                  </template>
                  管理链接
                </VButton>
              </div>
            </div>

            <div class="text-center mt-4">
              <VButton type="secondary" @click="addSubcategory">
                <template #icon>
                  <RiAddLine />
                </template>
                添加子分类
              </VButton>
            </div>
          </div>
        </VCard>
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