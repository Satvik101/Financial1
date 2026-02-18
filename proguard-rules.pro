# ── ProGuard / R8 rules for FinCalc ──────────────────────────

# ── Room entities (reflection for schema) ──
-keep class com.fincalc.app.data.local.db.entity.** { *; }

# ── Room generated code ──
-keep class * extends androidx.room.RoomDatabase { *; }

# ── MPAndroidChart ──
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# ── Gson (used for serialising inputs JSON in Room) ──
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.fincalc.app.domain.model.** { *; }

# ── Kotlin coroutines ──
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ── Google Play Billing ──
-keep class com.android.vending.billing.** { *; }

# ── AdMob ──
-keep class com.google.android.gms.ads.** { *; }
