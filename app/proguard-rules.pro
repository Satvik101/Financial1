# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**
