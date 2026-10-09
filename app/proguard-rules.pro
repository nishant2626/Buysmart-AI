# ProGuard rules for BuySmart AI

# Keep model classes
-keep class com.buysmart.ai.models.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Keep Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Keep MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Keep Lottie
-keep class com.airbnb.lottie.** { *; }
