package com.example.animalhealth.activities
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.example.animalhealth.utilities.Animation
import com.example.animalhealth.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
class Log : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log)

        FirebaseApp.initializeApp(this)

        var auth = FirebaseAuth.getInstance()

        //required variables
        var email:String
        var password:String
        var newintent:Intent
        var register=findViewById<TextView>(R.id.register)
        var forgotpass=findViewById<TextView>(R.id.forgotpass)
        var userEdit=findViewById<TextInputEditText>(R.id.user)
        var passwordEdit=findViewById<TextInputEditText>(R.id.password)
        var log = findViewById<Button>(R.id.log)
        //Animation variable used to create animations of the Animation class
        var user=auth.currentUser

        //checking if the user has already been registered
        //&& user.isEmailVerified hay que añadirlo al if aunque funcione correctamente el enlace
        if (user != null ) {
            newintent=Intent(this, ClinicList::class.java)
            startActivity(newintent)
        }else{
            makeText(this,"Algo no ha salido como se esperaba",Toast.LENGTH_SHORT)
        }


        //"link" to start the registration
        register.setPaintFlags(register.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        forgotpass.setPaintFlags(forgotpass.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        register.setOnClickListener() {
            Animation.animation(register, 0.98f, 1.0f, 100)
            register.setTextColor(getColor(R.color.softblack))
            register.postDelayed({ register.setTextColor(getColor(R.color.black)) }, 300)
            newintent=Intent(this, Register::class.java)
            register.postDelayed({startActivity(newintent)},320)

        }

        forgotpass.setOnClickListener{
            Animation.animation(forgotpass, 0.98f, 1.0f, 100)
            forgotpass.setTextColor(getColor(R.color.softblack))
            forgotpass.postDelayed({ register.setTextColor(getColor(R.color.black)) }, 300)
            newintent=Intent(this, ForgotPass::class.java)
            register.postDelayed({startActivity(newintent)},320)
        }

        //"link" to log in
        log.setOnClickListener() {
        Animation.animation(log, 0.98f, 1.0f, 100)
            if (!userEdit.text.isNullOrBlank() && !passwordEdit.text.isNullOrBlank()) {
                email = userEdit.text.toString()
                password = passwordEdit.text.toString()

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    user=auth.currentUser
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso, actualiza la interfaz de usuario
                        if (user!!.isEmailVerified) {
                            newintent = Intent(this, ClinicList::class.java)
                            startActivity(newintent)
                        }else{
                            makeText(this, "Este usuario no se encuentra verificado", Toast.LENGTH_SHORT).show()
                        }
                        // Puedes redirigir a la pantalla de inicio o hacer otras acciones necesarias
                    } else {
                        // Si el inicio de sesión falla, muestra un mensaje al usuario
                        makeText(this, "El usuario o la contraseña son incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                userEdit.setError("Este campo es obligatorio")
                passwordEdit.setError("Este campo es obligatorio")
            }

        }
    }

}