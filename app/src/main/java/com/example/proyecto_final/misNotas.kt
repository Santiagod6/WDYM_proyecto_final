package com.example.proyecto_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserecyclerviewkotlin.MyAdapter
import com.example.proyecto_final.databinding.ActivityMisNotasBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class misNotas : AppCompatActivity() {

    private lateinit var binding: ActivityMisNotasBinding
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : MutableList<Nota>
    val currentUser = Firebase.auth.currentUser
    val userIdeasRef = Firebase.database.getReference("notas").child(currentUser?.uid ?: "")

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        binding = ActivityMisNotasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRecyclerview = findViewById(R.id.recyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        userArrayList = mutableListOf<Nota>()
        getUserData()

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow2,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemRegresar -> Regresar()
            R.id.itemSalir -> salir()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserData() {

        userIdeasRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){

                        val nota = userSnapshot.getValue(Nota::class.java)
                        userArrayList.add(nota!!)

                    }
                    userRecyclerview.adapter = MyAdapter(userArrayList)
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