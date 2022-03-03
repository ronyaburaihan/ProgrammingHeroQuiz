package com.techdoctorbd.programmingheroquiz.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.techdoctorbd.programmingheroquiz.R

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun checkConnection(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connMgr.activeNetworkInfo
    return activeNetworkInfo != null
}

fun ImageView.loadImageFromUrl(
    context: Context,
    aImageUrl: String,
    aPlaceHolderImage: Int = R.drawable.ic_placeholder,
    aErrorImage: Int = R.drawable.ic_placeholder
) {
    try {
        if (aImageUrl.isNotEmpty()) {
            Glide.with(context).load(aImageUrl).placeholder(aPlaceHolderImage)
                .diskCacheStrategy(
                    DiskCacheStrategy.AUTOMATIC
                ).error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage, context)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int, context: Context) {
    Glide.with(context).load(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}
