package ru.anarkh.acomics.catalog.widget

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.catalog_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.MPAARating
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.core.list.BaseListElement
import java.util.*
import java.util.concurrent.TimeUnit

class CatalogComicsListElement(
	private val quantityStringParser: FixedLocaleQuantityStringParser
) : BaseListElement<CatalogComicsItem, CatalogViewHolder>() {

	var onItemClickListener: ((comicsTitle: String, pagesAmount: Int) -> Unit)? = null

	override fun onCreateViewHolder(parent: ViewGroup): CatalogViewHolder = CatalogViewHolder(parent)

	override fun onBind(holder: CatalogViewHolder, position: Int, model: CatalogComicsItem) {
		val context = holder.itemView.context
		holder.itemView.container.setOnClickListener {
			onItemClickListener?.invoke(model.title, model.totalPages)
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
	}

	override fun getViewType(): Int = 123

	private fun formRatingText(model: CatalogComicsItem, context: Context): SpannableString {
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

	private fun formSubscribersCount(model: CatalogComicsItem, context: Context): String {
		return if (model.totalSubscribers > 0) quantityStringParser.formatQuantityString(
			R.plurals.catalog_item_subscribers,
			model.totalSubscribers,
			model.totalSubscribers
		) else context.getText(R.string.catalog_item_no_subs) as String
	}

	private fun formLastUpdate(model: CatalogComicsItem, context: Context): String {
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

	private fun formTotalPages(model: CatalogComicsItem, context: Context) : String {
		return if (model.totalPages > 0) quantityStringParser.formatQuantityString(
			R.plurals.catalog_item_issues,
			model.totalPages,
			model.totalPages
		) else context.getText(R.string.catalog_item_no_updates) as String
	}
}