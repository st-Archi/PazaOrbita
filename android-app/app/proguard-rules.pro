# Reglas para que R8 no rompa Retrofit/Gson al ofuscar.

# Gson usa reflexión: conserva los nombres de campo de tus data classes de modelo.
-keep class com.plazaorbita.app.data.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit / OkHttp
-keepattributes Exceptions
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson genérico (TypeToken, etc.)
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
