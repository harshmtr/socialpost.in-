## 📱 App Screenshots

<p align="center">
  <img src="screenshots/post_creator.png" width="28%"/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="screenshots/news_screen.png" width="28%"/>
</p>



# SocialPost AI: Professional Tech Post Engine 🚀

**SocialPost AI** is a professional-grade Android application designed for tech leaders, developers, and advocates. It streamlines the entire process of tech research and content creation by transforming raw technology news into high-performing, viral-ready LinkedIn posts powered by **Google Gemini AI**.

---

## 🌟 Key Features

### 1. Automated Tech Research Feed
Stay ahead of the curve with a real-time research feed powered by the **Currents API**. Discover trending technology headlines across categories like AI & ML, Mobile Tech, and Cloud & DevOps.
- **Live Fetching**: Pulls global tech news instantly.
- **Topic Search**: Search for specific keywords to find the perfect context for your next post.

### 2. AI Post Drafting Engine
Turn any article into a compelling narrative with a single tap.
- **Customizable Styles**: Choose from "Bold Statement", "Thought-Provoking Question", or "Data Takeaway".
- **Dynamic Tones**: Align your post with your personal brand using "Professional", "Conversational", or "Thought Leadership" tones.
- **Viral Optimization**: Built-in metrics track word count, hashtag density, and readability to ensure your post is "Viral Ready".

### 3. Multi-Style AI Visuals
Posts with images get more engagement. SocialPost AI provides a suite of visual options for every post:
- **AI Generation**: Generate 5+ distinct visual styles (Futuristic 3D, Minimalist Vector, Cyberpunk Abstract, etc.) via Pollinations AI.
- **Web Referrals**: High-quality tech referrals based on article keywords.
- **Visual Picker**: A smooth selection UI with real-time loading indicators.

### 4. Robust LinkedIn Sharing
Publishing is seamless and reliable.
- **Clipboard Sync**: The app automatically copies your post text to the clipboard right before sharing—no more losing your captions if an external app ignores intent text.
- **Rich Intent Sharing**: Uses `FileProvider` to securely attach generated images to the LinkedIn share sheet.

---

## 🛠 Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Asynchronous Logic**: Kotlin Coroutines & Flow
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture patterns
- **AI Integration**: Google Gemini AI (via Retrofit)
- **Networking**: Retrofit 2 & OkHttp 4
- **Persistence**: Room Database (for saving drafts and posts)
- **Image Loading**: Coil
- **Dependency Management**: Gradle Version Catalog (libs.versions.toml)

---

## 📁 Project Structure

```text
com.example.socialpost
├── data
│   ├── local      # Room DB, DAOs, and Entities
│   ├── model      # Data classes for Articles and Posts
│   ├── remote     # API interfaces (Currents, Gemini, LinkedIn)
│   └── repository # Business logic and data orchestration
├── ui
│   ├── components # Reusable Compose widgets (NewsCard, QualityMeter)
│   ├── screens    # Feature screens (Research, Draft, Saved, Settings)
│   └── theme      # Material 3 color schemes and typography
└── util           # Shared utilities (SharingUtils, FileProvider)
```

---

## 🚀 Setup & Installation

### 1. Prerequisites
- **Android Studio Ladybug** or newer.
- **JDK 17** or higher.
- A physical device or emulator running **API 26+**.

### 2. Configuration
The app uses `local.properties` to store sensitive API keys. In your root project directory, ensure your `local.properties` file contains:

```properties
# Google Gemini API Key (Get one at aistudio.google.com)
GOOGLE_GEMINI_API_KEY=your_gemini_api_key_here

# Currents API Key (Get one at currentsapi.services)
NEWS_API_KEY=your_currents_api_key_here
```
🛠️ Developer & Editing Guide
If you want to modify, customize, or contribute to this project, here is how you can set it up, edit the code, and run it locally:

1. Prerequisites
Android Studio (Koala or latest recommended version installed with Android SDK).

Kotlin & Jetpack Compose knowledge for UI modifications.

2. Setting Up Local Environment
Because sensitive files (like API keys) are excluded from version control for security, you must set up your local configuration before running the app:

Clone the repository:

Bash
git clone https://github.com/harshmtr/socialpost.in-.git
Open the project in Android Studio.

Create a local.properties file in the root directory of the project (if it doesn't already exist).

Add your Gemini API key inside local.properties:

Properties
GOOGLE_GEMINI_API_KEY=your_actual_api_key_here
3. Key Project Architecture & Where to Edit
UI & Screens (app/src/main/java/.../ui/):

Contains Jetpack Compose screens like PostCreatorScreen.kt, NewsScreen.kt, and SettingsScreen.kt. Edit these files to change layouts, buttons, or add new UI components.

State Management (app/src/main/java/.../viewmodel/):

ViewModels handle app logic, data states, and API calls (e.g., image generation state, loading indicators).

Utilities & Sharing (app/src/main/java/.../utils/):

Handles external intents, clipboard syncing for LinkedIn sharing, and network helpers.

Strings & Localization (app/src/main/res/values/strings.xml):

All static UI text, labels, and descriptions are externalized here for clean maintenance and accessibility.

4. Building and Running
Sync your Gradle files in Android Studio (File > Sync Project with Gradle Files).

Connect a physical Android device (with USB debugging enabled) or start an Android Emulator.

Click the Run ('▶') button in Android Studio to build and launch the app.
### 3. Build & Run
1. Clone the repository.
2. Open the project in Android Studio.
3. **Sync Project with Gradle Files**.
4. Run the **`app`** configuration on your device/emulator.

---

## 📖 Usage Guide

1. **Research**: Browse the **Tech Research Feed** and tap **"Draft Post with AI"** on an article that interests you.
2. **Draft**: Configure your hook style and tone. Edit the AI-generated text as needed.
3. **Visualize**: Tap **"New Image"** to open the style picker. Choose the visual that fits your brand.
4. **Publish**: Tap **"Publish Post"**. The text is copied to your clipboard automatically. Select **LinkedIn** from the share sheet and **Paste** your thoughts into the new post!

---
## api Installation
GOOGLE_GEMINI_API_KEY =
sdk.dir=
IMAGE_PROVIDER=gemini ** if u useing GOOGLE_GEMINI_API_KEY
LINKEDIN_ACCESS_TOKEN=
LINKEDIN_CLIENT_ID=
LINKEDIN_CLIENT_ID=78b8uu6t7zw4an
LINKEDIN_CLIENT_SECRET=
MAX_RETRIES=3
NEWS_API_KEY= ** u can use anythind but i use currents api for it
POST_BACKGROUND_COLOR=#0A66C2
