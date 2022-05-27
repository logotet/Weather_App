package com.example.weatherapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat


class ResourceProvider(
    private val context: Context,
) {
    fun getStringResource(@StringRes stringId: Int): String =
        context.getString(stringId)

    fun getString(@StringRes stringId: Int, vararg arg: Any): String =
        context.getString(stringId, *arg)

    fun getDrawableResource(@DrawableRes drawableId: Int): Drawable? =
        ContextCompat.getDrawable(context, drawableId)

    fun getDrawable(@DrawableRes drawableId: Int,vararg arg: Any): Drawable? =
        ContextCompat.getDrawable(context, drawableId)
}
