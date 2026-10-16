# MAFIA AI Studio — Android source (v2)

Original Android AI-video-studio-inspired application. Open the project folder in Android Studio with JDK 17 and Android SDK 35, allow Gradle to sync, and run on an Android device (Android 8+).

## Implemented
- Dark UI with Home, Create, Library and Settings tabs
- Photo-to-video and text-to-video project setup
- Ten named effects/templates, styles, duration and aspect-ratio selectors
- Android system image/video pickers
- Prompt draft persistence and local project history
- Functional local photo contrast/saturation enhancement with JPEG save to Pictures/MAFIA AI Studio
- Opens an existing video in an installed editor when supported

## Not yet implemented
- Real AI video inference, cloud rendering, MP4 generation, payments, credits or subscriptions, in-app timeline video editor, or true AI photo upscaling

To implement AI generation, choose a licensed image/video model provider and build a backend that authenticates users, holds API secrets, uploads authorized media, submits jobs, polls for completion and returns generated video URLs. Never ship provider API secrets inside the app.

This is original code and branding, not a copy of Movify's proprietary code, assets or trademark.
