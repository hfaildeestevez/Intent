package com.example.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
//variable publica donde se guardara el dato del Intent
const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"
private val PROFESIONAL = "Profesional"
private val CLIENTE = "Cliente"

class pantalla_inicial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_inicial)

        val botonCliente:Button = findViewById(R.id.bCliente)
        botonCliente.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                //Transporto el dato message que la guardo en la variable definida antes
                putExtra(EXTRA_MESSAGE, CLIENTE)
            }
            //Inicio el intent
            startActivity(intent)
        }
        val botonProfesiona:Button = findViewById(R.id.bProfesional)
        botonProfesiona.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                //Transporto el dato message que la guardo en la variable definida antes
                putExtra(EXTRA_MESSAGE, PROFESIONAL)
            }
            //Inicio el intent
            startActivity(intent)
        }
    }
}





