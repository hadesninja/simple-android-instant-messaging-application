# 📱 Bluetooth Chat App (Android)

A simple Android application that allows **two devices** to discover each other via Bluetooth and exchange chat messages.  
Both devices must have Bluetooth enabled and the app installed. One device listens for incoming connections while the other initiates the connection, enabling **two-way chat** over a secure Bluetooth RFCOMM socket.

---

## 🚀 Features
- Scan for **nearby Bluetooth devices** (paired + discoverable).  
- Select a device to connect and start chatting.  
- Automatic **server + client mode**:
  - Each device listens for incoming connections.
  - If a user selects a device, it tries to connect as a client.
- **Real-time chat** with send/receive message support.  
- Works without internet – only Bluetooth is required.  

---

## 🛠️ Requirements
- Android Studio (latest recommended).  
- Android device with **Bluetooth support**.  
- Minimum SDK: **21 (Lollipop)**.  
- Target SDK: **34**.  

---

## 📦 Setup & Installation
1. Clone or download the repository:
   ```bash
   git clone https://github.com/yourusername/BluetoothChat.git
   ```
   Or unzip the provided project.

2. Open the project in **Android Studio**.

3. Let Gradle sync and install dependencies automatically.

4. Connect an Android device (or use two real devices for testing).  
   > ⚠️ Bluetooth does not work on most emulators, so use physical devices.

5. Run the app on **both devices**:
   - One device just stays open (server mode).  
   - The other device taps **"Scan Devices"**, selects the first one, and starts chatting.  

---

## 📱 Usage
1. Enable **Bluetooth** on both devices.  
2. Launch the app → it will show paired devices.  
3. Tap **Scan Devices** to find nearby ones.  
4. Select a device to connect.  
5. Start typing and sending messages.  

---

## 🔒 Permissions Used
- `BLUETOOTH` / `BLUETOOTH_ADMIN` (for pre-Android 12).  
- `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE` (for Android 12+).  
- `ACCESS_FINE_LOCATION` (required for device discovery in Android 6+).  

---

## ⚡ Project Structure
```
BluetoothChat/
 ├── app/
 │   ├── src/main/java/com/example/bluetoothchat/
 │   │   ├── MainActivity.java     # Device discovery & selection
 │   │   ├── ChatActivity.java     # Chat logic (server + client)
 │   ├── src/main/res/layout/
 │   │   ├── activity_main.xml     # UI for scanning devices
 │   │   ├── activity_chat.xml     # UI for chat screen
 │   ├── src/main/AndroidManifest.xml
 │   └── build.gradle
 ├── build.gradle
 └── settings.gradle
```

---

## 📷 Screenshots (Sample UI)
- **Main Screen:** Scan and select devices  
- **Chat Screen:** Send and receive messages  

*(You can capture actual screenshots from your build.)*

---

## ⚠️ Limitations
- Both devices must be **paired in system Bluetooth settings** before connecting.  
- Only supports **one-to-one chat** (not group chat).  
- Works best on Android 6.0 (API 23) and above.  

---

## 📌 Future Improvements
- Background service to keep chat alive after closing the app.  
- Notifications for incoming messages.  
- Group chat over Bluetooth.  
- UI enhancements with Material Design.  

---

## 📄 License
This project is open-source. You can freely use, modify, and distribute it for educational or personal purposes.  
