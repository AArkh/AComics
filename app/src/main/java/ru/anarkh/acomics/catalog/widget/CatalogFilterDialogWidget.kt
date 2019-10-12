package ru.anarkh.acomics.catalog.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.catalog.model.MPAARating
import ru.anarkh.acomics.core.dialog.DialogFactory
import ru.anarkh.acomics.core.dialog.DialogWidget
import ru.anarkh.acomics.core.dialog.FixedBottomSheetDialog
import ru.anarkh.acomics.core.dialog.ZombieDialog
import ru.arkharov.statemachine.StateRegistry

private const val EVERY_AGE_RATING_OPTION_SIZE = 4

class CatalogFilterDialogWidget(
	private val context: Context,
	lifecycle: Lifecycle,
	stateRegistry: StateRegistry
) : DialogFactory, DialogWidget {

	override val isShowing: Boolean
		get() = dialog.isShowing

	var currentFilterConfigProvider: (() -> CatalogSortConfig)? = null

	private var dialog = ZombieDialog(lifecycle, context, this, stateRegistry, "filter_dialog")

	override fun create(builder: AlertDialog.Builder): Dialog {
		val bottomShitDialog = FixedBottomSheetDialog(context)
		val view = LayoutInflater.from(context).inflate(R.layout.catalog_filter_bottom_dialog, null)

		val anyRateItem = view.findViewById<RadioButton>(R.id.catalog_age_filter_dialog_rating_all_option)
		val undfRateItem = view.findViewById<CheckBox>(R.id.catalog_age_filter_dialog_rating_undef_option)
		val gRateItem = view.findViewById<CheckBox>(R.id.catalog_age_filter_dialog_rating_g_option)
		val pGRateItem = view.findViewById<CheckBox>(R.id.catalog_age_filter_dialog_rating_pg_option)
		val pG13RateItem = view.findViewById<CheckBox>(R.id.catalog_age_filter_dialog_rating_pg_13_option)

		val sortConfig = currentFilterConfigProvider?.invoke()
		if (sortConfig != null) {
			when {
				sortConfig.rating.size >= EVERY_AGE_RATING_OPTION_SIZE -> {
					anyRateItem.isSelected = true
				}
				sortConfig.rating.contains(MPAARating.UNDEFINED) -> {
					undfRateItem.isSelected = true
				}
				sortConfig.rating.contains(MPAARating.G) -> {
					gRateItem.isSelected = true
				}
				sortConfig.rating.contains(MPAARating.PG) -> {
					pGRateItem.isSelected = true
				}
				sortConfig.rating.contains(MPAARating.PG_13) -> {
					pG13RateItem.isSelected = true
				}
			}
		}

		fun isAllSelected(): Boolean {
			return undfRateItem.isSelected and
				gRateItem.isSelected and
				pGRateItem.isSelected and
				pG13RateItem.isSelected
		}

		fun isNoneSelected(): Boolean {
			return !undfRateItem.isSelected and
				!gRateItem.isSelected and
				!pGRateItem.isSelected and
				!pG13RateItem.isSelected
		}

		fun toggleAll(isSelected: Boolean) {
			anyRateItem.isSelected = isSelected
			undfRateItem.isSelected = isSelected
			gRateItem.isSelected = isSelected
			pGRateItem.isSelected = isSelected
			pG13RateItem.isSelected = isSelected
		}

		fun updateAnyRateItemState() {
			if (isAllSelected()) {
				anyRateItem.isSelected = true
			} else if (isNoneSelected()) {
				anyRateItem.isSelected = false
			}
		}

		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_all).setOnClickListener {
			toggleAll(!anyRateItem.isSelected)
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_undef).setOnClickListener {
			undfRateItem.toggle()
			updateAnyRateItemState()
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_g).setOnClickListener {
			gRateItem.toggle()
			updateAnyRateItemState()
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_pg).setOnClickListener {
			pGRateItem.toggle()
			updateAnyRateItemState()
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_pg_13).setOnClickListener {
			pG13RateItem.toggle()
			updateAnyRateItemState()
		}

		bottomShitDialog.setContentView(view)
		bottomShitDialog.updateHeight()
		return bottomShitDialog
	}

	override fun show() = dialog.show()
	override fun hide() = dialog.hide()

	fun retain() = dialog.riseFromTheDead()
}