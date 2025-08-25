<script lang="ts" setup>
import LazyImage from "@/components/LazyImage.vue";
import NavSiteEditingModal from "@/components/NavSiteEditingModal.vue";
import type { NavSite, NavSiteList } from "@/types";
import { axiosInstance } from "@halo-dev/api-client";
import {
    Dialog,
    IconAddCircle,
    IconArrowLeft,
    IconArrowRight,
    IconCheckboxFill,
    Toast,
    VButton,
    VCard,
    VDropdown,
    VDropdownItem,
    VEmpty,
    VLoading,
    VPageHeader,
    VPagination,
    VSpace,
} from "@halo-dev/components";
import type { AttachmentLike } from "@halo-dev/console-shared";
import { useQuery } from "@tanstack/vue-query";
import Fuse from "fuse.js";
import { computed, nextTick, ref, watch } from "vue";
import TablerDeviceGamepad3 from '~icons/streamline-freehand-color/website-development-browser-hand'
import GroupList from "../components/GroupList.vue";
import {VueDraggable} from "vue-draggable-plus";

const removeFileExtension = (filename: string) => {
    return filename.replace(/\.[^/.]+$/, "");
};

const selectedSite = ref<NavSite | undefined>();
const selectedSites = ref<Set<NavSite>>(new Set<NavSite>());
const selectedGroup = ref<string>();
const editingModal = ref(false);
const checkedAll = ref(false);
const groupListRef = ref();

const page = ref(1);
const size = ref(20);
const total = ref(0);
const keyword = ref("");
const sites = ref<NavSite[]>([]);

const {
    isLoading,
    refetch,
} = useQuery<NavSite[]>({
    queryKey: ["plugin:zen-navigator:sites", page, size, keyword, selectedGroup],
    queryFn: async () => {
        if (!selectedGroup.value) {
            return [] as NavSite[];
        }
        const { data } = await axiosInstance.get<NavSiteList>("/apis/zenNavigator.lik.cc/v1alpha1/navsite", {
            params: {
                page: page.value,
                size: size.value,
                keyword: keyword.value,
                group: selectedGroup.value,
            },
        });
        total.value = data.total;
        return data.items
                .map((site: NavSite) => site)
                .sort((a: NavSite, b: NavSite) => {
                    const ap = (a as any).spec?.priority || 0;
                    const bp = (b as any).spec?.priority || 0;
                    return ap - bp;
                });
    },
    refetchInterval(data) {
        const hasDeleting = data?.some((site) => !!site.metadata.deletionTimestamp);
        return hasDeleting ? 1000 : false;
    },
    onSuccess(data) {
        sites.value = data;
    },
    refetchOnWindowFocus: false,
});

const handleSelectPrevious = () => {
    if (!sites.value) {
        return;
    }

    const currentIndex = sites.value.findIndex((site) => site.metadata.name === selectedSite.value?.metadata.name);

    if (currentIndex > 0) {
        selectedSite.value = sites.value[currentIndex - 1];
        return;
    }

    if (currentIndex <= 0) {
        selectedSite.value = undefined;
    }
};

const handleSelectNext = () => {
    if (!sites.value) {
        return;
    }

    if (!selectedSite.value) {
        selectedSite.value = sites.value[0];
        return;
    }
    const currentIndex = sites.value.findIndex((site) => site.metadata.name === selectedSite.value?.metadata.name);
    if (currentIndex !== sites.value.length - 1) {
        selectedSite.value = sites.value[currentIndex + 1];
    }
};

const handleOpenEditingModal = (site?: NavSite) => {
    selectedSite.value = site;
    editingModal.value = true;
};

const handleDeleteInBatch = () => {
    Dialog.warning({
        title: "是否确认删除所选的站点？",
        description: "删除之后将无法恢复。",
        confirmType: "danger",
        onConfirm: async () => {
            try {
                const promises = Array.from(selectedSites.value).map((site) => {
                    return axiosInstance.delete(`/apis/zenNavigator.lik.cc/v1alpha1/navsite/${site.metadata.name}`);
                });
                await Promise.all(promises);
            } catch (e) {
                console.error(e);
            } finally {
                pageRefetch();
            }
        },
    });
};

const handleCheckAllChange = (e: Event) => {
    const { checked } = (e.target as HTMLInputElement);
    handleCheckAll(checked);
};

const handleCheckAll = (checkAll: boolean) => {
    if (checkAll) {
        sites.value?.forEach((site) => {
            selectedSites.value.add(site);
        });
    } else {
        selectedSites.value.clear();
    }
};

const isChecked = (site: NavSite) => {
    return (
            site.metadata.name === selectedSite.value?.metadata.name ||
            Array.from(selectedSites.value)
                    .map((item) => item.metadata.name)
                    .includes(site.metadata.name)
    );
};

