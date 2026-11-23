# Exp-Tracked

Track your expenses by scanning UPI QR codes or logging payments manually. Everything stays on your phone — no accounts, no cloud, no tracking.

## What it does

- Scan a UPI QR code → copies the VPA to clipboard → opens your UPI app
- Log expenses manually with amount, payee, category, and note
- Pending expenses are confirmed when you return to the app
- View spending trends, category breakdowns, and budget status
- Light and dark theme

## How a payment works

1. Tap the **+** button
2. Enter amount and payee (or scan a QR)
3. Tap **Pay & Log** — the expense is saved locally and your UPI app opens
4. Complete (or cancel) the payment in your UPI app
5. Come back to Exp-Tracked — you'll be asked to confirm or discard the logged expense

## Tech

- Kotlin + Jetpack Compose
- Material 3 with custom theme
- CameraX + ML Kit for QR scanning
- SharedPreferences for local storage
- No backend, no network calls

## Build

Requires Android Studio and JDK 11+. Min SDK 26, target SDK 37.

## Privacy

All data is stored locally on your device. The app does not send anything to any server. UPI payments are handled entirely by your chosen UPI app (PhonePe, GPay, Paytm, etc.) — this app only logs what you enter.

## License

MIT — see [LICENSE](LICENSE).
