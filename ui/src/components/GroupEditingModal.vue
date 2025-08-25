<script lang="ts" setup>
import type { NavGroup } from "@/types";
import { submitForm } from "@formkit/core";
import { axiosInstance } from "@halo-dev/api-client";
import { VSpace, VButton, VModal } from "@halo-dev/components";
import { cloneDeep } from "lodash-es";
import {computed, nextTick, onMounted, ref, useTemplateRef} from "vue";

const props = withDefaults(
        defineProps<{
            group?: NavGroup;
        }>(),
        {
            group: undefined,
        }
);

const emit = defineEmits<{
    (event: "close"): void;
    (event: "saved", group: NavGroup): void;
}>();

const initialFormState: NavGroup = {
    metadata: {
        name: "",
        generateName: "nav-group-",
    },
    spec: {
        name: "",
        icon: "",
        inTopMenu: false,
        priority: 0,
    },
    kind: "NavGroup",
    apiVersion: "zenNavigator.lik.cc/v1alpha1",
} as NavGroup;

const formState = ref<NavGroup>(cloneDeep(initialFormState));
const isSubmitting = ref(false);
const modal = useTemplateRef<InstanceType<typeof VModal> | null>("modal");

const isUpdateMode = computed(() => {
    return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
    return isUpdateMode.value ? "编辑分组" : "新建分组";
});

onMounted(() => {
    if (props.group) {
        formState.value = cloneDeep(props.group);
    }
});

const annotationsFormRef = ref();

const handleSaveGroup = async () => {
    annotationsFormRef.value?.handleSubmit();
    await nextTick();
    const { customAnnotations, annotations, customFormInvalid, specFormInvalid } = annotationsFormRef.value || {};
    if (customFormInvalid || specFormInvalid) {
        return;
    }
    formState.value.metadata.annotations = {
        ...annotations,
        ...customAnnotations,
    };
    try {
        isSubmitting.value = true;
        if (isUpdateMode.value) {
            await axiosInstance.put<NavGroup>(
                    `/apis/zenNavigator.lik.cc/v1alpha1/navgroup/${formState.value.metadata.name}`,
                    formState.value
            );
        } else {
            const { data } = await axiosInstance.post<NavGroup>(`/apis/zenNavigator.lik.cc/v1alpha1/navgroup`, formState.value);
            emit("saved", data);
        }
        modal.value?.close();
    } catch (e) {
        console.error(e);
    } finally {
        isSubmitting.value = false;
    }
};
</script>
<template>
    <VModal ref="modal" :title="modalTitle" :width="650" @close="emit('close')">
        <template #actions>
            <slot name="append-actions" />
        </template>

        <FormKit
                id="nav-group-form"
                v-model="formState.spec"
                name="nav-group-form"
                :actions="false"
                :config="{ validationVisibility: 'submit' }"
                type="form"
                @submit="handleSaveGroup"
        >
            <div class=":uno: md:grid md:grid-cols-4 md:gap-6">
                <div class=":uno: md:col-span-1">
                    <div class=":uno: sticky top-0">
                        <span class=":uno: text-base text-gray-900 font-medium"> 常规 </span>
                    </div>
                </div>
                <div class=":uno: mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
                    <FormKit name="name" label="分组名称" type="text" validation="required"></FormKit>
                    <FormKit name="icon" label="图标" type="attachment" :accepts="['image/*']"></FormKit>
                    <FormKit name="inTopMenu" label="顶部菜单显示" type="checkbox"></FormKit>
                    <FormKit name="priority" label="优先级" type="number"></FormKit>
                </div>
            </div>
        </FormKit>
        <div class=":uno: py-5">
            <div class=":uno: border-t border-gray-200"></div>
        </div>
        <div class=":uno: md:grid md:grid-cols-4 md:gap-6">
            <div class=":uno: md:col-span-1">
                <div class=":uno: sticky top-0">
                    <span class=":uno: text-base text-gray-900 font-medium"> 元数据 </span>
                </div>
            </div>
            <div class=":uno: mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
                <AnnotationsForm
                        :key="formState.metadata.name"
                        ref="annotationsFormRef"
                        :value="formState.metadata.annotations"
                        kind="NavGroup"
                        group="enNavigator.lik.cc"
                />
            </div>
        </div>
        <template #footer>
            <VSpace>
                <VButton :loading="isSubmitting" type="secondary" @click="submitForm('nav-group-form')"> 保存 </VButton>
                <VButton @click="modal?.close()">取消</VButton>
            </VSpace>
        </template>
    </VModal>
</template>
