# Build MAFIA AI Studio APK using only your phone

1. Sign in to GitHub in Chrome and create a **private** repository named `MafiaAIStudio`.
2. Upload the **contents** of this project folder to the repository (including `.github/workflows/android-apk.yml`). GitHub's web uploader can upload multiple files, but preserving folders and hidden `.github` paths on mobile may be awkward; a Git-capable mobile app is another option.
3. In your repository, open **Actions** > **Build Android APK** > **Run workflow**. The workflow also starts when code is pushed to `main` or `master`.
4. When the workflow succeeds, open the completed run and download `MAFIA-AI-Studio-debug-APK` from **Artifacts**. Extract the ZIP to get `app-debug.apk`.
5. Open `app-debug.apk` on your phone. Android may ask you to allow installs from the browser or Files app. Only install an APK built from your own trusted repository.

This is a debug APK for personal testing, not a production release. Android might show a security warning. No AI generation service is connected, so the Generate button saves a draft, not a video.
