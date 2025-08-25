<script lang="ts" setup>
import type { NavSite } from "@/types";
import { submitForm } from "@formkit/core";
import { axiosInstance } from "@halo-dev/api-client";
import { VButton, VModal, VSpace } from "@halo-dev/components";
import { useMagicKeys } from "@vueuse/core";
import { cloneDeep } from "lodash-es";
import {computed, nextTick, onMounted, ref, useTemplateRef, watch} from "vue";

const props = withDefaults(
        defineProps<{
            site?: NavSite;
            group?: string;
        }>(),
        {
            site: undefined,
            group: undefined,
        }
);

const emit = defineEmits<{
    (event: "close"): void;
    (event: "saved", site: NavSite): void;
}>();

const initialFormState: NavSite = {
    metadata: {
        name: "",
        generateName: "navsite-",
    },
    spec: {
        name: "",
        url: "",
        icon: "",
        groupName: props.group || "",
        description: "",
    },
    kind: "NavSite",
    apiVersion: "ZenNavigator.lik.cc/v1alpha1",
} as NavSite;

const formState = ref<NavSite>(cloneDeep(initialFormState));
const isSubmitting = ref(false);
const modal = useTemplateRef<InstanceType<typeof VModal> | null>("modal");

const isUpdateMode = computed(() => {
    return !!formState.value.metadata.creationTimestamp;
});
const isMac = /macintosh|mac os x/i.test(navigator.userAgent);
const modalTitle = computed(() => {
    return isUpdateMode.value ? "编辑站点" : "添加站点";
});
const annotationsGroupFormRef = ref();

const handleCreateOrUpdateSite = async () => {
    annotationsGroupFormRef.value?.handleSubmit();
    await nextTick();
    const { customAnnotations, annotations, customFormInvalid, specFormInvalid } = annotationsGroupFormRef.value || {};
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
            await axiosInstance.put(
                    `/apis/console.api.zenNavigator.lik.cc/v1alpha1/navsites/${formState.value.metadata.name}`,
                    formState.value
            );
        } else {
            if (props.group) {
                formState.value.spec.groupName = props.group;
            }
            const { data } = await axiosInstance.post(`/apis/console.api.zenNavigator.lik.cc/v1alpha1/navsites`, formState.value);
            emit("saved", data as NavSite);
        }
        modal.value?.close();
    } catch (e) {
        console.error(e);
    } finally {
        isSubmitting.value = false;
    }
};

onMounted(() => {
    if (props.site) {
        formState.value = cloneDeep(props.site);
    }
});

const { ControlLeft_Enter, Meta_Enter } = useMagicKeys();

watch(ControlLeft_Enter, (v) => {
    if (v && !isMac) {
        submitForm("navsite-form");
    }
});

watch(Meta_Enter, (v) => {
    if (v && isMac) {
        submitForm("navsite-form");
    }
});
</script>
<template>
    <VModal ref="modal" :width="650" :title="modalTitle" @close="emit('close')">
        <template #actions>
            <slot name="append-actions" />
        </template>

        <FormKit
                id="navsite-form"
                v-model="formState.spec"
                name="navsite-form"
                :actions="false"
                :config="{ validationVisibility: 'submit' }"
                type="form"
                @submit="handleCreateOrUpdateSite"
        >
            <div class=":uno: md:grid md:grid-cols-4 md:gap-6">
                <div class=":uno: md:col-span-1">
                    <div class=":uno: sticky top-0">
                        <span class=":uno: text-base text-gray-900 font-medium"> 常规 </span>
                    </div>
                </div>
                <div class=":uno: mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
                    <FormKit name="name" label="名称" type="text" validation="required"></FormKit>
                    <FormKit name="icon" label="图标" type="text"></FormKit>
                    <FormKit name="url" label="网址" type="text"></FormKit>
                    <FormKit name="description" label="描述" type="textarea"></FormKit>
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
                        ref="annotationsGroupFormRef"
                        :value="formState.metadata.annotations"
                        kind="NavSite"
                        group="ZenNavigator.lik.cc"
                />
            </div>
        </div>
        <template #footer>
            <VSpace>
                <VButton :loading="isSubmitting" type="secondary" @click="submitForm('navsite-form')"> 保存 </VButton>
                <VButton @click="modal?.close()">取消</VButton>
            </VSpace>
        </template>
    </VModal>
</template>
