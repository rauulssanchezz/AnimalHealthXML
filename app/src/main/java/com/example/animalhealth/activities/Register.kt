package com.example.animalhealth.activities
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.animalhealth.utilities.Animation
import com.example.animalhealth.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        var email = ""
        var password = ""
        var userOk = false
        var passwordOk = false
        var confirmPasswordOk = false
        var userEdit = findViewById<TextInputEditText>(R.id.user)
        var passwordEdit = findViewById<TextInputEditText>(R.id.password)
        var confirmPasswordEdit = findViewById<TextInputEditText>(R.id.confirmpassword)
        var save = findViewById<Button>(R.id.save)

        userEdit.setText("")
        passwordEdit.setText("")
        confirmPasswordEdit.setText("")

        userEdit.doAfterTextChanged {
            if (!userEdit.text.isNullOrBlank()) {
                if (!userEdit.text!!.toString().contains("@gmail.com")) {
                    userEdit.setError("El correo debe ser válido")
                }
            }
        }

        passwordEdit.doAfterTextChanged {
            if (!passwordEdit.text.isNullOrBlank()){
                if (passwordEdit.text!!.toString().length < 6) {
                    passwordEdit.setError("La contraseña debe contener al menos 6 caracteres")
                }
            }
        }

        save.setOnClickListener {
            Animation.animation(save, 0.98f, 1.0f, 100)

            if (userEdit.text.isNullOrBlank()) {
                userEdit.setError("Este campo es obligatorio")
            } else{
                email = userEdit.text.toString()
                userOk = true
            }

            if (passwordEdit.text.isNullOrBlank()) {
                passwordEdit.setError("Este campo es obligatorio")
            } else {
                password = passwordEdit.text.toString()
                passwordOk = true
            }

            if (confirmPasswordEdit.text.isNullOrBlank()) {
                confirmPasswordEdit.setError("Este campo es obligatorio")
            } else if (!confirmPasswordEdit.text!!.toString().equals(password)) {
                confirmPasswordEdit.setError("Las contraseñas deben coincidir")
            } else if (confirmPasswordEdit.text!!.toString().equals(password)) {
                confirmPasswordOk = true
            }

            if (userOk && passwordOk && confirmPasswordOk) {

                // Crear usuario en Firebase (puedes adaptar esto según tu lógica)
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                        makeText(this,"Se ha enviado un enlace de confirmación a su correo electrónico",Toast.LENGTH_LONG).show()
                        var newIntent= Intent(this, Log::class.java)
                        startActivity(newIntent)

                    }else{
                        makeText(baseContext, "Algo no ha salido como se esperaba", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
}