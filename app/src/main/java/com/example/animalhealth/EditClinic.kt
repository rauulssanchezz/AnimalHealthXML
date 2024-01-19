package com.example.animalhealth
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
class EditClinic : AppCompatActivity(), CoroutineScope {
    private lateinit var name: EditText
    private lateinit var address: EditText
    private lateinit var photo: ImageView
    private lateinit var ratingBar:RatingBar
    private lateinit var edit: Button
    private lateinit var back: Button


    private var url_photo: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private lateinit var pojo_clinic: Clinic
    private lateinit var clinic_list: MutableList<Clinic>
    private lateinit var job: Job
    private var beforeName=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_clinic)
        val this_activity = this
        job = Job()

        pojo_clinic = intent.getParcelableExtra<Clinic>("clinic")!!


        name = findViewById(R.id.name)
        address = findViewById(R.id.address)
        photo = findViewById(R.id.photo)
        ratingBar=findViewById(R.id.rating)
        edit = findViewById(R.id.edit)
        back = findViewById(R.id.back)

        name.setText(pojo_clinic.name)
        beforeName=name.text.toString()
        address.setText(pojo_clinic.adress)
        ratingBar.rating=pojo_clinic.rate!!


        Glide.with(applicationContext)
            .load(pojo_clinic.photo)
            .apply(Utilities.glideOptions(applicationContext))
            .transition(Utilities.transition)
            .into(photo)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        clinic_list = Utilities.getClinicLis(db_ref)

        edit.setOnClickListener {

            if (name.text.toString().trim().isEmpty() ||
                address.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formularion", Toast.LENGTH_SHORT
                ).show()

            } else if (Utilities.clinicExist(clinic_list, name.text.toString().trim()) && !name.text.toString().trim().equals(beforeName)) {
                Toast.makeText(applicationContext, "Esa Clinica ya existe", Toast.LENGTH_SHORT)
                    .show()
            }else if(ratingBar.rating<0.5){
                Toast.makeText(applicationContext, "La puntuación no puede ser 0", Toast.LENGTH_SHORT)
                    .show()
            } else {

                //GlobalScope(Dispatchers.IO)
                photo.setOnClickListener {
                    galeryAcces.launch("image/*")
                }

                launch {
                    var url_photo_firebase= String()
                    if(url_photo == null){
                        url_photo_firebase = pojo_clinic.photo!!
                    }else{
                        url_photo_firebase =
                            Utilities.savePhoto(st_ref, pojo_clinic.id!!, url_photo!!)
                    }

                    var clinic=Clinic(
                        pojo_clinic.id!!,
                        name.text.toString().trim().capitalize(),
                        "c/ "+address.text.toString().trim().capitalize(),
                        url_photo_firebase,
                        ratingBar.rating
                    )

                    Utilities.writeClinic(db_ref,pojo_clinic.id!!,clinic)

                    Utilities.toastCourutine(
                        this_activity,
                        applicationContext,
                        "Clinica modificado con exito"
                    )
                    val activity = Intent(applicationContext, ClinicList::class.java)
                    startActivity(activity)
                }


            }




        }

        back.setOnClickListener {
            val activity = Intent(applicationContext, ClinicList::class.java)
            startActivity(activity)
        }

        photo.setOnClickListener {
            galeryAcces.launch("image/*")
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val galeryAcces = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_photo = uri
            photo.setImageURI(uri)
        }


    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}