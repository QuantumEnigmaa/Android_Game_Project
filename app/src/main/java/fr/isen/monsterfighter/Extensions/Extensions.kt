package fr.isen.monsterfighter.Extensions

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object Extensions {
    fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Activity.dialog(msg: String, title: String?, simple: Boolean, function:() -> Unit) {
        val dialogBuilder = AlertDialog.Builder(this)

        when (simple) {
            false -> {
                dialogBuilder.setMessage(msg).setCancelable(false)
                    .setPositiveButton("Oui") { dialog, _ ->
                        run {
                            dialog.dismiss()
                            function()
                        }
                    }
                    .setNegativeButton("Non") { dialog, _ -> dialog.cancel() }
            }
            true -> dialogBuilder.setMessage(msg).setCancelable(true)
        }

        val alert = dialogBuilder.create()
        title?.run { alert.setTitle(title)}
        alert.show()
    }
}