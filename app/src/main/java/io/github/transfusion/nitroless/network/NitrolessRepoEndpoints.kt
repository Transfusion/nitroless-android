package io.github.transfusion.nitroless.network

import io.github.transfusion.nitroless.data.NitrolessRepoModel
import retrofit2.Call
import retrofit2.http.GET

interface NitrolessRepoEndpoints {
    @GET("index.json")
    suspend fun getIndex(): NitrolessRepoModel
}