watch(
        () => selectedSites.value.size,
        (newValue) => {
            checkedAll.value = newValue === sites.value?.length;
        }
);

// search
let fuse: Fuse<NavSite> | undefined = undefined;

watch(
        () => sites.value,
        () => {
            if (!sites.value) {
                return;
            }

            fuse = new Fuse(sites.value, {
                keys: ["spec.name", "metadata.name", "spec.description", "spec.url"],
                useExtendedSearch: true,
            });
        }
);

const searchResults = computed({
    get() {
        if (!fuse || !keyword.value) {
            return sites.value || [];
        }

        return fuse?.search(keyword.value).map((item) => item.item);
    },
    set(value) {
        sites.value = value;
    },
});

// create by attachments
const attachmentModal = ref(false);

const onAttachmentsSelect = async (attachments: AttachmentLike[]) => {
    const newSites: {
        url: string;
        icon?: string;
        name?: string;
        type?: string;
    }[] = attachments
            .map((attachment) => {
                const post = {
                    groupName: selectedGroup.value || "",
                };

                if (typeof attachment === "string") {
                    return {
                        ...post,
                        url: attachment,
                        icon: attachment,
                    };
                }
                if ("url" in attachment) {
                    return {
                        ...post,
                        url: attachment.url,
                        icon: attachment.url,
                    };
                }
                if ("spec" in attachment) {
                    return {
                        ...post,
                        icon: attachment.status?.permalink,
                        name: attachment.spec.displayName ? removeFileExtension(attachment.spec.displayName) : undefined,
                        type: attachment.spec.mediaType,
                    };
                }
            })
            .filter(Boolean) as {
        url: string;
        icon?: string;
        name?: string;
        type?: string;
    }[];

    for (const site of newSites) {
        const type = site.type;
        if (!type) {
            Toast.error("只支持选择图片");
            nextTick(() => {
                attachmentModal.value = true;
            });

            return;
        }
        const fileType = type.split("/")[0];
        if (fileType !== "image") {
            Toast.error("只支持选择图片");
            nextTick(() => {
                attachmentModal.value = true;
            });
            return;
        }
    }

    const createRequests = newSites.map((site) => {
        return axiosInstance.post<NavSite>("/apis/zenNavigator.lik.cc/v1alpha1/navsite", {
            metadata: {
                name: "",
                generateName: "navsite-",
            },
            spec: site,
            kind: "NavSite",
            apiVersion: "zenNavigator.lik.cc/v1alpha1",
        });
    });

    await Promise.all(createRequests);

    Toast.success(`新建成功，一共创建了 ${newSites.length} 个站点。`);
    pageRefetch();
};

const handleSaveInBatch = async () => {
    try {
        const promises = sites.value?.map((site: NavSite, index: number) => {
            (site as any).spec.priority = index;
            return axiosInstance.put(`/apis/zenNavigator.lik.cc/v1alpha1/navsite/${site.metadata.name}`, site);
        });
        if (promises) {
            await Promise.all(promises);
        }
    } catch (e) {
        console.error(e);
    } finally {
        refetch();
    }
};

const groupSelectHandle = (group?: string) => {
    selectedGroup.value = group;
};

const pageRefetch = async () => {
    await groupListRef.value.refetch();
    await refetch();
    selectedSites.value = new Set<NavSite>();
};

const onEditingModalClose = () => {
    editingModal.value = false;
    refetch();
};

