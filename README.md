# Namaz Vakti Pro (Android Native)

Namaz Vakti Pro, **Android native (Kotlin + Jetpack Compose)** olarak geliştirilen bir namaz vakti uygulamasıdır. Ürün kapsamı Android-first yaklaşımıyla netleştirilmiştir.

## Ürün Kararı (Tek Kaynak)

- Platform stratejisi: **Android-first**
- Aktif geliştirme hedefi: **Sadece Android**
- iOS durumu: **Ayrı bir fazda planlanacak** (bu repo kapsamında aktif geliştirme yok)

Bu karar doğrultusunda uygulama gereksinimleri, mimari ve roadmap aşağıdaki teknik doküman ile hizalanmıştır:

- [`TECHNICAL_ARCHITECTURE.md`](TECHNICAL_ARCHITECTURE.md)

## Kapsam

### Mevcut (Android)

- Konum bazlı namaz vakitleri
- Takvim ekranı
- Kıble yönü desteği
- Ayarlar (tema, veri/uygulama tercihleri)
- Bildirim ve alarm altyapısı

### Kapsam Dışı (Bu Faz)

- iOS native uygulama
- Cross-platform (React Native / Flutter) geçişi

## Teknoloji Özeti

- Dil: Kotlin
- UI: Jetpack Compose
- Mimari: Android katmanlı yapı (UI / ViewModel / Repository / Data)
- DI: Hilt
- Arka plan işleri: WorkManager
- Kalıcı veri: Room + DataStore

## Roadmap (Üst Seviye)

1. Android uygulamasında iş kurallarının ve kullanıcı deneyiminin olgunlaştırılması
2. Android performans, test ve stabilite iyileştirmeleri
3. iOS fizibilitesi ve kapsamının ayrı faz dokümanında değerlendirilmesi

## Geliştirme

```bash
./gradlew assembleDebug
```

Gereksinim ve mimari detayları için teknik dokümana bakın:

- [`TECHNICAL_ARCHITECTURE.md`](TECHNICAL_ARCHITECTURE.md)
