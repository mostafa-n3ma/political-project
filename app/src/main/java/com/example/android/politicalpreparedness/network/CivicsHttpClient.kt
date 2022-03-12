package com.example.android.politicalpreparedness.network

import okhttp3.OkHttpClient

class CivicsHttpClient: OkHttpClient() {

    companion object {

//        const val API_KEY = "Api Key" //: Place your API Key Here
const val API_KEY ="AIzaSyBI0Dsi167CwRwMA9au-xsdBGmGtJbIo2U"

        fun getClient(): OkHttpClient {
            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url()
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                                .url(url)
                                .build()
                        chain.proceed(request)
                    }
                    .build()
        }

    }

}