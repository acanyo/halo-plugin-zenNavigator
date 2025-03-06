# 禅导航 (ZenNavigator)

化繁为简，专注所需！

![作者](https://img.shields.io/badge/作者-Handsome-blue)
![版本](https://img.shields.io/badge/版本-1.0.0-green)
![许可证](https://img.shields.io/badge/许可证-MIT-orange)

## 项目概述

ZenNavigator 是一个精心设计的网站导航插件，旨在为用户提供一个简洁、美观且功能丰富的导航体验。项目采用现代化的设计理念，支持响应式布局，适配各种设备屏幕。通过分类展示网站链接，用户可以快速找到所需资源。

## 功能特点

### 核心功能

- **分类导航**：支持多级分类，层次清晰地展示网站链接
- **主题切换**：内置明暗两种主题模式，可根据系统设置自动切换或手动选择
- **动态通知**：灵动岛式通知系统，展示欢迎语和系统通知
- **动态背景**：支持图片和视频背景，可自定义透明度
- **响应式设计**：完美适配桌面端、平板和移动设备

### 用户体验

- **平滑动画**：所有交互都有精心设计的过渡动画，提升用户体验
- **懒加载**：图片资源采用懒加载技术，提高页面加载速度
- **无障碍设计**：支持键盘导航和屏幕阅读器
- **自适应布局**：根据不同设备自动调整布局和元素大小

## 功能详解

### 1. 分类导航系统

导航站支持多级分类结构，包括主分类和子分类。每个子分类下可以包含多个网站链接。系统会根据配置自动生成分类菜单和网站卡片。

### 2. 主题系统

- **自动主题**：根据系统偏好自动切换明暗主题
- **手动切换**：通过主题切换按钮手动切换主题

### 3. 通知系统

- **欢迎通知**：根据时间段显示不同的欢迎语
- **系统通知**：支持HTML内容的系统通知
- **动画效果**：通知显示和消失都有平滑的动画效果

### 4. 背景系统

- **多媒体支持**：支持图片和视频背景
- **随机背景**：从配置的背景列表中随机选择一个显示
- **透明度控制**：可自定义背景透明度

## 安装与使用

### 安装步骤


目前此插件为主题端提供了 /nav 路由，
模板为 nav.html，也提供了 Finder API，可以将导航列表渲染到任何地方。
## 自定义开发
路由信息
模板路径：/templates/nav.html
访问路径：/nav
## Finder API
**/api/navigation**

返回值

Json

示例
`{
"siteName": "Handsome主页导航",
"logo": "https://www.lik.cc/upload/logo.png",
"theme": "light",
"bgImages": "https://img-baofun.zhhainiao.com/pcwallpaper_ugc/preview/9b09b3e56cc7abc842f2985bafdc4c74_preview.mp4",
"iconfont": "//at.alicdn.com/t/c/font_4714658_73f7wduo2u7.css",
"transparency": "30",
"enableSmart": true,
"content": "",
"categories": [
{
"id": "link-group-fTmnX",
"name": "测试工具1",
"icon": "handsome icon-pengyouquan",
"inTopMenu": true,
"subcategories": [
{
"id": "ea98550a-780b-49c9-aa0b-f887fa1b3ea1",
"owner_id": "link-group-fTmnX",
"name": "测试2",
"icon": "handsome icon-tuku",
"sites": [
{
"name": "首页｜webjing",
"url": "https://blog.webjing.cn/",
"icon": "https://blog.webjing.cn/upload/icon/favicon.ico",
"description": "记录一个非科班编程小白的coding 之路。\n路虽远行则将至，事虽难做则必成！"
}
]
},
{
"id": "84309228-5bd1-4bff-a1fd-d641e2e15482",
"owner_id": "link-group-fTmnX",
"name": "最帅的博客",
"icon": "handsome icon-tuku",
"sites": [
{
"name": "首页｜Handsome",
"url": "https://www.lik.cc/",
"icon": "https://www.lik.cc/upload/logo.png",
"description": "心若有所向往,何惧道阻且长"
}
]
}
]
}
]
}`

## 贡献指南

git clone git@github.com:acanyo/halo-plugin-zenNavigator.git

# 或者当你 fork 之后
git clone git@github.com:{your_github_id}/halo-plugin-zenNavigator.git
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
# macOS / Linux
./gradlew haloServer

# Windows
./gradlew.bat haloServer
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件

## 联系方式

- 作者：Handsome
- 网站：[https://lik.cc](https://lik.cc)
---

