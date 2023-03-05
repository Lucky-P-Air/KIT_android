package com.example.kit.data

import com.example.kit.model.*
import com.example.kit.network.ContactRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

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

    @GET("contacts/{id}/")
    suspend fun getSingleContact(
        @Path("id") contactID: String,
        @HeaderMap headers: Map<String, String>) : ContactResponse

    @POST("contacts/create/")
    suspend fun postNewContact(
        @HeaderMap headers: Map<String, String>,
        @Body contactRequest: ContactRequest) : ContactResponse

    @PUT("contacts/update/{id}/")
    suspend fun updateContact(
        @Path("id") contactID: String,
        @HeaderMap headers: Map<String, String>,
        @Body contactRequest: ContactRequest) : Response<ContactResponse>

    @DELETE("contacts/delete/{id}/")
    suspend fun deleteContact(
        @Path("id") contactID: String,
        @HeaderMap headers: Map<String, String>) : Response<Unit> //Server responds with 204 (null)

}

object ContactApi {
    val retrofitService : ContactApiService by lazy { retrofit.create(ContactApiService::class.java) }
}