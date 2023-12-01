package com.example.proyecto_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserecyclerviewkotlin.MyAdapterFavoritas
import com.example.proyecto_final.databinding.ActivityMisNotasFavoritasBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class misNotasFavoritas : AppCompatActivity() {

    private lateinit var binding: ActivityMisNotasFavoritasBinding
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: MutableList<NotaFavorita>
    val currentUser = Firebase.auth.currentUser
    val userIdeasRef = Firebase.database.getReference("favoritos").child(currentUser?.uid ?: "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisNotasFavoritasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRecyclerview = findViewById(R.id.recyclerViewFavoritos)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        userArrayList = mutableListOf<NotaFavorita>()
        getUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemRegresar -> Regresar()
            R.id.itemSalir -> salir()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserData() {

        userIdeasRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        val nota = userSnapshot.getValue(NotaFavorita::class.java)
                        userArrayList.add(nota!!)
                    }
                    userRecyclerview.adapter = MyAdapterFavoritas(userArrayList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    fun Regresar() {
        // Crear un Intent para pasar de esta actividad a la segunda actividad
        val intent = Intent(this, MainActivity::class.java)

        // Iniciar la segunda actividad
        startActivity(intent)
    }

    private fun salir() {
        // Crear un Intent para pasar de esta actividad a la segunda actividad
        val intent = Intent(this, LoginActivity::class.java)
        // Iniciar la segunda actividad
        startActivity(intent)
    }
}