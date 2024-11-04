package com.example.practico4mob

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practico4mob.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ContactoViewModel
    private lateinit var adapter: ContactAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el ViewModel
        viewModel = ViewModelProvider(this)[ContactoViewModel::class.java]

        // Inicializa el adaptador y configura el RecyclerView
        setupRecyclerView()

        // Observa la lista de contactos desde el ViewModel
        viewModel.contactos.observe(this) { contactos ->
            Log.d("MainActivity", "Lista de contactos observada: $contactos")
            adapter.setContacts(contactos)
        }

        // Carga los contactos inicialmente
        viewModel.fetchContacts()

        // Maneja el clic en el botón "Nuevo Contacto"
        binding.btnNuevoContacto.setOnClickListener {
            val intent = Intent(this, FormularioContactoActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupRecyclerView() {
        adapter = ContactAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Configura el listener para cuando se hace clic en un contacto
        adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onItemClick(contact: Contact) {
                showOptionsDialog(contact)
            }
        })
    }

    private fun showOptionsDialog(contact: Contact) {
        val options = arrayOf("Editar", "Eliminar")
        // Muestra un cuadro de diálogo con opciones
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editContact(contact)
                    1 -> confirmDelete(contact)
                }
            }
            .show()
    }

    private fun editContact(contact: Contact) {
        val intent = Intent(this, FormularioContactoActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id) // Pasar solo el ID del contacto
        }
        startActivity(intent)
    }


    private fun confirmDelete(contact: Contact) {
        // Muestra un diálogo de confirmación para eliminar
        AlertDialog.Builder(this)
            .setTitle("Eliminar ")
            .setMessage("elimino")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.deleteContact(contact.id)
            }
            .setNegativeButton("No", null)
            .show()
    }



}
