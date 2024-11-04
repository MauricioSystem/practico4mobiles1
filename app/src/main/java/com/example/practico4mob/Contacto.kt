package com.example.practico4mob

data class Phone(
    val id: Int,
    var persona_id: Int,
    val label: String,
    val number: String
)
data class Email(
    val id: Int,
    var persona_id: Int,
    val label: String,
    val email: String
)
data class Contact(
    val id: Int,
    val name: String,
    val last_name: String,
    val company: String,
    val address: String,
    val city: String,
    val state: String,
    val profile_picture: String,
    val phones: List<Phone>,
    val emails: List<Email>
)

data class ResponseType(
    val id: Int,
    val success: Boolean,
    val message: String,
    val data: Contact? // Puedes incluir más campos según tu API
)

