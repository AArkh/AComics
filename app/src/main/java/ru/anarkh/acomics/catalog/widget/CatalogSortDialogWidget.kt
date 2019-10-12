package ru.anarkh.acomics.catalog.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
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

class CatalogSortDialogWidget(
	private val context: Context,
	lifecycle: Lifecycle,
	stateRegistry: StateRegistry
) : DialogFactory, DialogWidget {

	override val isShowing: Boolean
		get() = zombieDialog.isShowing
	var onSortingItemClick: ((pickedSort: CatalogSortingBy) -> Unit)? = null
	var currentlyPickedSortingProvider: (() -> CatalogSortingBy)? = null

	private var zombieDialog = ZombieDialog(lifecycle, context, this, stateRegistry, "sort_dialog")

	override fun create(builder: AlertDialog.Builder): Dialog {
		val bottomShitDialog = FixedBottomSheetDialog(context)
		bottomShitDialog.setOnCancelListener { hide() }
		val view = LayoutInflater.from(context).inflate(R.layout.catalog_sort_bottom_dialog, null)
		val sortByDateIcon = view.findViewById<View>(R.id.by_date_selected_icon)
		val sortBySubsIcon = view.findViewById<View>(R.id.by_subs_selected_icon)
		val sortByIssuesIcon = view.findViewById<View>(R.id.by_issue_selected_icon)
		val sortByAlphabetIcon = view.findViewById<View>(R.id.by_alphabet_selected_icon)
		when(currentlyPickedSortingProvider?.invoke()) {
			CatalogSortingBy.BY_DATE -> sortByDateIcon.visibility = View.VISIBLE
			CatalogSortingBy.BY_SUBS -> sortBySubsIcon.visibility = View.VISIBLE
			CatalogSortingBy.BY_ISSUES -> sortByIssuesIcon.visibility = View.VISIBLE
			CatalogSortingBy.BY_ALPHABET -> sortByAlphabetIcon.visibility = View.VISIBLE
		}
		view.findViewById<TextView>(R.id.by_date).setOnClickListener {
			onSortClick(CatalogSortingBy.BY_DATE)
		}
		view.findViewById<TextView>(R.id.by_subs).setOnClickListener {
			onSortClick(CatalogSortingBy.BY_SUBS)
		}
		view.findViewById<TextView>(R.id.by_issue).setOnClickListener {
			onSortClick(CatalogSortingBy.BY_ISSUES)
		}
		view.findViewById<TextView>(R.id.by_alphabet).setOnClickListener {
			onSortClick(CatalogSortingBy.BY_ALPHABET)
		}
		bottomShitDialog.setContentView(view)
		bottomShitDialog.updateHeight()
		return bottomShitDialog
	}

	override fun show() = zombieDialog.show()

	override fun hide() = zombieDialog.hide()

	fun retain() = zombieDialog.riseFromTheDead()

	private fun onSortClick(sortingBy: CatalogSortingBy) {
		onSortingItemClick?.invoke(sortingBy)
		hide()
	}
}