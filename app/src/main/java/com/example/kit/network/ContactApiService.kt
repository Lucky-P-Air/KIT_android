package com.example.kit.data

import com.example.kit.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

private const val BASE_URL = "https://reminder-django-backend.herokuapp.com/api/" //contacts/

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ContactApiService {

    @GET("contacts/")
    suspend fun getContacts(@HeaderMap headers: Map<String, String>) : ContactListResponse

    @POST("contacts/create/")
    suspend fun postNewContact(
        @HeaderMap headers: Map<String, String>,
        @Body contactRequest: ContactRequest) : ContactRequest
}

object ContactApi {
    val retrofitService : ContactApiService by lazy { retrofit.create(ContactApiService::class.java) }
}