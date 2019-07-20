package ru.anarkh.acomics.catalog.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.catalog_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.controller.CatalogItemsDiffsValidator
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser


class CatalogAdapter(
    private val quantityStringParser: FixedLocaleQuantityStringParser
): PagedListAdapter<CatalogComicsItem, CatalogViewHolder>(CatalogItemsDiffsValidator()) {

    var onItemClickListener: ((link: String, pagesAmount: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder = CatalogViewHolder(parent)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val model = getItem(position) as CatalogComicsItem
        val context = holder.itemView.context

        holder.itemView.container.setOnClickListener {
            onItemClickListener?.invoke(model.hyperLink, model.totalPages)
        }
        val frescoDraweeController = Fresco.newDraweeControllerBuilder()
            .setOldController(holder.itemView.image.controller)
            .setAutoPlayAnimations(false)
            .setImageRequest(ImageRequest.fromUri(model.previewImage))
            .build()
        holder.itemView.image.controller = frescoDraweeController
        holder.itemView.title.text = model.title.comicsTitle
        holder.itemView.rating.text = formRatingText(context, model)
        holder.itemView.updates_count.text = model.formattedTotalPages
        holder.itemView.subscribers_count.text = formSubscribersCount(model, context)
        holder.itemView.description.text = model.description
        holder.itemView.last_updated.text = "Последнее обновление ${model.lastUpdated}"
    }

    private fun formRatingText(context: Context, model: CatalogComicsItem): SpannableString {
        val rating = "${context.getString(R.string.catalog_item_rating)} "
        val spannableRating = SpannableString(rating + model.rating.text)
        spannableRating.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, model.rating.colorRes)),
            rating.length,
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
        ) else context.getText(R.string.catalog_item_no_updates) as String
    }
}