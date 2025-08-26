# 禅导航 (ZenNavigator)

Halo 2.0 的禅导航, 支持在 Console 进行管理以及为主题端提供 `/nav` 页面路由。

插件后台由 [图库管理](https://www.halo.run/store/apps/app-BmQJW) 插件 和 [链接管理](https://www.halo.run/store/apps/app-hfbQg) 插件 改编而来

![作者](https://img.shields.io/badge/作者-Handsome-blue)
![版本](https://img.shields.io/badge/版本-2.0.0-green)
![许可证](https://img.shields.io/badge/许可证-MIT-orange)

# 💬交流
![群.png](https://www.lik.cc/upload/iShot_2025-03-03_16.03.00.png)


## 项目概述
演示站 : [https://lik.cc/nav](https://lik.cc/nav)



## 使用方式
1. 安装，插件安装和更新方式可参考：<https://docs.halo.run/user-guide/plugins>
2. 安装完成之后，访问 Console 左侧的**禅导航**菜单项，即可进行管理。
3. 内置模板，无需主题支持，但也可以通过主题自定义模板。
4. 前台访问地址为 `/nav`，主题提供模板（nav.html）。

## 使用文档

https://docs.lik.cc/

## 开发环境

```bash
git clone https://github.com/acanyo/halo-plugin-zenNavigator.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/halo-plugin-zenNavigator.git
```

```bash
cd path/to/plugins-zenNavigator/

```

```bash
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
```

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    classes-directories:
      - "build/classes"
      - "build/resources"
    lib-directories:
      - "libs"
    fixedPluginPath:
      - "/path/to/plugin-zenNavigator"
```
## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件

## 联系方式

- 作者：Handsome
- 网站：[https://lik.cc](https://lik.cc)
---

