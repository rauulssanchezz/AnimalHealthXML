package com.example.animalhealth.activities
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.animalhealth.utilities.Clinic
import com.example.animalhealth.R
import com.example.animalhealth.utilities.Utilities
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext
class CreateClinic : AppCompatActivity(), CoroutineScope {

    private lateinit var name:TextInputEditText
    private lateinit var adress:TextInputEditText
    private lateinit var nameLayout:TextInputLayout
    private lateinit var addressLayout:TextInputLayout
    private lateinit var photo:ImageView
    private lateinit var ratingBar:RatingBar
    private lateinit var add:Button
    private lateinit var back:Button
    var rating=0f

    private var url_photo: Uri?=null
    private lateinit var db_ref:DatabaseReference
    private lateinit var st_ref: StorageReference
    private lateinit var clinic_list: MutableList<Clinic>
    private lateinit var job: Job
    private lateinit var date:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_clinic)

        val this_activity = this
        job = Job()

        name=findViewById(R.id.name)
        adress=findViewById(R.id.adress)
        nameLayout=findViewById(R.id.nameLayout)
        addressLayout=findViewById(R.id.addressLayout)
        photo=findViewById(R.id.photo)
        ratingBar=findViewById(R.id.rating)
        add=findViewById(R.id.add)
        back=findViewById(R.id.back)
        rating=ratingBar.rating
        date=LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .toString()

        ratingBar.setOnRatingBarChangeListener { _, ratingInput, _ ->
            rating=ratingInput
        }

        db_ref=FirebaseDatabase.getInstance().reference
        st_ref = FirebaseStorage.getInstance().reference

        clinic_list= Utilities.getClinicLis(db_ref)


        add.setOnClickListener {

            if (name.text.toString().trim().isEmpty()||adress.text.toString().trim().isEmpty()){
                nameLayout.error="Campo obligatorio"
                addressLayout.error="Campo obligatorio"
            }else if(url_photo==null){
                Toast.makeText(applicationContext, "Falta seleccionar la foto", Toast.LENGTH_SHORT
                ).show()
            }else if(Utilities.clinicExist(clinic_list, name.text.toString().trim())){
                Toast.makeText(applicationContext, "Esa clinica ya existe", Toast.LENGTH_SHORT)
                    .show()
            }else if(rating<0.5) {
                Toast.makeText(applicationContext, "La valoración no puede ser 0", Toast.LENGTH_SHORT)
                    .show()
            }else{
                var generated_id:String?=db_ref.child("AnimalHealth").child("clinics").push().key


                launch {
                    val url_photo_firebase= Utilities.savePhoto(st_ref, generated_id!!, url_photo!!)

                    var clinic= Clinic(
                        generated_id,
                        name.text.toString().trim().capitalize(),
                        "c/ "+adress.text.toString().trim().capitalize(),
                        url_photo_firebase,
                        rating,
                        date
                    )
                    Utilities.writeClinic(db_ref, generated_id!!, clinic)


                    Utilities.toastCourutine(
                        this_activity,
                        applicationContext,
                        "Clinica creada con exito"
                    )

                    val activity=Intent(applicationContext, ClinicList::class.java)
                    startActivity(activity)

                }
            }
        }

        back.setOnClickListener {
            val activity=Intent(applicationContext, ClinicList::class.java)
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
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val galeryAcces = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if(it!=null){
            url_photo = it
            photo.setImageURI(it)
        }
    }
}