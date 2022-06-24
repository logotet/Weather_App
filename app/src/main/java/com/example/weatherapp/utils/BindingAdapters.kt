package com.example.weatherapp.utils

import android.graphics.drawable.RotateDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.weatherapp.R

@BindingAdapter("imgUrl")
fun setBackground(imgView: ImageView, url: String?) {
    Glide.with(imgView.context)
        .asBitmap()
        .override(300, 300)
        .load(url)
        .into(imgView)
}

@BindingAdapter("drawableDirection")
fun setUpDrawable(textView: TextView, rotation: Int) {
    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_direction, 0, 0, 0);
    val compoundDrawables = textView.compoundDrawables
    val rotateDrawable = RotateDrawable()
    val drawable = compoundDrawables[0]
    rotateDrawable.drawable = drawable
    rotateDrawable.fromDegrees = rotation.toFloat()
    rotateDrawable.toDegrees = rotation.toFloat()
    rotateDrawable.level = 1
    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, rotateDrawable, null);
}

@BindingAdapter("visible")
fun toggleVisibility(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
    }
}

