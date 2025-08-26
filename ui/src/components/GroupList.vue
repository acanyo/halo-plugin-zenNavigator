<script lang="ts" setup>
import type { NavGroup, NavGroupList, NavSiteList } from "../types/index";
import { axiosInstance } from "@halo-dev/api-client";
import {
    Dialog,
    IconList,
    VButton,
    VCard,
    VDropdownItem,
    VEmpty,
    VEntity,
    VEntityField,
    VLoading,
    VStatusDot,
} from "@halo-dev/components";
import { useQuery } from "@tanstack/vue-query";
import { useRouteQuery } from "@vueuse/router";
import { ref, watch } from "vue";
import { VueDraggable } from "vue-draggable-plus";
import GroupEditingModal from "./GroupEditingModal.vue";

const emit = defineEmits<{
    (event: "select", group?: string): void;
}>();

const groupEditingModal = ref(false);

const updateGroup = ref<NavGroup>();

const selectedGroup = useRouteQuery<string>("nav-group");

const groups = ref<NavGroup[]>([]);

async function computeGroupCounts() {
    if (!groups.value?.length) return;
    const promises = groups.value.map(async (group) => {
        try {
            const { data } = await axiosInstance.get<NavSiteList>(
                "/apis/api.zenNavigator.lik.cc/v1alpha1/navsites",
                { params: { page: 1, size: 1, group: group.metadata.name } }
            );
            const count = data.total ?? (data.items?.length || 0);
            group.status = { ...(group.status || {}), equipmentCount: count } as any;
        } catch (e) {
            group.status = { ...(group.status || {}), equipmentCount: 0 } as any;
        }
    });
    await Promise.all(promises);
}

const { refetch, isLoading } = useQuery<NavGroup[]>({
    queryKey: ["plugin:zen-navigator:groups"],
    queryFn: async () => {
        const { data } = await axiosInstance.get<NavGroupList>("/apis/zenNavigator.lik.cc/v1alpha1/navgroup");
        return data.items
                .map((group) => {
                    if (group.spec) {
                        group.spec.priority = group.spec.priority || 0;
                    }
                    return group;
                })
                .sort((a, b) => {
                    return (a.spec?.priority || 0) - (b.spec?.priority || 0);
                });
    },
    refetchInterval(data) {
        const hasDeletingGroup = data?.some((group) => !!group.metadata.deletionTimestamp);
        return hasDeletingGroup ? 1000 : false;
    },
    async onSuccess(data) {
        groups.value = data;
        await computeGroupCounts();

        if (selectedGroup.value) {
            const groupNames = data.map((group) => group.metadata.name);
            if (groupNames.includes(selectedGroup.value)) {
                emit("select", selectedGroup.value);
            } else if (data.length) {
                handleSelectedClick(data[0]);
            }
        } else if (data.length) {
            handleSelectedClick(data[0]);
        } else {
            selectedGroup.value = "";
            emit("select", "");
        }
    },
    refetchOnWindowFocus: false,
});

// compute counts when groups change (fallback)
watch(
    () => groups.value?.map(g => g.metadata.name).join(','),
    async () => {
        await computeGroupCounts();
    },
    { immediate: true }
);

const handleSaveInBatch = async () => {
    try {
        const promises = groups.value?.map((group: NavGroup, index: number) => {
            if (group.spec) {
                group.spec.priority = index;
            }
            return axiosInstance.put(`/apis/zenNavigator.lik.cc/v1alpha1/navgroup/${group.metadata.name}`, group);
        });
        if (promises) {
            await Promise.all(promises);
        }
    } catch (e) {
        console.error(e);
    } finally {
        await refetch();
        await computeGroupCounts();
    }
};

const handleDelete = async (group: NavGroup) => {
    Dialog.warning({
        title: "确定要删除该分组吗？",
        description: "该操作不可恢复。",
        confirmType: "danger",
        onConfirm: async () => {
            try {
                await axiosInstance.delete(`/apis/zenNavigator.lik.cc/v1alpha1/navgroup/${group.metadata.name}`);
            } catch (e) {
                console.error(e);
            } finally {
                await refetch();
                await computeGroupCounts();
            }
        },
    });
};

const handleOpenEditingModal = (group?: NavGroup) => {
    groupEditingModal.value = true;
    updateGroup.value = group;
};

const handleSelectedClick = (group: NavGroup) => {
    selectedGroup.value = group.metadata.name;
    emit("select", group.metadata.name);
};

defineExpose({
    refetch,
});

function onGroupEditingModalClose() {
    groupEditingModal.value = false;
    refetch();
}
</script>
<template>
    <GroupEditingModal v-if="groupEditingModal" :group="updateGroup" @close="onGroupEditingModalClose" />
    <VCard :body-class="[':uno: !p-0']" title="类型">
        <VLoading v-if="isLoading" />
        <Transition v-else-if="!groups || !groups.length" appear name="fade">
            <VEmpty message="你可以尝试刷新或者新建类型分组" title="当前没有类型分组">
                <template #actions>
                    <VSpace>
                        <VButton size="sm" @click="refetch()"> 刷新</VButton>
                    </VSpace>
                </template>
            </VEmpty>
        </Transition>
        <Transition v-else appear name="fade">
            <div class=":uno: w-full overflow-x-auto">
                <table class=":uno: w-full border-spacing-0">
                    <VueDraggable
                            v-model="groups"
                            class=":uno: divide-y divide-gray-100"
                            group="group"
                            handle=".drag-element"
                            item-key="metadata.name"
                            tag="tbody"
                            @update="handleSaveInBatch"
                    >
                        <VEntity
                                v-for="group in groups"
                                :key="group.metadata.name"
                                :is-selected="selectedGroup === group.metadata.name"
                                class=":uno: group"
                                @click="handleSelectedClick(group)"
                        >
                            <template #prepend>
                                <div
                                        class=":uno: drag-element absolute inset-y-0 left-0 hidden w-3.5 cursor-move items-center bg-gray-100 transition-all group-hover:flex hover:bg-gray-200"
                                >
                                    <IconList class=":uno: size-3.5" />
                                </div>
                            </template>

                            <template #start>
                                <VEntityField
                                        :title="group.spec?.name"
                                        :description="`${group.status?.equipmentCount || 0} 个站点`"
                                ></VEntityField>
                            </template>

                            <template #end>
                                <VEntityField v-if="group.metadata.deletionTimestamp">
                                    <template #description>
                                        <VStatusDot v-tooltip="`删除中`" state="warning" animate />
                                    </template>
                                </VEntityField>
                            </template>

                            <template #dropdownItems>
                                <VDropdownItem @click="handleOpenEditingModal(group)"> 修改 </VDropdownItem>
                                <VDropdownItem type="danger" @click="handleDelete(group)"> 删除 </VDropdownItem>
                            </template>
                        </VEntity>
                    </VueDraggable>
                </table>
            </div>
        </Transition>

        <template v-if="!isLoading" #footer>
            <Transition appear name="fade">
                <!-- @unocss-skip-start -->
                <VButton
                        v-permission="['plugin:zen-navigator:manage']"
                        block
                        type="secondary"
                        @click="handleOpenEditingModal(undefined)"
                >
                    新增类型
                </VButton>
            </Transition>
        </template>
    </VCard>
</template>
