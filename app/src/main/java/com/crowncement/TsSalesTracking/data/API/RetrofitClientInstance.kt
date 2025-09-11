package com.crowncement.TsSalesTracking.data.API

import com.crowncement.TsSalesTracking.data.API.EndPoint.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientInstance {

    lateinit var retrofit: Retrofit

    val retrofitInstance: Retrofit
        get() {
   //         if (!this::retrofit.isInitialized) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val okHttpClient = OkHttpClient.Builder()
                    .readTimeout(180, TimeUnit.SECONDS)
                    .connectTimeout(180, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build()

//                val headersInterceptor = Interceptor { chain ->
//                    val requestBuilder = chain.request().newBuilder()
//                   // requestBuilder.header("Authorization", "Bearer $token")
//                    chain.proceed(requestBuilder.build())
//                }
//                val okHttpClient = OkHttpClient()
//                    .newBuilder()
//                    .followRedirects(true)
//                    .addInterceptor(headersInterceptor)
//                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
 //           }
            return retrofit
        }



    val upload_location_retrofitInstance: Retrofit
        get() {
            // if (!this::retrofit.isInitialized) {

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
            //    }
            return retrofit
        }


    val retrofitInstanceTS: Retrofit
        get() {
            // if (!this::retrofit.isInitialized) {

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(EndPoint.BASE_URL_TS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
            //    }
            return retrofit
        }


    val retrofitInstanceMetting: Retrofit
        get() {
            // if (!this::retrofit.isInitialized) {

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(EndPoint.BASE_URL_meeting)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
            //    }
            return retrofit
        }


}