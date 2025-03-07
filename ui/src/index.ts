import { definePlugin } from "@halo-dev/console-shared";
import NavigatorView from "./views/NavigatorView.vue";
import CategoryEditView from "./views/CategoryEditView.vue";
import SettingsView from "./views/SettingsView.vue";
import { markRaw } from "vue";
import RiNavigationFill from "~icons/ri/navigation-fill";

// 导入链接管理页面组件
// 注意：这个组件需要创建，下一步我们会创建它
import LinkManagerView from "./views/LinkManagerView.vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/navigator",
        name: "Navigator",
        component: NavigatorView,
        meta: {
          title: "网址导航",
          menu: {
            name: "网址导航",
            group: "content",
            icon: markRaw(RiNavigationFill),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/navigator/categories/create",
        name: "CreateCategory",
        component: CategoryEditView,
        meta: {
          title: "新增分类",
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/navigator/categories/edit/:name",
        name: "EditCategory",
        component: CategoryEditView,
        meta: {
          title: "编辑分类",
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/navigator/settings",
        name: "NavigatorSettings",
        component: SettingsView,
        meta: {
          title: "网址导航设置",
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/navigator/categories/:categoryName/subcategory/:subcategoryIndex/links",
        name: "LinkManager",
        component: LinkManagerView,
        meta: {
          title: "链接管理",
        },
      },
    },
  ],
  extensionPoints: {},
});
