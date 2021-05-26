package io.github.transfusion.nitroless.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceBuilder(endpoint: String) {
    private val client = OkHttpClient.Builder().build()
    private val baseUrl: String

    init {
        val split = endpoint.split("?")
        var tmp = split[0]
        if (!tmp.endsWith("/")) tmp = "$tmp/"
        baseUrl = tmp
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}