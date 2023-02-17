package com.example.kit.data

import com.example.kit.model.Attributes
import com.example.kit.model.ContactResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

private const val BASE_URL = "https://reminder-django-backend.herokuapp.com/api/" //contacts/

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ContactApiService {

    @GET("contacts/")
    suspend fun getContacts(@HeaderMap headers: Map<String, String>) : ContactResponse

    @POST("contacts/create/")
    suspend fun postNewContact() : Attributes
}

object ContactApi {
    val retrofitService : ContactApiService by lazy { retrofit.create(ContactApiService::class.java) }
}