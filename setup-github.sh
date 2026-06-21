#!/bin/bash

# 小红书自动化App - GitHub构建脚本
# 使用此脚本初始化Git仓库并推送到GitHub，自动构建APK

set -e

echo "========================================="
echo "  小红书自动化App - GitHub构建设置"
echo "========================================="
echo ""

# 检查git
if ! command -v git &> /dev/null; then
    echo "错误: 未安装git"
    exit 1
fi

# 检查GitHub CLI
if ! command -v gh &> /dev/null; then
    echo "提示: 未安装GitHub CLI"
    echo "请先安装: brew install gh"
    echo "或者手动在GitHub网站创建仓库"
    echo ""
    read -p "是否继续? (y/n): " continue
    if [ "$continue" != "y" ]; then
        exit 1
    fi
fi

# 进入项目目录
cd "$(dirname "$0")"

# 初始化Git仓库
if [ ! -d ".git" ]; then
    echo "初始化Git仓库..."
    git init
    git add .
    git commit -m "Initial commit: XhsAutoApp"
fi

echo ""
echo "下一步操作："
echo ""
echo "方式一：使用GitHub CLI（推荐）"
echo "1. 运行: gh auth login（登录GitHub）"
echo "2. 运行: gh repo create XhsAutoApp --public --source=. --push"
echo ""
echo "方式二：手动操作"
echo "1. 在GitHub网站创建新仓库: https://github.com/new"
echo "2. 仓库名称: XhsAutoApp"
echo "3. 选择 Public"
echo "4. 不要初始化README（我们已经有了）"
echo "5. 运行以下命令："
echo "   git remote add origin https://github.com/你的用户名/XhsAutoApp.git"
echo "   git branch -M main"
echo "   git push -u origin main"
echo ""
echo "推送代码后，GitHub Actions会自动构建APK"
echo "构建完成后，在Actions页面下载APK"
echo ""
echo "========================================="
