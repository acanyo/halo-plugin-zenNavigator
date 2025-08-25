import { definePlugin } from "@halo-dev/console-shared";
import { defineAsyncComponent, markRaw } from "vue";
import TablerDeviceGamepad3 from '~icons/tabler/device-gamepad-3'
import "uno.css";
import { VLoading } from "@halo-dev/components";

export default definePlugin({
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/navigation",
        name: "Navigation",
        component: defineAsyncComponent({
          loader: () => import("@/views/NavSiteList.vue"),
          loadingComponent: VLoading,
        }),
        meta: {
          permissions: ["plugin:zen-navigator:view"],
          menu: {
            name: "导航管理",
            group: "content",
            icon: markRaw(TablerDeviceGamepad3),
          },
        },
      },
    },
  ],
});
