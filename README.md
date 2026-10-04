# Noor — Islamic Companion App

**Version 1.0.0**
**Package:** com.noor.islamicapp
**Language:** Java (native Android)
**Min SDK:** 24 (Android 7.0)
**Target SDK:** 34 (Android 14)

A complete Islamic companion app with Quran, Prayer Times, Qibla, Tasbih, 99 Names, Duas, and Hijri Calendar. All data embedded — works offline.

---

## Features

| Feature | Description |
|---------|-------------|
| Prayer Times | GPS-based accurate prayer times via Aladhan API. 5+ calculation methods. |
| Quran Reader | All 114 surahs offline. Arabic + English translation. Search included. |
| Qibla Compass | Shows Qibla direction. Fallback for devices without compass sensor. |
| Tasbih Counter | Digital dhikr counter with 6 presets, vibration, progress saving. |
| 99 Names of Allah | Asma ul-Husna with Arabic + transliteration + meaning. Search. |
| Duas & Azkar | 40+ authentic duas from Quran and Sunnah. Categorized + searchable. |
| Hijri Calendar | Islamic date + 12 months reference. |
| Dark Mode | Full dark mode support. |
| Adhan Notifications | Scheduled notifications for the 5 daily prayers. |
| Multi-feature Hub | One place to access all features. |

---

## Requirements

- Android Studio (Arctic Fox 2020.3.1+)
- JDK 17
- Android SDK Platform 34 + Build Tools 34.0.0
- Gradle 8.9 (wrapper included)

---

## How to Build

### Method 1: Android Studio

1. Extract the ZIP
2. Open Android Studio -> File -> Open -> select the noor-islamic-app folder
3. Wait for Gradle sync (5-10 min first time)
4. Click Run or Build -> Build APK(s)
5. Output: app/build/outputs/apk/debug/app-debug.apk

### Method 2: Command Line

    cd noor-islamic-app
    ./gradlew assembleDebug

Output: app/build/outputs/apk/debug/app-debug.apk

---

## Customization

### Change App Name
Edit app/src/main/res/values/strings.xml: <string name="app_name">Your Name</string>

### Change Package Name
Use Android Studio -> Right-click package -> Refactor -> Rename -> Rename Package

### Change Colors
Edit both files:
- app/src/main/res/values/colors.xml
- app/src/main/res/values-night/colors.xml

### Change App Icon
Replace: app/src/main/res/mipmap-*/ic_launcher.png
Or use Android Studio -> Image Asset

### Change Default Location
Edit HomeFragment.java: DEFAULT_LAT, DEFAULT_LON, DEFAULT_CITY

### Change Prayer Method
Edit HomeFragment.java: CALC_METHOD (1-5)

### Add Duas
Edit app/src/main/assets/duas.json - follow existing format

---

## Data Sources

| Data | Source | License |
|------|--------|---------|
| Quran text | Tanzil.net | Free with credit |
| Translation | Pickthall | Public Domain |
| Prayer times | Aladhan.com | Free API |
| 99 Names | Traditional | Public Domain |
| Duas | Quran & Sunnah | Public Domain |

---

## Support

Support provided for 6 months from purchase date. Use CodeCanyon comments.

---

## Changelog

v1.0.0 (2026-09-22) - Initial release
