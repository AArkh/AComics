package ru.anarkh.acomics.catalog.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.model.CatalogSortingBy
import ru.anarkh.acomics.core.dialog.DialogFactory
import ru.anarkh.acomics.core.dialog.DialogWidget
import ru.anarkh.acomics.core.dialog.FixedBottomSheetDialog
import ru.anarkh.acomics.core.dialog.ZombieDialog
import ru.arkharov.statemachine.StateRegistry

class SortDialogWidget(
	private val context: Context,
	lifecycle: Lifecycle,
	stateRegistry: StateRegistry
) : DialogFactory, DialogWidget {

	override val isShowing: Boolean
		get() = zombieDialog.isShowing
	var onSortingItemClick: ((pickedSort: CatalogSortingBy) -> Unit)? = null

	private var zombieDialog = ZombieDialog(lifecycle, context, this, stateRegistry)

	init {
		zombieDialog.riseFromTheDead()
	}

	override fun create(builder: AlertDialog.Builder): Dialog {
		val bottomShitDialog = FixedBottomSheetDialog(context)
		val view = LayoutInflater.from(context).inflate(R.layout.catalog_sort_bottom_dialog, null)
		view.findViewById<TextView>(R.id.by_date).setOnClickListener {
			onSortingItemClick?.invoke(CatalogSortingBy.BY_DATE)
		}
		view.findViewById<TextView>(R.id.by_subs).setOnClickListener {
			onSortingItemClick?.invoke(CatalogSortingBy.BY_SUBS)
		}
		view.findViewById<TextView>(R.id.by_issue).setOnClickListener {
			onSortingItemClick?.invoke(CatalogSortingBy.BY_ISSUES)
		}
		view.findViewById<TextView>(R.id.by_alphabet).setOnClickListener {
			onSortingItemClick?.invoke(CatalogSortingBy.BY_ALPHABET)
		}
		bottomShitDialog.setContentView(view)
		bottomShitDialog.updateHeight()
		return bottomShitDialog
	}
	override fun show()  = zombieDialog.show()

	override fun hide() = zombieDialog.hide()
}