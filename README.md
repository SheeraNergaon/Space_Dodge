# 🚀 Space Dodge	

**Space Dodge** is an Android arcade game built in Kotlin, where you control a spaceship dodging asteroids and collecting stars! The game includes tilt sensor support, dynamic speed modes, background music, score tracking, GPS-based highscore location logging, and a beautiful Google Maps integration.

---

## 📱 Features

- 🎮 Classic grid-based movement with touch or tilt
- 🌟 Collect stars to earn points
- ☄️ Avoid asteroids or lose lives
- 📈 Live score and distance counter
- 🗺️ Save your score with GPS coordinates and view on Google Maps
- 🔊 Background music and sound effects
- 🎚️ Switch between Normal and Fast speed
- 📡 Toggle Sensor (Tilt) Mode
- 🏆 Highscore table with top 10 scores and map markers

---

## 📸 Screenshots
<img width="171" alt="Menu" src="https://github.com/user-attachments/assets/a0e884c6-5874-403c-b13c-7fb6c87fc7f1" />




<img width="192" alt="Game Screen" src="https://github.com/user-attachments/assets/da19124d-6020-432f-a88d-e257f19e8c36" />


---


## 🗺️ Map Integration

Google Maps is used to mark the location where a high score was achieved. The map zooms to that location when you click a score in the list.

To enable this:
1. [Generate a Google Maps API key](https://console.cloud.google.com/)
2. Replace the placeholder in `res/values/strings.xml`:
   ```xml
   <string name="google_maps_key">YOUR_API_KEY_HERE</string>
   ```

---

## 🔊 Sound & Music

- **SoundPool** is used for game effects.
- **MediaPlayer** is used for background music.


## 🚀 How to Run

1. Clone the repo.
2. Open in Android Studio.
3. Add your Google Maps API key.
4. Run on a device or emulator (location permissions required).
5. Play and dodge like a pro! 🕹️

---

## 📌 Permissions

Declared in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
```

---

## 🛠️ Built With

- Kotlin
- Android SDK
- Google Maps SDK
- SensorManager
- SharedPreferences
- Material Design Components

---

## 📫 Contact

Made with ❤️ by **Sheera**  
Feel free to fork, star, and contribute!
Email: Sheera.Nergaon1@gmail.com
