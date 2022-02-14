package com.example.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    //Declaramos un objeto  varible de tipo FirebaseAuth

    private lateinit var auth: FirebaseAuth

    //Declaramos variables dónde les pasaremos el id de los elementos del layout

    lateinit var email: EditText
    lateinit var contraseña: EditText
    lateinit var iniciarSesion: Button
    lateinit var registro: Button
    lateinit var mapaIntent: Intent
    lateinit var bRealTime: Button
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Asignamos los id de los elementos del layout a las varaibles previamente declaradas
        email = findViewById(R.id.email)
        contraseña = findViewById(R.id.contraseña)
        iniciarSesion = findViewById(R.id.iniciarSesion)
        registro = findViewById(R.id.registro)
        mapaIntent = Intent(this, MapsActivity::class.java)
        Log.i("Previo", "antes de declarar database")
        database = Firebase.database("https://intentasion-default-rtdb.europe-west1.firebasedatabase.app/").reference
        Log.i("Previo","Database declarado")




        //Instanciamos en objeto auth que nos permitira crear un usuario e iniciar sesion
        auth = Firebase.auth

        /*Hacemos un listener de los botones para que cuando se pulsen llamen a la función createAccount e sgnIn que
        Permitirá  crear una cuenta o inciar sesión cuando cuando se pulsen los botones*/
        registro.setOnClickListener {
            createAccount(email.text.toString(), contraseña.text.toString())
        }
        iniciarSesion.setOnClickListener {
            signIn(email.text.toString(), contraseña.text.toString())
        }


    }

    /**
     * Método que recibe un email y una contraseña las valida y crea un usuario con ellas.
     */
    private fun createAccount(email: String, password: String) {
        /*
            Si el registro del usuario  se realiza correctamente
            el listener del estado de autenticación será notificado y la lógica para manejar el
            el usuario que se ha registrado se puede resuelve en el listener.
        */
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Logs de control
                    Log.d(TAG, "createUserWithEmail:success")
                    Log.d("estado", "usuario registrado")
                    //Asignas a la variable user el usuario actual  para después pasarselo a método updateUI
                    val user = auth.currentUser
                    //Se llama al método que actualizará el layout cuando se haya registrado y se le pasa el usuario
                    updateUI(user)
                } else {
                    //  Si el registro falla, muestra un mensaje al usuario
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    //Logs de control
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Log.d("estado", "usuario NO registrado")

                }
            }

    }

    /**
     * Método que sirve para actualizar el layout cuando inicias sesión o te registras
     * Para que funcione deberias crear otras activities y que se cargasen cuando el registro
     * o el inicio fuesen correctos y te permitiesen acceder a las funcionalidades de la app
     */

    private fun updateUI(user: FirebaseUser?) {
        Log.d("estado", "" + auth.currentUser?.uid)
    }

    /**
     * Método que sirve para iniciar sesión a través de un email y contraseña
     */
    private fun signIn(email: String, password: String) {
        /*
          Si el inicio de sesión del usuario  se realiza correctamente
          el listener del estado de autenticación será notificado y la lógica para manejar el
          el usuario que ha iniciado sesión se puede resuelve en el listener.
      */
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Logs de control
                    Log.d(TAG, "signInWithEmail:success")
                    Log.d("estado", "inicio de sesión correcto")
                    //Asignas a la variable user el usuario actual para después pasarselo a método updateUI
                    val user = auth.currentUser
                    //Se llama al método que actualizará el layout cuando se haya iniciado sesión y se le pasa el usuario
                    updateUI(user)
                    startActivity(mapaIntent)
                } else {
                    //  Si el inicio de sesión falla, muestra un mensaje al usuario
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Log.d("estado", "No se puedo iniciar sesion")
                }
            }

    }

    /*
     Un companion object es un objeto que es común a todas las instancias de esa clase. Vendría a ser similar a los campos estáticos en Java.
     En este caso creamos el objeto TAG al que llamaremos en los Log de control para saber si se ha iniciado sesión correctamente
     */

    companion object {
        private const val TAG = "EmailPassword"
    }

}