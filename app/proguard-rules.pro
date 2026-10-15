# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*

# Retain data models for Gson serialization
-keep class com.yalu.india.network.models.** { *; }
-keepclassmembers class com.yalu.india.network.models.** { *; }

# Retrofit & OkHttp
-dontwarn okio.**
-dontwarn retrofit2.**
