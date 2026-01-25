# Quantum IMS

<div align="center">
  <h3>Google Pixel 专用增强型 IMS 配置工具</h3>

  [![Android](https://img.shields.io/badge/Android-14%2B-green.svg)](https://www.android.com/)
  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
  [![Version](https://img.shields.io/badge/Version-3.1-brightgreen.svg)](https://github.com/Transwarpcom/QuantumIMS/releases)

  [English](README.md) | 简体中文
</div>

---

## 📱 简介

**Quantum IMS** 是原 [vvb2060 IMS 项目](https://github.com/vvb2060/Ims) 的增强分支版本，专为 Google Pixel 手机设计。通过系统级权限注入配置，强制开启 VoLTE、VoWiFi、VoNR 等高级功能，并针对国内网络环境进行了深度优化。

本版本采用了 **全新的 Material Design 3 界面**，内置 **高级配置编辑器**，并集成了针对中国运营商（电信/移动/联通/广电）的 **信号与 GPS 优化配置**。

<div align="center">
  <img src="screenshots/home.png" width="30%" alt="主界面"/>
  <img src="screenshots/editor.png" width="30%" alt="高级编辑器"/>
</div>

## ✨ Quantum IMS 新特性

### 🛠️ **高级配置编辑器**
- **完整权限：** 查看并修改设备上支持的 **所有** 运营商配置项（Carrier Config）。
- **搜索过滤：** 支持按关键词快速查找配置项。
- **自定义覆盖：** 支持修改布尔值、整数、字符串及数组，修改即时生效。
- **虚拟工具：** 提供一键 **解锁 APN 编辑**、**移除 QNS 切换限制** 等特殊功能。

### 🚀 **深度优化预设**
- **5G 网络：** 解锁 5G SA/NSA、VoNR，并针对国内频段（N41/N78/N79）进行优化。
- **信号优化：** 调整 QNS（服务质量）阈值，优化弱信号下的切换逻辑，防止过早回落 4G。
- **GPS 修复：** 强制使用博通（Broadcom）LTO 服务器，显著提升定位速度和精度。
- **流量优化：** 配置 "5G 不计费" 标识，优化后台流量策略。

### 🎨 **现代化体验**
- **Material Design 3：** 跟随系统色彩的自适应 UI 设计。
- **全语言支持：** 完美支持 简体中文、繁体中文 和 英文。
- **智能 SIM 识别：** 自动显示运营商名称（如 "中国联通"），告别单纯的 "SIM 1" 显示。

## 🎯 核心功能

### 📡 通话与连接
- ✅ **VoLTE** (4G 高清通话)
- ✅ **VoWiFi** (WiFi 通话)
- ✅ **VoNR** (5G 高清通话)
- ✅ **VT** (运营商视频通话)
- ✅ **跨 SIM 卡通话** (智能利用副卡流量通话)

### ⚡ 性能与信号
- ✅ **信号阈值优化** (更真实的信号格数显示)
- ✅ **5G SA 独立组网** (强制开启 SA)
- ✅ **TCP 缓冲区优化** (提升网络吞吐量)
- ✅ **GPS 搜星优化** (替换为高可用服务器)

### 🔧 高级工具
- ✅ **解锁 APN 编辑** (修改只读 APN)
- ✅ **短信重试机制** (提高短信发送成功率)
- ✅ **UT 接口支持** (支持通过菜单设置呼叫转移等)

## 🚀 安装方法

### 方法 1：下载 APK (推荐)
1. 从 [Releases](https://github.com/Transwarpcom/QuantumIMS/releases) 下载最新 APK。
2. 安装到您的 Pixel 手机。
3. 按照提示授予 Shizuku 权限。

### 方法 2：源码编译
```bash
git clone https://github.com/Transwarpcom/QuantumIMS.git
cd QuantumIMS
./gradlew assembleRelease
```

## 📖 使用指南

### 1. 配置 Shizuku
本应用需要 **Shizuku** 才能修改系统配置。
- 安装 [Shizuku](https://github.com/RikkaApps/Shizuku)。
- 通过 "无线调试" 或 "Root" 启动 Shizuku 服务。

### 2. 应用预设
- 打开 Quantum IMS。
- 选择需要配置的 SIM 卡。
- 点击 **"应用配置" (Apply Configuration)**。
- 等待几秒，应用会自动注入包含 VoLTE、5G 和信号优化的预设配置。

### 3. 高级编辑 (可选)
- 点击底部的 **"高级编辑器" (Advanced Editor)**。
- 搜索你想要修改的 Key (例如 `carrier_volte_available_bool`)。
- 点击修改并保存，配置将立即生效。

---

### ⚠️ 重要说明

**Android 16+ 限制：**
受 Google 安全补丁（2026.01.05+）影响，非 Root 环境下（仅使用 Shizuku）可能无法修改部分配置或导致闪退。
- **已 Root 用户：** 推荐配合 [Sui](https://github.com/Transwarpcom/Sui) 使用。
- **未 Root 用户：** 在最新的 Android 16 版本上可能功能受限。

**持久化问题：**
在 Android 16 QPR2 Beta 3 及更高版本上，运营商配置 **无法持久化**。重启手机后配置会丢失，需要重新打开 App 点击应用。

---

## 🤝 致谢

- **原项目：** [IMS by vvb2060](https://github.com/vvb2060/Ims)
- **Shizuku：** [RikkaApps](https://github.com/RikkaApps/Shizuku)
- **贡献者：** 感谢所有参与测试和提供配置建议的朋友。

## 📄 开源协议

Apache License 2.0
