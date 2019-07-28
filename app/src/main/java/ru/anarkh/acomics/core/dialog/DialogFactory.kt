package ru.anarkh.acomics.core.dialog

import android.app.Dialog
import androidx.appcompat.app.AlertDialog

interface DialogFactory {
	fun create(builder: AlertDialog.Builder): Dialog
}
