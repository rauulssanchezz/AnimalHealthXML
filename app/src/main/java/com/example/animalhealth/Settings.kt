package com.example.animalhealth
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var logout=findViewById<Button>(R.id.Logout)
        var newintent:Intent

        logout.setOnClickListener{
            Animation.animation(logout,0.98f,1.0f,150)
            Firebase.auth.signOut()
            newintent=Intent(this,Log::class.java)
            logout.postDelayed({startActivity(newintent)},170)
        }

    }
}