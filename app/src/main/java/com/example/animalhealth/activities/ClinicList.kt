package com.example.animalhealth.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animalhealth.utilities.Animation
import com.example.animalhealth.utilities.Clinic
import com.example.animalhealth.utilities.ClinicAdaptor
import com.example.animalhealth.R
import com.example.animalhealth.utilities.State
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.atomic.AtomicInteger

class ClinicList : AppCompatActivity() {
    private lateinit var add: ImageView

    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Clinic>
    private lateinit var adaptador: ClinicAdaptor
    private lateinit var db_ref: DatabaseReference
    private var androidId: String = ""
    private lateinit var generator: AtomicInteger
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clinic_list)
        var settings = findViewById<ImageView>(R.id.settings)
        var filter = findViewById<CardView>(R.id.filter)
        var searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.search)


        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter((newText))
                return true
            }

        })

        add = findViewById(R.id.add)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("AnimalHealth")
            .child("clinics")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach { hijo: DataSnapshot?
                        ->
                        val pojo_clinic = hijo?.getValue(Clinic::class.java)
                        lista.add(pojo_clinic!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = ClinicAdaptor(lista)
        recycler = findViewById(R.id.lista_cluvs)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

        filter.setOnClickListener {
            Animation.animation(it, 0.95f, 1.0f, 100)
            lista.sortBy { it.rate }
            recycler.adapter?.notifyDataSetChanged()
            adaptador = ClinicAdaptor(lista)
            recycler = findViewById(R.id.lista_cluvs)
            recycler.adapter = adaptador
            lista.reverse()
        }

        add.setOnClickListener {

            Animation.animation(it, 0.95f, 1.0f, 100)
            val activity = Intent(applicationContext, CreateClinic::class.java)
            it.postDelayed({ startActivity(activity) }, 100)

        }

        settings.setOnClickListener {
            Animation.animation(it, 0.95f, 1.0f, 100)
            var newIntent = Intent(this, Settings::class.java)
            startActivity(newIntent)
        }

        createChannel()
        androidId = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
        db_ref = FirebaseDatabase.getInstance().reference
        generator = AtomicInteger(0)

        db_ref.child("AnimalHealth").child("clinics")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo = snapshot.getValue(Clinic::class.java)
                    if (pojo!!.userNotifications.equals(androidId) && pojo.not_state!!.equals(
                            State.create
                        )
                    ) {
                        db_ref.child("AnimalHealth").child("clinics").child(pojo.id!!)
                            .child("not_state").setValue(State.notificated)
                        generateNotification(
                            generator.incrementAndGet(),
                            pojo,
                            "Se ha creado una nueva clinica" + pojo.name,
                            "Nuevos datos en la app",
                            ClinicList::class.java
                        )
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo = snapshot.getValue(Clinic::class.java)
                    if (pojo!!.userNotifications.equals(androidId) && pojo.not_state!!.equals(
                            State.modificates
                        )
                    ) {
                        db_ref.child("AnimalHealth").child("clinics").child(pojo.id!!)
                            .child("not_state").setValue(State.notificated)
                        generateNotification(
                            generator.incrementAndGet(),
                            pojo,
                            "Se ha editado una  clinica" + pojo.name,
                            "Datos modificados en la app",
                            ClinicList::class.java
                        )
                    }
                }


                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojo = snapshot.getValue(Clinic::class.java)
                    if (!pojo!!.userNotifications.equals(androidId)
                    ) {
                        db_ref.child("AnimalHealth").child("clinics").child(pojo.id!!)
                            .child("not_state").setValue(State.notificated)
                        generateNotification(
                            generator.incrementAndGet(),
                            pojo,
                            "Se ha editado una  clinica" + pojo.name,
                            "Datos modificados en la app",
                            ClinicList::class.java
                        )
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun generateNotification(
        id_not: Int,
        pojo: Parcelable,
        content: String,
        tittle: String,
        destiny: Class<*>
    ) {
        val newIntent = Intent(applicationContext, destiny)
        newIntent.putExtra("clinic", pojo)

        var id = "test_channel"
        var pedingIntent =
            PendingIntent.getActivity(this, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.animalhealth)
            .setContentTitle(tittle)
            .setContentText(content)
            .setSubText("sistema de informacion")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pedingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            notify(id_not, notification)
        }
    }


    private fun createChannel() {
        val name = "basic_channel"
        var id = "test_channel"
        val description = "basic notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id, name, importance).apply {
            this.description = description
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

}