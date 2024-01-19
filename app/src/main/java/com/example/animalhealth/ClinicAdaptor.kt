package com.example.animalhealth

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ClinicAdaptor(private val clinic_list:MutableList<Clinic>): RecyclerView.Adapter<ClinicAdaptor.ClinicViewHolder>(),
    Filterable {

    private lateinit var context: Context
    private var filter_list=clinic_list


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClinicAdaptor.ClinicViewHolder {
        val item_view= LayoutInflater.from(parent.context).inflate(R.layout.item_clinic,parent,false)
        context=parent.context
        return ClinicViewHolder(item_view)
    }

    override fun getItemCount(): Int = filter_list.size

    class ClinicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val photo: ImageView = itemView.findViewById(R.id.photo_item)
        val name: TextView = itemView.findViewById(R.id.name_item)
        val address: TextView = itemView.findViewById(R.id.address_item)
        val ratingBar: RatingBar =itemView.findViewById(R.id.rating)
        val edit: ImageView = itemView.findViewById(R.id.edit_item)
        val delete: ImageView = itemView.findViewById(R.id.delete_item)
        val date:TextView=itemView.findViewById(R.id.date)

    }


    override fun onBindViewHolder(holder: ClinicViewHolder, position: Int) {
        val actual_item=filter_list[position]
        holder.name.text=actual_item.name
        holder.address.text=actual_item.adress
        holder.ratingBar.rating=actual_item.rate?:0f
        holder.date.text=actual_item.fecha

        val URL:String? = when (actual_item.photo){
            ""->null
            else->actual_item.photo
        }

        Glide.with(context).load(URL).apply(Utilities.glideOptions(context)).transition(Utilities.transition).into(holder.photo)
        holder.edit.setOnClickListener {
            Animation.animation(it, 0.95f, 1.0f, 100)
            var newIntent= Intent(context,EditClinic::class.java)
            newIntent.putExtra("clinic",actual_item)
            holder.edit.postDelayed({context.startActivity(newIntent)},100)

        }
        holder.delete.setOnClickListener {
            val db_ref = FirebaseDatabase.getInstance().getReference()
            val sto_ref = FirebaseStorage.getInstance().getReference()
            Animation.animation(it, 0.95f, 1.0f, 100)

            holder.delete.postDelayed({filter_list.remove(actual_item)
                sto_ref.child("AnimalHealth").child("clinics").child("photos").child(actual_item.id!!).delete()
                db_ref.child("AnimalHealth").child("clinics").child(actual_item.id!!).removeValue()

                Toast.makeText(context,"Clinica borrada con exito",Toast.LENGTH_SHORT).show()},100)
        }
    }

    override fun getFilter(): Filter {
        return  object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val search = p0.toString().lowercase()

                if (search.isEmpty()){
                    filter_list = clinic_list
                }else {
                    filter_list = (clinic_list.filter {
                        if (it.name.toString().lowercase().contains(search)){
                            it.name.toString().lowercase().contains(search)
                        }else{
                            it.adress.toString().lowercase().contains(search)
                        }
                    }) as MutableList<Clinic>
                }

                val filterResults = FilterResults()
                filterResults.values = filter_list
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}