</script>
<template>
    <NavSiteEditingModal
            v-if="editingModal"
            :site="selectedSite"
            :group="selectedGroup"
            @close="onEditingModalClose"
            @saved="pageRefetch"
    >
        <template #append-actions>
      <span @click="handleSelectPrevious">
        <IconArrowLeft />
      </span>
            <span @click="handleSelectNext">
        <IconArrowRight />
      </span>
        </template>
    </NavSiteEditingModal>
    <AttachmentSelectorModal v-model:visible="attachmentModal" :accepts="['image/*']" @select="onAttachmentsSelect" />
    <VPageHeader title="禅导航">
        <template #icon>
            <TablerDeviceGamepad3 />
        </template>
    </VPageHeader>
    <div class=":uno: p-4">
        <div class=":uno: flex flex-col gap-2 lg:flex-row">
            <div class=":uno: w-full flex-none lg:w-96">
                <GroupList ref="groupListRef" @select="groupSelectHandle" />
            </div>
            <div class=":uno: min-w-0 flex-1 shrink">
                <VCard>
                    <template #header>
                        <div class=":uno: block w-full bg-gray-50 px-4 py-3">
                            <div class=":uno: relative flex flex-col items-start sm:flex-row sm:items-center">
                                <div class=":uno: mr-4 hidden items-center sm:flex">
                                    <input v-model="checkedAll" type="checkbox" @change="handleCheckAllChange" />
                                </div>
                                <div class=":uno: w-full flex flex-1 sm:w-auto">
                                    <SearchInput v-if="!selectedSites.size" v-model="keyword" />
                                    <VSpace v-else>
                                        <VButton type="danger" @click="handleDeleteInBatch"> 删除 </VButton>
                                    </VSpace>
                                </div>
                                <div v-if="selectedGroup" v-permission="['plugin:zen-navigator:manage']" class=":uno: mt-4 flex sm:mt-0">
                                    <VDropdown>
                                        <VButton size="xs"> 新增 </VButton>
                                        <template #popper>
                                            <VDropdownItem @click="handleOpenEditingModal()"> 新增 </VDropdownItem>
                                            <VDropdownItem @click="attachmentModal = true"> 从附件库选择 </VDropdownItem>
                                        </template>
                                    </VDropdown>
                                </div>
                            </div>
                        </div>
                    </template>
                    <VLoading v-if="isLoading" />
                    <Transition v-else-if="!selectedGroup" appear name="fade">
                        <VEmpty message="请选择或新建分组类型" title="未选择分组类型"></VEmpty>
                    </Transition>
                    <Transition v-else-if="!searchResults.length" appear name="fade">
                        <VEmpty message="你可以尝试刷新或者新建站点" title="当前没有站点">
                            <template #actions>
                                <VSpace>
                                    <VButton @click="refetch"> 刷新</VButton>
                                    <VButton v-permission="['plugin:zen-navigator:manage']" type="primary" @click="handleOpenEditingModal()">
                                        <template #icon>
                                            <IconAddCircle class=":uno: size-full" />
                                        </template>
                                        新增站点
                                    </VButton>
                                </VSpace>
                            </template>
                        </VEmpty>
                    </Transition>
                    <Transition v-else appear name="fade">
                        <VueDraggable
                                v-model="sites"
                                class=":uno: grid grid-cols-1 mt-2 gap-x-2 gap-y-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5"
                                group="navsite"
                                handle=".drag-element"
                                item-key="metadata.name"
                                tag="div"
                                role="list"
                                @update="handleSaveInBatch"
                        >
                            <VCard
                                    v-for="site in sites"
                                    :key="site.metadata.name"
                                    :body-class="[':uno: !p-0']"
                                    :class="{
                  ':uno: ring-primary ring-1': isChecked(site),
                  ':uno: ring-1 ring-red-600': site.metadata.deletionTimestamp,
                }"
                                    class=":uno: hover:shadow drag-element "
                                    @click="handleOpenEditingModal(site)"
                            >
                                <div class=":uno: group relative bg-white">
                                    <div class=":uno: block aspect-16/9 size-full cursor-pointer overflow-hidden bg-gray-100 relative">
                                        <LazyImage
                                                :key="site.metadata.name"
                                                :alt="site.spec.name"
                                                :src="site.spec.icon || site.spec.url"
                                                classes="size-full pointer-events-none group-hover:opacity-75"
                                        >
                                            <template #loading>
                                                <div class=":uno: h-full flex justify-center">
                                                    <VLoading></VLoading>
                                                </div>
                                            </template>
                                            <template #error>
                                                <div class=":uno: h-full flex items-center justify-center object-cover">
                                                    <span class=":uno: text-xs text-red-400"> 加载异常 </span>
                                                </div>
                                            </template>
                                        </LazyImage>
                                    </div>

                                    <p
                                            v-tooltip="site.spec.name"
                                            class=":uno: block cursor-pointer truncate px-2 py-1 text-center text-xs text-gray-700 font-medium"
                                    >
                                        {{ site.spec.name }}
                                    </p>

                                    <div v-if="site.metadata.deletionTimestamp" class=":uno: absolute top-1 right-1 text-xs text-red-300">
                                        删除中...
                                    </div>

                                    <div
                                            v-if="!site.metadata.deletionTimestamp"
                                            v-permission="['plugin:zen-navigator:manage']"
                                            :class="{ ':uno: !flex': selectedSites.has(site) }"
                                            class=":uno: absolute left-0 top-0 hidden h-1/3 w-full cursor-pointer justify-end from-gray-300 to-transparent bg-gradient-to-b ease-in-out group-hover:flex"
                                            @click.stop="selectedSites.has(site) ? selectedSites.delete(site) : selectedSites.add(site)"
                                    >
                                        <IconCheckboxFill
                                                :class="{
                        ':uno: !text-primary': selectedSites.has(site),
                      }"
                                                class=":uno: hover:text-primary mr-1 mt-1 h-6 w-6 cursor-pointer text-white transition-all"
                                        />
                                    </div>
                                </div>
                            </VCard>
                        </VueDraggable>
                    </Transition>

                    <template #footer>
                        <VPagination v-model:page="page" v-model:size="size" :total="total" :size-options="[20, 30, 50, 100]" />
                    </template>
                </VCard>
            </div>
        </div>
    </div>
</template>
