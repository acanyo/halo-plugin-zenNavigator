<script lang="ts" setup>
import type {NavSite} from "@/types";
import {submitForm} from "@formkit/core";
import {axiosInstance} from "@halo-dev/api-client";
import {Toast, VButton, VLoading, VModal, VSpace} from "@halo-dev/components";
import MdiWebRefresh from "~icons/mdi/web-refresh";
import {useMagicKeys} from "@vueuse/core";
import {cloneDeep} from "lodash-es";
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
    apiVersion: "zenNavigator.lik.cc/v1alpha1",
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
const loading = ref(false);
const handleGetLinkDetail = async () => {
    if (loading.value) {
        return;
    }
    const url = formState.value.spec?.url;
    if (!url) {
        return;
    }
    loading.value = true;
    try {
        const {data} = await axiosInstance.get(`/apis/api.zenNavigator.lik.cc/v1alpha1/link-detail`, {
            params: { url },
        });

        formState.value.spec!.name = data.title || "";
        formState.value.spec!.icon = data.icon;
        formState.value.spec!.description = data.description;

        Toast.info("获取链接详情成功");
    } catch (e) {
        console.error(e);
        Toast.error("获取链接详情失败");
    } finally {
        loading.value = false;
    }
};
const handleCreateOrUpdateSite = async () => {
    annotationsGroupFormRef.value?.handleSubmit();
    await nextTick();
    const {customAnnotations, annotations, customFormInvalid, specFormInvalid} = annotationsGroupFormRef.value || {};
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
                    `/apis/zenNavigator.lik.cc/v1alpha1/navsite/${formState.value.metadata.name}`,
                    formState.value
            );
        } else {
            if (props.group) {
                formState.value.spec.groupName = props.group;
            }
            const {data} = await axiosInstance.post(`/apis/zenNavigator.lik.cc/v1alpha1/navsite`, formState.value);
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

const {ControlLeft_Enter, Meta_Enter} = useMagicKeys();

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
            <slot name="append-actions"/>
        </template>

        <FormKit
                id="navsite-form"
                v-model="formState.spec"
                name="navsite-form"
                :config="{ validationVisibility: 'submit' }"
                :disabled="loading"
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
                    <FormKit type="url" name="url" validation="required" label="网站地址">
                        <template #suffix>
                            <div
                                    v-tooltip="{
                  content: '获取网站信息',
                }"
                                    class=":uno: group h-full flex cursor-pointer items-center px-3 transition-all"
                                    @click="handleGetLinkDetail"
                            >
                                <VLoading v-if="loading" class=":uno: size-4 text-gray-500 group-hover:text-gray-700"/>
                                <MdiWebRefresh v-else class=":uno: size-4 text-gray-500 group-hover:text-gray-700"/>
                            </div>
                        </template>
                    </FormKit>
                    <FormKit name="name" label="名称" type="text" validation="required"></FormKit>
                    <FormKit name="icon" label="图标" type="attachment" :accepts="['image/*']"></FormKit>
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
                        group="zenNavigator.lik.cc"
                />
            </div>
        </div>
        <template #footer>
            <VSpace>
                <!-- @vue-ignore -->
                <VButton :loading="isSubmitting" type="secondary" @click="$formkit.submit('navsite-form')"> 保存</VButton>
                <VButton @click="modal?.close()">取消</VButton>
            </VSpace>
        </template>
    </VModal>
</template>
