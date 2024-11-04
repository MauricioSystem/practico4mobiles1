package com.example.practico4mob

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {
    @GET("personas")
    suspend fun getContacts(): Response<List<Contact>>

    @POST("personas")
    suspend fun addContact(@Body contact: Contact): Response<Contact>

    @DELETE("personas/{id}")
    suspend fun deleteContact(@Path("id") contactId: Int): Response<Unit>

    @POST("phones")
    suspend fun addPhone(@Body phone: Phone): Response<Phone>

    @POST("emails")
    suspend fun addEmail(@Body email: Email): Response<Email>

    @GET("personas/{id}")
    suspend fun getContactById(@Path("id") contactId: Int): Response<Contact>

    @PUT("personas/{id}")
    suspend fun updateContact(@Path("id") contactId: Int, @Body contact: Contact): Response<Contact>

   /* @Multipart @POST("personas")
    suspend fun addContactWithImage(@Part("contact") contact: RequestBody, @Part image: MultipartBody.Part?): Response<Contact>

    @Multipart
    @PUT("personas/{id}")
    suspend fun updateContactWithImage(@Path("id") contactId: Int, @Part("contact") contact: RequestBody, @Part image: MultipartBody.Part?): Response<Contact>
*/
}
