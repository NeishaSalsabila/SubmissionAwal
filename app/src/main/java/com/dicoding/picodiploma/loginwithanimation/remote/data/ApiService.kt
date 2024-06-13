package com.dicoding.picodiploma.loginwithanimation.remote.data

import okhttp3.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @POST("logout")
    suspend fun logout(): Response<Unit>

    // Fungsi untuk mendapatkan daftar cerita
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): AddNewStoriesResponse

    class TokenInterceptor(private var token: String?) : Interceptor {
        fun updateToken(token: String?) {
            this.token = token
        }

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            token?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }
            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }
}
