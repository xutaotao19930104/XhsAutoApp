# GitHub Actions 自动构建APK说明

## 快速开始

### 方式一：使用GitHub CLI（最简单）

1. **安装GitHub CLI**
   ```bash
   brew install gh
   ```

2. **登录GitHub**
   ```bash
   gh auth login
   ```
   按提示完成登录

3. **创建仓库并推送**
   ```bash
   cd XhsAutoApp
   gh repo create XhsAutoApp --public --source=. --push
   ```

4. **等待构建完成**
   - 访问: https://github.com/你的用户名/XhsAutoApp/actions
   - 等待构建完成（约2-3分钟）
   - 点击最新的构建任务
   - 在Artifacts区域下载APK

### 方式二：手动操作

1. **创建GitHub账号**（如果没有）
   - 访问: https://github.com

2. **创建新仓库**
   - 点击右上角"+" → "New repository"
   - 仓库名称: `XhsAutoApp`
   - 选择: Public
   - **不要**勾选 "Add a README file"
   - 点击 "Create repository"

3. **推送代码**
   ```bash
   cd XhsAutoApp
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/你的用户名/XhsAutoApp.git
   git branch -M main
   git push -u origin main
   ```

4. **下载APK**
   - 访问仓库的 Actions 页面
   - 点击绿色的 ✓ 或黄色的 ● 构建任务
   - 滚动到底部 "Artifacts" 区域
   - 下载 `XhsAutoApp-debug.zip`
   - 解压得到 `app-debug.apk`

## 构建产物说明

| 文件名 | 说明 |
|--------|------|
| `XhsAutoApp-debug.zip` | 调试版APK，可直接安装 |
| `XhsAutoApp-release.zip` | 发布版APK，未签名 |

## 安装APK

1. 将APK传输到手机
2. 点击安装
3. 如提示"未知来源"，需在设置中允许安装

## 注意事项

- 首次构建可能需要3-5分钟
- 每次推送代码都会触发自动构建
- 调试版APK可直接安装使用
- 发布版APK需要签名才能安装

## 常见问题

**Q: 构建失败怎么办？**
A: 检查Actions页面的构建日志，查看具体错误信息

**Q: 如何更新代码？**
A: 修改代码后，执行 `git add . && git commit -m "更新" && git push`

**Q: APK在哪里下载？**
A: 在仓库的 Actions 页面 → 点击构建任务 → Artifacts 区域
