# Technical Architecture (Single Source of Truth)

Bu doküman, Namaz Vakti Pro için ürün ve teknik mimarinin resmi referansıdır.

## 1) Platform Kararı

- Birincil platform: **Android native**
- Kod tabanı: Bu repo yalnızca Android geliştirmeyi kapsar.
- iOS: Ayrı faz olarak ele alınır; bu fazda iOS kodu veya cross-platform mimari hedeflenmez.

## 2) Mimari Yaklaşım

Uygulama Android tarafında aşağıdaki katmanlı yapıyı izler:

- **UI Katmanı**
  - Jetpack Compose ekranları
  - ViewModel tabanlı state yönetimi
- **Domain / İş Kuralları**
  - Namaz vakti, kıble ve takvim hesaplama/akış kuralları
- **Data Katmanı**
  - Repository
  - Network API istemcileri
  - Room veritabanı
  - DataStore tabanlı ayarlar

Destekleyici bileşenler:

- Hilt (Dependency Injection)
- WorkManager (periyodik güncelleme/arka plan işleri)
- Android bildirim altyapısı (alarm/hatırlatma)

## 3) Ürün Kapsamı (Android)

### Dahil

- Konum bazlı namaz vakitleri
- Takvim görünümü
- Kıble ekranı ve yön hesaplama
- Ayarlar ve kullanıcı tercihleri
- Bildirim/hatırlatma akışları

### Hariç (Bu Faz)

- iOS native uygulama geliştirme
- React Native veya diğer cross-platform geçiş çalışmaları

## 4) iOS Faz Planı (Ayrı Faz)

iOS için planlama, Android fazından bağımsız bir ürün/teknik değerlendirme ile başlatılacaktır.

Önerilen iOS faz çıktıları:

1. iOS gereksinim dokümanı (özellik paritesi + iOS-özel gereksinimler)
2. Teknoloji kararı (SwiftUI native vs olası paylaşımlı business katmanı)
3. Yol haritası, efor tahmini, ekip planı
4. Android ile API/veri sözleşmesi uyumluluk listesi

## 5) Tek Kaynak Politikası

- Ürün kapsamı ve mimari kararlar için birincil kaynaklar:
  1. `README.md`
  2. `TECHNICAL_ARCHITECTURE.md` (bu doküman)
- Çelişki durumunda bu doküman güncellenir ve README eşzamanlı hizalanır.

## 6) Değişiklik Kriteri

Aşağıdaki değişikliklerden biri olduğunda bu doküman güncellenmelidir:

- Platform stratejisi değişimi
- Mimari katman veya ana teknoloji değişimi
- Kapsama giren/çıkan büyük özellikler
- iOS fazının başlatılması veya kapsam güncellemesi
