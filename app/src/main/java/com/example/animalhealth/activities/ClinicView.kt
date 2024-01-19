package com.example.animalhealth.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animalhealth.R
import com.example.animalhealth.utilities.Clinic
import com.example.animalhealth.utilities.ClinicAdaptor
import com.example.animalhealth.utilities.Utilities
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ClinicView : AppCompatActivity() {
    private lateinit var db_ref:DatabaseReference
    private lateinit var clinic:Clinic
    private lateinit var adaptor: ClinicAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_clinic_view)
        var name=findViewById<TextView>(R.id.clinicName)
        var address=findViewById<TextView>(R.id.clinicAddress)
        var rate=findViewById<RatingBar>(R.id.clinicRate)
        var photo=findViewById<ImageView>(R.id.clinicImage)

        var context:Context=this

        clinic= Clinic()
        db_ref=FirebaseDatabase.getInstance().reference
        val id=intent.getStringExtra("id")

        db_ref.child("AnimalHealth")
            .child("clinics").child(id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val pojo_clinic = snapshot.getValue(Clinic::class.java)
                    clinic = pojo_clinic!!
                    name.text = clinic.name.toString().trim().capitalize()
                    address.text = clinic.adress.toString().trim().capitalize()
                    rate.rating = clinic.rate!!
                    val URL:String? = when (pojo_clinic.photo){
                        ""->null
                        else->pojo_clinic.photo
                    }

                    Glide.with(context).load(URL).apply(Utilities.glideOptions(context)).transition(
                        Utilities.transition).into(photo)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })
    }
}