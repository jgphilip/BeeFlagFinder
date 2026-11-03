# :auth – reusable Firebase authentication library

Google, Meta (Facebook) and email/password sign-up/sign-in, with a ready-made Compose UI.

## Use in another project
1. Copy the `auth` folder, add `include(":auth")` to `settings.gradle.kts`, and `implementation(project(":auth"))` in your app.
2. Add `google-services.json` to your app and apply the `com.google.gms.google-services` plugin.
3. Facebook (optional): override these strings in your app's `strings.xml`:
   `facebook_app_id`, `facebook_client_token`, `fb_login_protocol_scheme` (`fb<APP_ID>`).
   Facebook is hidden while `facebook_app_id` is `0`.
4. In `Application`: `val authModule = AuthModule(this)`.
5. In `MainActivity`:
```kotlin
AuthGate(module = authModule, style = AuthStyle(...)) { user, signOut ->
    YourApp(user.email ?: user.displayName, signOut)
}
```
Without Firebase configured, `AuthGate` shows a "not set up" screen instead of crashing.

## Firebase Console setup
- Build → Authentication → Get started → **Sign-in method**.
- **Email/Password**: enable.
- **Google**: enable, choose a support email, save, then re-download `google-services.json`.
- **Facebook**: enable, paste Meta App ID + App secret, copy the OAuth redirect URI into Meta → Facebook Login → Settings.
- Project settings → Your apps: add debug/release SHA-1 and SHA-256 fingerprints.
