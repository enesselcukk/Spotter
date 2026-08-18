# Spotter

Spotter, yakındaki şarj istasyonları, otoparklar, oto yıkama ve benzeri noktaları keşfetmenizi sağlayan **Kotlin Multiplatform** mobil uygulamasıdır. Android ve iOS’ta aynı Compose arayüzünü paylaşır.

## Özellikler

- Kategori filtreleri: şarj, oto yıkama, otopark, yakıt, tamir
- Liste ve sütun görünümü
- Harita üzerinde rota önizleme ve adım adım navigasyon
- Favoriler ve arama
- Türkçe / İngilizce, açık / koyu tema
- OpenStreetMap verisi (Overpass API)

## Screenshots

### iOS

<p align="center">
  <img src="docs/screenshots/ios/01-home.png" alt="iOS Ana Sayfa" width="16%" />
  <img src="docs/screenshots/ios/02-map-route.png" alt="iOS Harita Rota" width="16%" />
  <img src="docs/screenshots/ios/03-navigation.png" alt="iOS Navigasyon" width="16%" />
</p>
<p align="center">
  <img src="docs/screenshots/ios/04-favorites.png" alt="iOS Favoriler" width="16%" />
  <img src="docs/screenshots/ios/05-settings.png" alt="iOS Ayarlar" width="16%" />
  <img src="docs/screenshots/ios/06-search.png" alt="iOS Arama" width="16%" />
</p>
<p align="center"><sub>Ana Sayfa · Harita · Navigasyon · Favoriler · Ayarlar · Arama</sub></p>

### Android

<p align="center">
  <img src="docs/screenshots/android/01-home.png" alt="Android Ana Sayfa" width="16%" />
  <img src="docs/screenshots/android/02-map-route.png" alt="Android Harita Rota" width="16%" />
  <img src="docs/screenshots/android/03-navigation.png" alt="Android Navigasyon" width="16%" />
</p>
<p align="center">
  <img src="docs/screenshots/android/04-favorites.png" alt="Android Favoriler" width="16%" />
  <img src="docs/screenshots/android/05-settings.png" alt="Android Ayarlar" width="16%" />
  <img src="docs/screenshots/android/06-search.png" alt="Android Arama" width="16%" />
</p>
<p align="center"><sub>Ana Sayfa · Harita · Navigasyon · Favoriler · Ayarlar · Arama</sub></p>

## Tech Stack

| Katman | Teknoloji |
|--------|-----------|
| UI | Compose Multiplatform, Material 3 |
| Mimari | Feature modülleri, Koin, ViewModel |
| Ağ | Ktor, Overpass API |
| Harita | OSMDroid (Android) |
| Kalıcılık | Room, Multiplatform Settings |
| Navigasyon | Navigation 3 |

## Proje Yapısı

```
Spotter/
├── composeApp/          # Android giriş noktası
├── iosApp/              # iOS giriş noktası (Xcode)
├── app/shared/          # Paylaşılan uygulama kabuğu
├── core/                # Ortak altyapı (network, ui, datastore, …)
└── feature/             # home, map, favorites, settings, splash, …
```

## Çalıştırma

**Android**

```bash
./gradlew :composeApp:assembleDebug
```

**iOS**

```bash
open iosApp/iosApp.xcodeproj
```

Xcode’dan hedef cihaz veya simülatör seçip Run.

## Gereksinimler

- JDK 17+
- Android Studio (Android için)
- Xcode 16+ (iOS için)
- Kotlin 2.4+

## Lisans

Bu proje kişisel / eğitim amaçlıdır.
