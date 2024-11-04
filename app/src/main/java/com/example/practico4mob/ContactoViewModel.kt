package com.example.practico4mob

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ContactoViewModel : ViewModel() {
    private val _contactos = MutableLiveData<List<Contact>>().apply { value = listOf() }
    val contactos: LiveData<List<Contact>> get() = _contactos

    private val apiService = RetrofitInstance.api

    fun fetchContacts() {
        viewModelScope.launch {
            try {
                val response = apiService.getContacts()
                if (response.isSuccessful) {
                    response.body()?.let { contacts ->
                        _contactos.value = contacts
                        Log.d("ContactoViewModel", "Contactos obtenidos: $contacts")
                    } ?: Log.e("ContactoViewModel", "Error: la respuesta de contactos es nula")
                } else {
                    Log.e("ContactoViewModel", "Error en la obtención de contactos: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Error en la obtención de contactos", e)
            }
        }
    }

    fun deleteContact(contactId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteContact(contactId)
                if (response.isSuccessful) {
                    Log.d("ContactoViewModel", "Contacto eliminado exitosamente")
                    fetchContacts() // Refresca la lista de contactos
                } else {
                    Log.e("ContactoViewModel", "Error al eliminar contacto: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Excepción al eliminar contacto", e)
            }
        }
    }

    fun agregarContacto(contact: Contact): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        viewModelScope.launch {
            try {
                // Verifica si es una actualización (si el ID es diferente de 0)
                val response = if (contact.id != 0) {
                    Log.d("ContactoViewModel", "Editando contacto existente: ${contact.id}")
                    apiService.updateContact(contact.id, contact)
                } else {
                    Log.d("ContactoViewModel", "Creando nuevo contacto")
                    apiService.addContact(contact)
                }

                if (response.isSuccessful) {
                    response.body()?.let { updatedContact ->
                        Log.d("ContactoViewModel", "Contacto procesado con éxito: $updatedContact")
                        result.postValue(true)
                        fetchContacts() // Refresca la lista de contactos
                    } ?: run {
                        Log.e("ContactoViewModel", "Error: respuesta de la API es nula")
                        result.postValue(false)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ContactoViewModel", "Error en la respuesta de la API: $errorBody")
                    result.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Excepción al procesar contacto", e)
                result.postValue(false)
            }
        }

        return result
    }



    private fun agregarTelefonosYEmails(contactId: Int, phones: List<Phone>, emails: List<Email>) {
        viewModelScope.launch {
            phones.forEach { phone ->
                // Intercambia number y label
                val phoneRequest = Phone(id = 0, number = phone.label, persona_id = contactId, label = phone.number) // label como number
                try {
                    val response = apiService.addPhone(phoneRequest) // Enviar el objeto completo
                    if (response.isSuccessful) {
                        Log.d("ContactoViewModel", "Teléfono añadido con éxito: ${phone.label}")
                    } else {
                        Log.e("ContactoViewModel", "Error al agregar teléfono: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("ContactoViewModel", "Fallo al agregar teléfono", e)
                }
            }

            emails.forEach { email ->
                // Intercambia email y label
                val emailRequest = Email(id = 0, email = email.label, persona_id = contactId, label = email.email) // label como email
                try {
                    val response = apiService.addEmail(emailRequest) // Enviar el objeto completo
                    if (response.isSuccessful) {
                        Log.d("ContactoViewModel", "Email añadido con éxito: ${email.label}")
                    } else {
                        Log.e("ContactoViewModel", "Error al agregar email: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("ContactoViewModel", "Fallo al agregar email", e)
                }
            }
            
        }
    }
    fun getContactById(contactId: Int): LiveData<Contact?> {
        val contactData = MutableLiveData<Contact?>()
        viewModelScope.launch {
            try {
                val response = apiService.getContactById(contactId)
                if (response.isSuccessful) {
                    contactData.value = response.body()
                } else {
                    Log.e("ContactoViewModel", "Error al obtener contacto: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Excepción al obtener contacto", e)
            }
        }
        return contactData
    }

}
