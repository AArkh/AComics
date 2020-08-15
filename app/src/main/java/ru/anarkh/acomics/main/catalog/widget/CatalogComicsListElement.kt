package ru.anarkh.acomics.main.catalog.widget

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.catalog_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.list.BaseListElement
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemUiModel
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel
import ru.anarkh.acomics.main.catalog.model.MPAARating
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser
import java.util.*
import java.util.concurrent.TimeUnit

class CatalogComicsListElement(
	private val quantityStringParser: FixedLocaleQuantityStringParser
) : BaseListElement<CatalogComicsItemUiModel>(R.layout.catalog_item) {

	var onItemClickListener: ((webModel: CatalogComicsItemWebModel) -> Unit)? = null
	var onFavoriteClickListener: ((model: CatalogComicsItemUiModel) -> Unit)? = null
	var onReportClickListener: ((model: CatalogComicsItemUiModel) -> Unit)? = null

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: CatalogComicsItemUiModel) {
		val context = holder.itemView.context
		holder.itemView.container.setOnClickListener {
			onItemClickListener?.invoke(model.webModel)
		}
		holder.itemView.add_to_favorites_item.setOnClickListener {
			onFavoriteClickListener?.invoke(model)
		}

		val imageRequest = ImageRequest.fromUri(Uri.parse(model.previewImage))
		val frescoDraweeController = Fresco.newDraweeControllerBuilder()
			.setOldController(holder.itemView.image.controller)
			.setAutoPlayAnimations(false)
			.setImageRequest(imageRequest)
			.build()

		holder.itemView.image.controller = frescoDraweeController
		holder.itemView.title.text = model.title
		holder.itemView.rating.text = formRatingText(model, context)
		holder.itemView.updates_count.text = formTotalPages(model, context)
		holder.itemView.subscribers_count.text = formSubscribersCount(model, context)
		holder.itemView.description.text = model.description
		holder.itemView.last_updated.text = formLastUpdate(model, context)
		val favoriteIconRes = if (model.isFavorite) {
			R.drawable.ic_favorite_24dp
		} else {
			R.drawable.ic_favorite_border_24dp
		}
		holder.itemView.add_to_favorites_item.setImageResource(favoriteIconRes)
		holder.setUpMenu(model, context)
	}

	private fun formRatingText(model: CatalogComicsItemUiModel, context: Context): SpannableString {
		val ratingPrefix = "${context.getString(R.string.catalog_item_rating)} "
		val spannableRating = SpannableString(ratingPrefix + model.rating)
		val ratingColorRes = MPAARating.fromString(model.rating).colorRes
		spannableRating.setSpan(
			ForegroundColorSpan(ContextCompat.getColor(context, ratingColorRes)),
			ratingPrefix.length,
			spannableRating.length,
			Spannable.SPAN_INCLUSIVE_INCLUSIVE
		)
		return spannableRating
	}

	private fun formSubscribersCount(model: CatalogComicsItemUiModel, context: Context): String {
		return if (model.totalSubscribers > 0) quantityStringParser.formatQuantityString(
			R.plurals.catalog_item_subscribers,
			model.totalSubscribers,
			model.totalSubscribers
		) else context.getText(R.string.catalog_item_no_subs) as String
	}

	private fun formLastUpdate(model: CatalogComicsItemUiModel, context: Context): String {
		val hoursAgo = TimeUnit.SECONDS.toHours(model.lastUpdated)
		return if (hoursAgo < 24) {
			val formatterHoursAgo = quantityStringParser.formatQuantityString(
				R.plurals.catalog_item_hours,
				hoursAgo.toInt(),
				hoursAgo.toInt()
			)
			"Последний выпуск $formatterHoursAgo"
		} else {
			val unixTime = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(model.lastUpdated)
			val dateFormatter = DateFormat.getDateFormat(context)
			"Последний выпуск ${dateFormatter.format(Date(unixTime))}"
		}
	}

	private fun formTotalPages(model: CatalogComicsItemUiModel, context: Context) : String {
		return if (model.totalPages > 0) quantityStringParser.formatQuantityString(
			R.plurals.catalog_item_issues,
			model.totalPages,
			model.totalPages
		) else context.getText(R.string.catalog_item_no_updates) as String
	}

	private fun RecyclerView.ViewHolder.setUpMenu(
		model: CatalogComicsItemUiModel,
		context: Context
	) {
		itemView.menu.setOnClickListener {
			val popupMenu = PopupMenu(context, itemView.menu)
			popupMenu.inflate(R.menu.catalog_item_menu)
			popupMenu.setOnMenuItemClickListener { item: MenuItem ->
				if (item.itemId == R.id.report_item) {
					onReportClickListener?.invoke(model)
					return@setOnMenuItemClickListener true
				}
				return@setOnMenuItemClickListener false
			}
			popupMenu.show()
		}
	}
}