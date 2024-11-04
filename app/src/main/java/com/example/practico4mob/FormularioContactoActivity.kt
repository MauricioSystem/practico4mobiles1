package com.example.practico4mob

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.practico4mob.databinding.ActivityFormularioContactoBinding

class FormularioContactoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioContactoBinding
    private val viewModel: ContactoViewModel by viewModels()
    private var imageUri: Uri? = null
    private var contactToEdit: Contact? = null  // Contacto a editar, si se recibe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Revisar si se recibió un ID de contacto para editar
        val contactId = intent.getIntExtra("CONTACT_ID", -1)
        if (contactId != -1) {
            fetchContactData(contactId)
        }

        setupSpinners()

        binding.btnSeleccionarImagen.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnGuardar.setOnClickListener {
            guardarContacto()
        }
    }

    // Método para hacer una solicitud a la API y obtener el contacto por ID
    private fun fetchContactData(contactId: Int) {
        viewModel.getContactById(contactId).observe(this, Observer { contact ->
            contact?.let {
                contactToEdit = it
                populateContactForm(it)
                Log.d("FormularioContactoActivity", "Datos del contacto cargados: $it")
            }
        })
    }

    private fun populateContactForm(contact: Contact) {
        binding.etNombre.setText(contact.name)
        binding.etApellido.setText(contact.last_name)
        binding.etCompania.setText(contact.company)
        binding.etDireccion.setText(contact.address)
        binding.etCiudad.setText(contact.city)
        binding.etEstado.setText(contact.state)

        if (contact.phones.isNotEmpty()) {
            val phone = contact.phones[0]
            binding.etTelefono.setText(phone.number)
            setSpinnerSelection(binding.spinnerEtiquetaTelefono, phone.label)
        }

        if (contact.emails.isNotEmpty()) {
            val email = contact.emails[0]
            binding.etEmail.setText(email.email)
            setSpinnerSelection(binding.spinnerEtiquetaEmail, email.label)
        }

        contact.profile_picture?.let {
            imageUri = Uri.parse(it)
            binding.imageViewProfile.setImageURI(imageUri)
        }
    }

    private fun setSpinnerSelection(spinner: AdapterView<*>, value: String) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val position = adapter.getPosition(value)
        spinner.setSelection(position)
    }

    private fun setupSpinners() {
        val etiquetasTelefono = listOf("Casa", "Trabajo", "Celular")
        val etiquetasEmail = listOf("Persona", "Trabajo", "Universidad")

        val adapterTelefono = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetasTelefono)
        val adapterEmail = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetasEmail)

        adapterTelefono.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerEtiquetaTelefono.adapter = adapterTelefono
        binding.spinnerEtiquetaEmail.adapter = adapterEmail

        binding.spinnerEtiquetaTelefono.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Log.d("FormularioContactoActivity", "Etiqueta teléfono seleccionada: ${etiquetasTelefono[position]}")
                if (position != 0) {
                    binding.etTelefono.setText("") // Limpia el campo de teléfono
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerEtiquetaEmail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Log.d("FormularioContactoActivity", "Etiqueta email seleccionada: ${etiquetasEmail[position]}")
                if (position != 0) {
                    binding.etEmail.setText("") // Limpia el campo de email
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            binding.imageViewProfile.setImageURI(imageUri)
            Log.d("FormularioContactoActivity", "Imagen seleccionada: $imageUri")
        }
    }

    private fun guardarContacto() {
        val nombre = binding.etNombre.text.toString()
        val apellido = binding.etApellido.text.toString()
        val compania = binding.etCompania.text.toString()
        val direccion = binding.etDireccion.text.toString()
        val ciudad = binding.etCiudad.text.toString()
        val estado = binding.etEstado.text.toString()

        val listaTelefonos = mutableListOf<Phone>()
        val listaEmails = mutableListOf<Email>()

        val telefono = binding.etTelefono.text.toString()
        val etiquetaTelefono = binding.spinnerEtiquetaTelefono.selectedItem.toString()
        val email = binding.etEmail.text.toString()
        val etiquetaEmail = binding.spinnerEtiquetaEmail.selectedItem.toString()

        if (telefono.isNotBlank()) {
            listaTelefonos.add(Phone(0, 0, telefono, etiquetaTelefono))
        }

        if (email.isNotBlank()) {
            listaEmails.add(Email(0, 0, email, etiquetaEmail))
        }

        val urlImagen = imageUri?.toString() ?: ""

        val contact = Contact(
            id = contactToEdit?.id ?: 0,
            name = nombre,
            last_name = apellido,
            company = compania,
            address = direccion,
            city = ciudad,
            state = estado,
            profile_picture = urlImagen,
            phones = listaTelefonos,
            emails = listaEmails
        )

        Log.d("FormularioContactoActivity", "Guardando contacto: $contact")

        viewModel.agregarContacto(contact).observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Contacto guardado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar contacto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



