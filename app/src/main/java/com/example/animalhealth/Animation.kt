package com.example.animalhealth

import android.view.View

class Animation (){

    companion object{
        fun animation(view: View, scale1:Float, scale2:Float, time:Long){
            view.animate().apply {
                scaleY(scale1)
                scaleX(scale1)
                duration=time
            }.withEndAction {
                view.animate().apply {
                    scaleY(scale2)
                    scaleX(scale2)
                    duration=time
                }
            }
        }
    }

}