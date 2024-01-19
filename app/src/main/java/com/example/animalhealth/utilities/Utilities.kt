package com.example.animalhealth.utilities
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.animalhealth.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
class Utilities {
    companion object {
        fun clinicExist(clinic: List<Clinic>, name: String): Boolean {
            return clinic.any { it.name!!.lowercase() == name.lowercase() }
        }

        fun getClinicLis(db_ref: DatabaseReference): MutableList<Clinic> {
            var list = mutableListOf<Clinic>()

            db_ref.child("AnimalHealth").child("clinics")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { child: DataSnapshot ->
                            val pojo_clinic = child.getValue(Clinic::class.java)
                            list.add(pojo_clinic!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
            return list
        }

        fun writeClinic(db_ref:DatabaseReference,id:String,clinic: Clinic){

            db_ref.child("AnimalHealth").child("clinics").child(id).setValue(clinic)
        }

    suspend fun savePhoto(sto_ref: StorageReference, id: String, image: Uri): String {
        lateinit var url_photo_firebase: Uri
        url_photo_firebase = sto_ref.child("AnimalHealth").child("clinics").child("photos").child(id)
            .putFile(image).await().storage.downloadUrl.await()

        return url_photo_firebase.toString()
    }

    fun toastCourutine(activity: AppCompatActivity, contex: Context, text: String) {
        activity.runOnUiThread {
            Toast.makeText(contex, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun load_animation(contex: Context): CircularProgressDrawable {
        val animation = CircularProgressDrawable(contex)
        animation.strokeWidth = 5f
        animation.centerRadius = 30f
        animation.start()
        return animation
    }

    val transition = DrawableTransitionOptions.withCrossFade(500)

    fun glideOptions(contex: Context): RequestOptions {
        val options = RequestOptions().placeholder(load_animation(contex))
            .fallback(R.drawable.baseline_business_24)
            .error(R.drawable.baseline_error_24)
        return options
    }
}
}