package com.techdoctorbd.programmingheroquiz.util

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.techdoctorbd.programmingheroquiz.R

class CustomProgressDialog(private val context: Context) {

    private lateinit var alertDialog: AlertDialog

    fun showLoadingBar(message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val dialogView = View.inflate(context, R.layout.layout_progress_dialog, null)
        builder.setCancelable(false)
        builder.setView(dialogView)

        val progressText = dialogView.findViewById<TextView>(R.id.progress_bar_text)

        progressText.text = message

        alertDialog = builder.create()

        alertDialog.show()
    }

    fun dismiss() {
        alertDialog.dismiss()
    }
}