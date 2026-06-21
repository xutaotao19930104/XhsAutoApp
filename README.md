# 小红书自动化App使用说明

## 功能介绍

本App基于Android无障碍服务开发，可实现：
1. **自动发帖** - 从本地JSON读取图文内容，自动发布到小红书
2. **自动评论** - 搜索指定关键词的帖子，自动添加评论

## 安装步骤

### 方式一：使用Android Studio构建（推荐）

1. **安装Android Studio**
   - 下载地址：https://developer.android.com/studio
   - 安装后打开，等待初始化完成

2. **打开项目**
   - File → Open → 选择 `XhsAutoApp` 文件夹
   - 等待Gradle同步完成

3. **构建APK**
   - 菜单：Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 等待构建完成
   - APK位置：`app/build/outputs/apk/debug/app-debug.apk`

4. **安装到手机**
   - 将APK复制到手机
   - 点击安装（需开启"允许安装未知来源应用"）

### 方式二：直接使用已有APK

如果有现成的APK文件，直接安装即可。

## 使用方法

### 1. 首次配置

1. 打开"小红书助手"App
2. 点击"开启无障碍服务"
3. 在系统设置中找到"小红书助手"并开启
4. 返回App，确认状态显示"已开启 ✓"

### 2. 配置文件

配置文件位置：`/sdcard/xhsauto/config.json`

```json
{
  "posts": [
    {
      "title": "帖子标题",
      "content": "帖子正文内容\n\n#话题1 #话题2",
      "images": [
        "/sdcard/xhsauto/images/photo1.jpg",
        "/sdcard/xhsauto/images/photo2.jpg"
      ]
    }
  ],
  "comments": {
    "keywords": ["搜索关键词1", "搜索关键词2"],
    "default_comment": "默认评论文案"
  }
}
```

**配置说明：**
- `posts`: 发帖内容数组
  - `title`: 帖子标题
  - `content`: 帖子正文（支持换行和话题标签）
  - `images`: 图片路径数组（绝对路径）
- `comments`: 评论配置
  - `keywords`: 搜索关键词数组
  - `default_comment`: 默认评论文案

### 3. 准备图片

1. 在手机创建目录：`/sdcard/xhsauto/images/`
2. 将要发布的图片放入此目录
3. 确保图片路径与配置文件中一致

### 4. 发帖功能

1. 确保小红书App已登录
2. 打开"小红书助手"
3. 点击"自动发帖"
4. 脚本会自动：
   - 打开小红书
   - 点击发布按钮
   - 选择图片
   - 填写标题和正文
   - 点击发布

### 5. 评论功能

1. 确保小红书App已登录
2. 打开"小红书助手"
3. 点击"自动评论"
4. 脚本会自动：
   - 打开小红书
   - 搜索指定关键词
   - 进入第一个帖子
   - 输入评论并发送

## 注意事项

### 权限要求
- **无障碍服务** - 必须开启，这是自动化的核心
- **存储权限** - 读取配置文件和图片
- **网络权限** - 小红书需要网络

### 使用限制
- 建议不要过于频繁使用，避免被封号
- 每次操作间隔已内置，可根据需要调整代码
- 脚本基于界面元素定位，小红书更新后可能需要调整

### 问题排查

**Q: 无法安装APK？**
A: 需要在手机设置中开启"允许安装未知来源应用"

**Q: 无障碍服务无法开启？**
A: 部分手机需要在设置 → 应用 → 特殊权限中开启

**Q: 自动化不工作？**
A: 检查无障碍服务是否开启，查看App日志输出

**Q: 找不到界面元素？**
A: 小红书版本更新可能导致界面变化，需要用Android Studio的Layout Inspector重新定位

## 技术说明

### 项目结构
```
XhsAutoApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/xhsauto/app/
│   │   │   ├── MainActivity.java          # 主界面
│   │   │   └── XhsAccessibilityService.java  # 无障碍服务
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml   # 界面布局
│   │   │   ├── values/strings.xml         # 字符串资源
│   │   │   └── xml/accessibility_service_config.xml  # 服务配置
│   │   └── AndroidManifest.xml            # 应用配置
│   └── build.gradle                       # 模块构建配置
├── build.gradle                           # 项目构建配置
├── settings.gradle                        # 项目设置
└── config.json                            # 配置文件示例
```

### 核心技术
- **Accessibility Service** - Android无障碍服务，用于自动化操作
- **Gesture Description** - 模拟用户点击操作
- **Node Info** - 获取界面元素信息
- **Gson** - 解析JSON配置文件

## 免责声明

本App仅供学习交流使用，请遵守小红书平台规则。使用本App产生的一切后果由用户自行承担。

## 更新日志

### v1.0 (2024-01-01)
- 初始版本
- 支持自动发帖功能
- 支持自动评论功能
- JSON配置文件支持
