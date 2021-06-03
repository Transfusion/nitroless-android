package io.github.transfusion.nitroless.network

import io.github.transfusion.nitroless.NitrolessApplication.Companion.applicationContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ServiceBuilder(endpoint: String) {
    private val client: OkHttpClient
    private val baseUrl: String

    init {
        val split = endpoint.split("?")
        var tmp = split[0]
        if (!tmp.endsWith("/")) tmp = "$tmp/"
        baseUrl = tmp

//        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR)
        val httpCacheDirectory = File(applicationContext().cacheDir, "responses")
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())
        val builder = OkHttpClient.Builder()
        builder.cache(cache)
        client = builder.build()
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