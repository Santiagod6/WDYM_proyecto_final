package com.example.proyecto_final

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.proyecto_final.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var editTextList: MutableList<EditText>
    private var ultimoEditTextCreado: EditText? = null
    private var enFavoritos = false
    private val favoritosList: MutableList<String> = mutableListOf()
    private lateinit var imageViewFavorito: ImageView
    private var textViewCreado = false
    private val LANGUAGE_PREF_KEY = "language_pref_key"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editTextList = mutableListOf()

        binding.btnGenerarEditText.setOnClickListener {

            // Verifica si ya hay un TextView creado
            if (!textViewCreado) {
                cambioColor(binding.container2, "#FFCBBF")
//            val TextViewFecha = EditText(this)
//            TextViewFecha.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            val fechaActual = obtenerFechaActual()
//            TextViewFecha.setText("Fecha: "+fechaActual)

                // Crea un nuevo EditText
                val nuevoEditText = EditText(this)
                nuevoEditText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                nuevoEditText.hint = getString(R.string.hintIdeas)

                // Asigna un ID único al EditText
                val nuevoId = View.generateViewId()
                nuevoEditText.id = nuevoId

                // Agrega el nuevo EditText al contenedor
                binding.container2.addView(nuevoEditText)
                //binding.container2.addView(TextViewFecha)

                // Agrega el EditText a la lista
                editTextList.add(nuevoEditText)

                ultimoEditTextCreado = nuevoEditText
                imageViewFavorito.visibility = View.VISIBLE
                textViewCreado = true
            } else {
                Toast.makeText(this, "YA SE CREÓ UNA NOTA", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGuardar.setOnClickListener {
            if (!textViewCreado) {
                // Muestra el mensaje si no se ha creado un TextView
                Toast.makeText(this, "NO SE HA CREADO NOTA", Toast.LENGTH_SHORT).show()
            } else {
                // Verifica si el último EditText creado está vacío
                if (ultimoEditTextCreado?.text.isNullOrBlank()) {
                    Toast.makeText(this, "NOTA VACÍA", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                cambioColor(binding.container2, "WHITE")
                val ideasGuardadas = editTextList.map { it.text.toString() }

                // Aquí puedes realizar la lógica de guardado, como guardar en Firebase o en otro lugar
                val db = FirebaseDatabase.getInstance()
                // Obtiene la referencia al nodo de ideas para el usuario actual
                val currentUser = Firebase.auth.currentUser
                val userIdeasRef = db.getReference("notas").child(currentUser?.uid ?: "")

                // Crear una nueva instancia de la clase Nota con los datos que deseas guardar
                val nuevaNota = Nota(
                    contenido = ideasGuardadas.joinToString(separator = "\n")
                )
                // Obtén la referencia a la nueva nota y establece manualmente el ID
                val nuevaNotaRef = userIdeasRef.push()
                nuevaNota.idNota = nuevaNotaRef.key ?: ""

                // Ahora, establece el valor de la nota en Firebase
                nuevaNotaRef.setValue(nuevaNota)
                //userIdeasRef.push().setValue(nuevaNota)

                // Por ahora, mostraremos un mensaje Toast como ejemplo
                Toast.makeText(this, getString(R.string.GuardarFirebase), Toast.LENGTH_SHORT).show()


                // Limpia la lista
                editTextList.clear()

                // Oculta el TextView después de guardar
                ultimoEditTextCreado?.visibility = View.GONE

                for (editText in editTextList) {
                    binding.container2.removeView(editText)
                }
                imageViewFavorito.visibility = View.INVISIBLE
                imageViewFavorito.setImageResource(R.drawable.estrella)
                enFavoritos = false

                // Limpia la lista
                editTextList.clear()
                textViewCreado = false  //Notificar que ya no hay textView
            }
        }

        binding.btnEliminar.setOnClickListener {
            // Verifica si hay algún EditText para eliminar
            if (ultimoEditTextCreado != null) {
                // Elimina el último EditText del contenedor
                binding.container2.removeView(ultimoEditTextCreado)

                // Remueve el último EditText de la lista
                editTextList.remove(ultimoEditTextCreado)

                // Limpia la referencia al último EditText
                ultimoEditTextCreado = null
                imageViewFavorito.visibility = View.INVISIBLE
                imageViewFavorito.setImageResource(R.drawable.estrella)
                enFavoritos = false
                textViewCreado = false  //Notificar que ya no hay textView
                cambioColor(binding.container2, "WHITE")
            }
        }



        imageViewFavorito = binding.imageViewFavorito
        imageViewFavorito.setOnClickListener {
            // Realiza la animación al tocar la ImageView
            realizarAnimacion()

            // Cambia la imagen de la ImageView según el estado
            if (enFavoritos) {
                imageViewFavorito.setImageResource(R.drawable.estrella)
                enFavoritos = false
            } else {
                imageViewFavorito.setImageResource(R.drawable.estrella2)
                enFavoritos = true
            }

            // Verifica si la nota actual ya está en la lista de favoritos
            if (ultimoEditTextCreado != null) {
                val notaActual = ultimoEditTextCreado!!.text.toString()
                if (enFavoritos) {
                    // Agrega la nota actual a la lista de favoritos
                    favoritosList.add(notaActual)
                    //Firebase
                    val db = FirebaseDatabase.getInstance()
                    val currentUser = Firebase.auth.currentUser

                    // Referencia al nodo de notas normales
                    val userNotasRef = db.getReference("notas").child(currentUser?.uid ?: "")
                    // Referencia al nodo de notas favoritas
                    val userFavoritosRef =
                        db.getReference("favoritos").child(currentUser?.uid ?: "")

                    val ideasGuardadas = editTextList.map { it.text.toString() }

                    val nuevaNota = Nota(
                        contenido = ideasGuardadas.joinToString(separator = "\n")
                    )

                    val nuevaNotaRef = userFavoritosRef.push()
                    nuevaNota.idNota = nuevaNotaRef.key ?: ""

                    nuevaNotaRef.setValue(nuevaNota)


                    Toast.makeText(this, getString(R.string.FavGuardada), Toast.LENGTH_SHORT).show()
                } else {
                    // Elimina la nota actual de la lista de favoritos
                    favoritosList.remove(notaActual)
                    Toast.makeText(this, getString(R.string.FavEliminada), Toast.LENGTH_SHORT)
                        .show()
                    val db = FirebaseDatabase.getInstance()
                    val currentUser = Firebase.auth.currentUser
                    val userFavoritosRef =
                        db.getReference("favoritos").child(currentUser?.uid ?: "")

                    // Supongamos que contenidoNota es la cadena de contenido de la nota que estás buscando
                    val contenidoNotaBuscada = notaActual

                    // Realiza una consulta para encontrar la nota en favoritos con el contenido específico
                    userFavoritosRef.orderByChild("contenido").equalTo(contenidoNotaBuscada)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (notaSnapshot in snapshot.children) {
                                    // Aquí, notaSnapshot.key contiene el ID único de la nota en favoritos
                                    val idNotaEnFavoritos = notaSnapshot.key
                                    // Puedes usar idNotaEnFavoritos según tus necesidades
                                    val notaEliminarRef =
                                        userFavoritosRef.child(idNotaEnFavoritos.toString())
                                    notaEliminarRef.removeValue()
                                    //Toast.makeText(
//                                        this@MainActivity,
//                                        "Nota elminada: $idNotaEnFavoritos",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Manejo de errores si es necesario
                            }
                        })

                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> irAVentanaNotas()
            R.id.item2 -> irAVentanaNotasFavoritas()
            R.id.item3 -> {
                // Obtener el idioma actual almacenado en las preferencias compartidas
                val currentLanguage = getLanguageFromPreferences()

                // Cambiar al siguiente idioma (español -> inglés y viceversa)
                val newLanguage = if (currentLanguage == "es") "en" else "es"

                // Guardar el nuevo idioma en las preferencias compartidas
                setLocale(newLanguage)
                saveLanguageToPreferences(newLanguage)

                // Reiniciar la actividad para aplicar el cambio de idioma
                recreate()
            }
            R.id.item4 -> salir()
        }
        return super.onOptionsItemSelected(item)
    }


    //Funciones

    private fun obtenerFechaActual(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = Date()
        return dateFormat.format(fechaActual)
    }

    private fun realizarAnimacion() {
        val animacion: Animation = AnimationUtils.loadAnimation(this, R.anim.animacion_favorito)
        imageViewFavorito.startAnimation(animacion)
    }

    private fun cambioColor(view: View, colorHex: String) {
        view.setBackgroundColor(Color.parseColor(colorHex))
    }
    private fun salir() {
        // Crear un Intent para pasar de esta actividad a la segunda actividad
        val intent = Intent(this, LoginActivity::class.java)
        // Iniciar la segunda actividad
        startActivity(intent)
    }
    fun irAVentanaNotas() {
        // Crear un Intent para pasar de esta actividad a la segunda actividad
        val intent = Intent(this, misNotas::class.java)
        // Iniciar la segunda actividad
        startActivity(intent)
    }

    fun irAVentanaNotasFavoritas() {
        // Crear un Intent para pasar de esta actividad a la segunda actividad
        val intent = Intent(this, misNotasFavoritas::class.java)
        // Iniciar la segunda actividad
        startActivity(intent)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun getLanguageFromPreferences(): String {
        val preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return preferences.getString(LANGUAGE_PREF_KEY, "en") ?: "en"
    }

    private fun saveLanguageToPreferences(language: String) {
        val preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        preferences.edit().putString(LANGUAGE_PREF_KEY, language).apply()
    }
}