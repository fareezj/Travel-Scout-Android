package com.wolf.travelscout.util

import okhttp3.Interceptor
import okhttp3.Response


class HeaderInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Authorization", "Bearer " + SharedPreferencesUtil.accessToken!!)
            .build()
        return chain.proceed(request)
    }
}