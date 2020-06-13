package ru.anarkh.acomics.main.catalog.widget.filter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.dialog.DialogFactory
import ru.anarkh.acomics.core.dialog.DialogWidget
import ru.anarkh.acomics.core.dialog.FixedBottomSheetDialog
import ru.anarkh.acomics.core.dialog.ZombieDialog
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.main.catalog.model.MPAARating
import ru.anarkh.acomics.main.catalog.model.TranslationType
import ru.arkharov.statemachine.SavedSerializable
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
	var onDialogCloseListener: ((state: CatalogSortConfig) -> Unit)? = null

	private val currentState = SavedSerializable<CatalogSortConfig>("filter_dialog_state", null)
	private val dialog = ZombieDialog(lifecycle, context, this, stateRegistry, "filter_dialog")

	init {
		stateRegistry.register(currentState)
	}

	override fun create(builder: AlertDialog.Builder): Dialog {
		val bottomShitDialog = FixedBottomSheetDialog(context)
		val view = LayoutInflater.from(context).inflate(R.layout.catalog_filter_bottom_dialog, null)

		initRatingFilter(view)
		initTranslationFilter(view)
		initIssuesFilter(view)

		bottomShitDialog.setOnCancelListener {
			val value = currentState.value
			if (value != null) {
				onDialogCloseListener?.invoke(value)
			}
			currentState.value = null
			hide()
		}

		bottomShitDialog.setContentView(view)
		bottomShitDialog.updateHeight()
		return bottomShitDialog
	}

	override fun show() = dialog.show()
	override fun hide() = dialog.hide()

	fun retain() = dialog.riseFromTheDead()

	private fun initRatingFilter(view: View) {
		val anyRateItem = view.findViewById<RadioButton>(
			R.id.catalog_age_filter_dialog_rating_all_option
		)
		val undfRateItem = view.findViewById<CheckBox>(
			R.id.catalog_age_filter_dialog_rating_undef_option
		)
		val gRateItem = view.findViewById<CheckBox>(R.id.catalog_age_filter_dialog_rating_g_option)
		val pGRateItem = view.findViewById<CheckBox>(
			R.id.catalog_age_filter_dialog_rating_pg_option
		)
		val pG13RateItem = view.findViewById<CheckBox>(
			R.id.catalog_age_filter_dialog_rating_pg_13_option
		)

		if (currentState.value == null) {
			currentState.value = currentFilterConfigProvider?.invoke()?.copy()
		}
		val sortConfig = currentState.value

		fun toggleAll(isSelected: Boolean) {
			anyRateItem.isChecked = isSelected
			undfRateItem.isChecked = isSelected
			gRateItem.isChecked = isSelected
			pGRateItem.isChecked = isSelected
			pG13RateItem.isChecked = isSelected
		}

		if (sortConfig != null) {
			if (sortConfig.rating.size >= EVERY_AGE_RATING_OPTION_SIZE) {
				toggleAll(true)
			} else {
				if (sortConfig.rating.contains(MPAARating.UNDEFINED)) {
					undfRateItem.isChecked = true
				}
				if (sortConfig.rating.contains(MPAARating.G)) {
					gRateItem.isChecked = true
				}
				if (sortConfig.rating.contains(MPAARating.PG)) {
					pGRateItem.isChecked = true
				}
				if (sortConfig.rating.contains(MPAARating.PG_13)) {
					pG13RateItem.isChecked = true
				}
			}
		}

		fun isAllSelected(): Boolean {
			return undfRateItem.isChecked and
				gRateItem.isChecked and
				pGRateItem.isChecked and
				pG13RateItem.isChecked
		}

		fun updateAnyRateItemState() {
			anyRateItem.isChecked = isAllSelected()
		}

		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_all).setOnClickListener {
			toggleAll(!anyRateItem.isChecked)
			if (anyRateItem.isChecked) {
				currentState.value?.rating?.addAll(
					listOf(
						MPAARating.UNDEFINED, MPAARating.G, MPAARating.PG, MPAARating.PG_13
					)
				)
			} else {
				currentState.value?.rating?.clear()
			}
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_undef).setOnClickListener {
			undfRateItem.toggle()
			if (undfRateItem.isChecked) currentState.value?.rating?.add(MPAARating.UNDEFINED)
			else currentState.value?.rating?.remove(MPAARating.UNDEFINED)
			updateAnyRateItemState()
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_g).setOnClickListener {
			gRateItem.toggle()
			if (gRateItem.isChecked) currentState.value?.rating?.add(MPAARating.G)
			else currentState.value?.rating?.remove(MPAARating.G)
			updateAnyRateItemState()
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_pg).setOnClickListener {
			pGRateItem.toggle()
			if (pGRateItem.isChecked) currentState.value?.rating?.add(MPAARating.PG)
			else currentState.value?.rating?.remove(MPAARating.PG)
			updateAnyRateItemState()
		}
		view.findViewById<View>(R.id.catalog_age_filter_dialog_rating_pg_13).setOnClickListener {
			pG13RateItem.toggle()
			if (pG13RateItem.isChecked) currentState.value?.rating?.add(MPAARating.PG_13)
			else currentState.value?.rating?.remove(MPAARating.PG_13)
			updateAnyRateItemState()
		}
	}

	private fun initIssuesFilter(view: View) {
		val issuesCountFilterText = view.findViewById<TextView>(R.id.issues_filter_text)
		val issuesCountSeekBar = view.findViewById<SeekBar>(R.id.seek_bar)
		issuesCountSeekBar.progress = currentState.value?.minPages ?: 0
		issuesCountFilterText.text = context.getString(
			R.string.catalog_filter_type_dialog_issues_config_title,
			currentState.value?.minPages ?: 0
		)

		issuesCountSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onStartTrackingTouch(seekBar: SeekBar) {}
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				issuesCountFilterText.text = context.getString(
					R.string.catalog_filter_type_dialog_issues_config_title,
					progress
				)
			}

			override fun onStopTrackingTouch(seekBar: SeekBar) {
				currentState.value?.minPages = seekBar.progress
			}
		})
	}

	private fun initTranslationFilter(view: View) {
		val anyTypeOption = view.findViewById<RadioButton>(
			R.id.catalog_filter_type_dialog_all_option
		)
		val originalTypeOption = view.findViewById<RadioButton>(
			R.id.catalog_filter_type_dialog_original_option
		)
		val transTypeOption = view.findViewById<RadioButton>(
			R.id.catalog_filter_type_dialog_translate_option
		)

		when (currentState.value?.translationType) {
			TranslationType.ANY -> anyTypeOption.isChecked = true
			TranslationType.ORIGINAL -> originalTypeOption.isChecked = true
			TranslationType.TRANSLATED -> transTypeOption.isChecked = true
		}

		fun onTypeClick(type: TranslationType) {
			anyTypeOption.isChecked = TranslationType.ANY == type
			originalTypeOption.isChecked = TranslationType.ORIGINAL == type
			transTypeOption.isChecked = TranslationType.TRANSLATED == type
			currentState.value?.translationType = type
		}

		view.findViewById<View>(R.id.catalog_filter_type_dialog_all).setOnClickListener {
			onTypeClick(TranslationType.ANY)
		}
		view.findViewById<View>(R.id.catalog_filter_type_dialog_original).setOnClickListener {
			onTypeClick(TranslationType.ORIGINAL)
		}
		view.findViewById<View>(R.id.catalog_filter_type_dialog_translated).setOnClickListener {
			onTypeClick(TranslationType.TRANSLATED)
		}
	}
}