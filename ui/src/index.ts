import { definePlugin } from "@halo-dev/console-shared";
import { defineAsyncComponent, markRaw } from "vue";
import TablerDeviceGamepad3 from '~icons/streamline-freehand-color/website-development-browser-hand'
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
            name: "禅导航",
            group: "content",
            icon: markRaw(TablerDeviceGamepad3),
          },
        },
      },
    },
  ],
});
