package com.dicoding.picodiploma.loginwithanimation.remote.data

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private var token: String?) : Interceptor {
    fun updateToken(token: String?) {
        this.token = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
