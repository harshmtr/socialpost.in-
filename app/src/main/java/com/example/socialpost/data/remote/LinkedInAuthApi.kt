package com.example.socialpost.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class LinkedInTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("scope") val scope: String? = null
)

interface LinkedInAuthApi {
    @FormUrlEncoded
    @POST("oauth/v2/accessToken")
    suspend fun exchangeAuthCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): LinkedInTokenResponse
}
