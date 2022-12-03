package ru.smartro.inventory.ui.main.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

fun showActionDialog(
    context: Context,
    messageText: String,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    positiveAction: () -> Unit,
    negativeAction: (() -> Unit)? = null
) {
    val dialogClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    positiveAction()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    if(negativeAction != null)
                        negativeAction()
                    else
                        dialog.dismiss()
                }
            }
        }

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    builder.setMessage(messageText)
        .setPositiveButton(positiveButtonText ?: "Да", dialogClickListener)
        .setNegativeButton(negativeButtonText ?: "Нет", dialogClickListener)
        .show()
}