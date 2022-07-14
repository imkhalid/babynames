package com.kausTech.network

import com.kausTech.babynames.di.dashboard.InternetConnectionListener
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object{
        val currentInstance="http://192.168.0.112:8000/"
    }

    // API's response in logs
    private val mInternetConnectionLaistener: InternetConnectionListener? = null
    val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    var client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor { chain ->
            val request: Request

            request = chain.request().newBuilder().addHeader("distribution", "agent")
                .addHeader(
                    "Authorization","")
                .build()

            return@addInterceptor chain.proceed(request)
        }
        this.addInterceptor(interceptor)
        this.connectTimeout(30, TimeUnit.SECONDS)
        this.readTimeout(60, TimeUnit.SECONDS)

    }.build()

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(currentInstance)  // development server
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .client(client)
            .build()
    }


    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }
}