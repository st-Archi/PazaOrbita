package com.plazaorbita.app.data.remote

import com.plazaorbita.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // BASE_URL viene de BuildConfig (definido en app/build.gradle.kts a partir de local.properties),
    // NUNCA hardcodeado aquí. Así compilas contra el backend en la nube sin tocar código,
    // y la URL no queda como texto plano fácil de encontrar al descompilar el APK.
    private val BASE_URL = BuildConfig.BASE_URL

    private val logging = HttpLoggingInterceptor().apply {
        // En release, no mandes el cuerpo de las peticiones al log (puede contener tokens/contraseñas).
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
