# Quantum IMS

<div align="center">
  <h3>Enhanced IMS Configuration Tool for Google Pixel Devices</h3>

  [![Android](https://img.shields.io/badge/Android-14%2B-green.svg)](https://www.android.com/)
  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
  [![Version](https://img.shields.io/badge/Version-3.1-brightgreen.svg)](https://github.com/Transwarpcom/QuantumIMS/releases)

  [简体中文](README_CN.md) | English
</div>

---

## 📱 About

**Quantum IMS** is an enhanced fork of the original [IMS project by vvb2060](https://github.com/vvb2060/Ims), designed to enable VoLTE, VoWiFi, VoNR, and other advanced IMS features on Google Pixel phones through privileged system configuration.

This enhanced version includes a **Modern Material Design UI**, **Advanced Configuration Editor**, and **Specific Carrier Optimizations** (Signal/GPS/5G), making it the ultimate tool for Pixel connectivity.

<div align="center">
  <img src="screenshots/home.png" width="30%" alt="Home Screen"/>
  <img src="screenshots/editor.png" width="30%" alt="Advanced Editor"/>
</div>

## ✨ New Features in Quantum IMS

### 🛠️ **Advanced Configuration Editor**
- **Full Access:** View and modify **ALL** carrier configuration keys available on the device.
- **Search & Filter:** Easily find specific keys by name.
- **Override System:** Apply custom values (Booleans, Integers, Strings, Arrays) that persist across reboots (on supported Android versions).
- **Virtual Tools:** Special toggles to remove restrictions (e.g., **Unlock APN Editing**, **Remove QNS Handover Restrictions**).

### 🚀 **Optimized Presets**
- **5G/Network:** Unlocks 5G SA/NSA, VoNR, and specific bands (N41/N78/N79).
- **Signal Optimization:** Tuned QNS (Quality of Service) thresholds for better signal handover and 5G retention.
- **GPS Fix:** Forces Broadcom PSDS servers for faster and more accurate location lock.
- **Traffic Optimization:** Configures "Unmetered 5G" flags to prevent data counting on specific NSA/SA carriers.

### 🎨 **Modern Experience**
- **Material Design 3:** Beautiful, adaptive UI that follows system themes.
- **Automatic Localization:** Full English and Chinese (Simplified/Traditional) support.
- **Smart SIM Handling:** Automatically detects and displays Carrier Names for easier multi-SIM management.

## 🎯 Core Features

### 📡 Connectivity & Voice
- ✅ **VoLTE** (Voice over LTE) - HD Voice calls
- ✅ **VoWiFi** (WiFi Calling) - Call over WiFi
- ✅ **VoNR** (Voice over New Radio) - Native 5G Voice
- ✅ **VT** (Video Calling) - Carrier Video Calls
- ✅ **Cross-SIM Calling** - Use data from one SIM to handle calls on the other

### ⚡ Performance & Signal
- ✅ **Signal Thresholds** - Optimized bar display and handover logic
- ✅ **5G Standalone (SA)** - Force enable SA mode
- ✅ **TCP Buffers** - Optimized network buffer sizes for higher speeds
- ✅ **GPS Optimization** - Switched to reliable LTO servers

### 🔧 Advanced Tools
- ✅ **Unlock APN** - Edit read-only APN settings
- ✅ **SMS Retry** - Increased retry count for reliability
- ✅ **UT Interface** - Enable Supplementary Services over UT

## 🚀 Installation

### Method 1: Download APK (Recommended)
1. Download the latest APK from [Releases](https://github.com/Transwarpcom/QuantumIMS/releases).
2. Install the APK on your Pixel device.
3. Grant necessary permissions (Shizuku).

### Method 2: Build from Source
```bash
git clone https://github.com/Transwarpcom/QuantumIMS.git
cd QuantumIMS
./gradlew assembleRelease
```

## 📖 Usage Guide

### 1. Setup Shizuku
This app requires **Shizuku** to perform privileged system operations.
- Install [Shizuku](https://github.com/RikkaApps/Shizuku).
- Start Shizuku via Wireless Debugging or Root.

### 2. Apply Presets
- Open Quantum IMS.
- Select your SIM card.
- Click **"Apply Configuration"** to apply the optimized preset package.
- This enables VoLTE, 5G, and Signal optimizations automatically.

### 3. Advanced Editing (Optional)
- Click **"Advanced Editor"** at the bottom.
- Search for any key (e.g., `carrier_volte_available_bool`).
- Tap to edit the value.
- Click "Save" to apply immediately.

---

### ⚠️ Important Notes

**Android 16+ Limitations:**
Due to Google's security patches (2026.01.05+), modifying carrier configs via non-root Shell (Shizuku) is restricted.
- **Rooted Users:** Use [Sui](https://github.com/Transwarpcom/Sui) instead of Shizuku.
- **Non-Rooted Users:** Functionality may be limited or cause crashes on latest Android 16 builds.

**Persistence:**
On Android 16 QPR2 Beta 3+, configurations are **NOT persistent** across reboots. You must re-apply them after restarting your phone.

---

## 🤝 Credits

- **Original Project:** [IMS by vvb2060](https://github.com/vvb2060/Ims)
- **Shizuku:** [RikkaApps](https://github.com/RikkaApps/Shizuku)
- **Contributors:** All who helped test and improve the presets.

## 📄 License

Apache License 2.0
