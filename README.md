# BuySmartAI — Your Intelligent Shopping Companion 🛍️✨

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![Design](https://img.shields.io/badge/Design-Material_3-blue.svg)](https://m3.material.io)
[![Backend](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com)
[![AI](https://img.shields.io/badge/AI-ML_Kit-blueviolet.svg)](https://developers.google.com/ml-kit)

**BuySmartAI** is not just a shopping app—it's a world-class AI assistant designed to eliminate the friction of modern e-commerce. Built with a focus on minimalism, speed, and intelligence, it empowers users to save money and time through advanced automation.

---

## 🚀 High-Impact "Recruiter-Ready" Features

### 🔍 1. Zero-Input Shopping (Smart Scan)
The **Impact Killer**. Stop searching and start saving. Users can upload a screenshot of their cart from Amazon or Flipkart. Our **AI Vision Engine (ML Kit OCR)** instantly identifies the items and calculates the lowest possible total across the entire web.

### 💳 2. Multi-Store Unified Checkout (The Proxy Cart)
The ultimate "Super App" experience. Why jump between 5 apps? BuySmartAI provides a **Unified Proxy Checkout**. View a consolidated breakdown of items from Amazon, Zepto, and Myntra, and pay once. We handle the logistics in the background.

### 🌱 3. Sustainability Score (The Green Badge)
For the eco-conscious shopper. Every local delivery (SwiftDash) calculates a **CO2 Savings Score**. If your items are coming via bicycle or EV, we award a **Green Badge**, showing exactly how many grams of carbon you saved by choosing a smarter delivery route.

### 📈 4. AI Price Prediction & Dashboards
Never wonder "Is this a good time to buy?" again. Our **AI Intelligence Engine** analyzes 30-day price trends using **MPAndroidChart** and provides a confidence score.
*   **Buy Now**: Price is at a historical low.
*   **Wait**: AI predicts a drop within 48 hours.

---

## 🎨 Visual Language
Inspired by **Apple's elegance** and **Nothing OS's minimalism**:
*   **Bento Box Grid**: A structured, high-readability dashboard.
*   **Glassmorphism**: Semi-transparent UI layers for a futuristic, light feel.
*   **Dynamic Motion**: Physics-based "Lift-on-Touch" interactions.
*   **Edge-to-Edge**: Fully optimized for Android 15/16 notched displays.

---

## 🛠️ Tech Stack
*   **Language**: Java (Modern Android SDK 35)
*   **Architecture**: Layered Modular Architecture (Presentation, Logic, Data)
*   **Database/Auth**: Google Firebase (Firestore, Auth, Storage)
*   **Vision**: Google ML Kit (Text Recognition)
*   **Networking**: Retrofit 2 & OkHttp 3
*   **UI/Motion**: Material 3, ViewBinding, Lottie, Staggered Grids

---

## ⚙️ Setup & Installation

1. **Clone the Project**:
   ```bash
   git clone https://github.com/yourusername/BuySmartAI.git
   ```

2. **Firebase Configuration**:
   *   Create a project in the [Firebase Console](https://console.firebase.google.com/).
   *   Enable **Email/Google Authentication** and **Cloud Firestore**.
   *   Download `google-services.json` and place it in the `app/` directory.

3. **API Keys**:
   *   Add your **Google Maps API Key** to `strings.xml`.

4. **Build**:
   *   Sync Gradle and run on an Android 13+ device for the best experience.

---

## 📝 Pre-Commit Checklist (Before you Push!)

Before committing your final code, ensure:
- [ ] **Secrets Removed**: Ensure no private API keys are hardcoded.
- [ ] **google-services.json**: This should be in your `.gitignore` if the repo is public.
- [ ] **Linter**: Run `Analyze > Inspect Code` to remove unused imports.
- [ ] **Firebase Rules**: Ensure your Firestore rules are set to `allow read, write: if request.auth != null;`.

---

## 👨‍💻 Author
**[Your Name]** - Principal Developer & Designer
*Experience building future-ready AI products.*

---
*Developed as a showcase of premium Android Engineering in 2024-2026.*
