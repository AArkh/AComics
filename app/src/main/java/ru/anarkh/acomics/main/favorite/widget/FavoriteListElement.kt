package ru.anarkh.acomics.main.favorite.widget

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.catalog_item.view.add_to_favorites_item
import kotlinx.android.synthetic.main.catalog_item.view.container
import kotlinx.android.synthetic.main.catalog_item.view.description
import kotlinx.android.synthetic.main.catalog_item.view.image
import kotlinx.android.synthetic.main.catalog_item.view.title
import kotlinx.android.synthetic.main.favorite_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.list.BaseListElement
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity

class FavoriteListElement(
	private val quantityStringParser: FixedLocaleQuantityStringParser
) : BaseListElement<FavoriteEntity, FavoriteViewHolder>() {

	var onItemClickListener: ((catalogId: String, pagesAmount: Int) -> Unit)? = null
	var onFavoriteClickListener: ((catalogId: String) -> Unit)? = null

	override fun getViewType(): Int = 234346511

	override fun onCreateViewHolder(parent: ViewGroup) = FavoriteViewHolder(parent)

	override fun onBind(holder: FavoriteViewHolder, position: Int, model: FavoriteEntity) {
		val context = holder.itemView.context
		holder.itemView.container.setOnClickListener {
			onItemClickListener?.invoke(model.catalogId, model.totalPages)
		}
		holder.itemView.add_to_favorites_item.setOnClickListener {
			onFavoriteClickListener?.invoke(model.catalogId)
		}

		val imageRequest = ImageRequest.fromUri(Uri.parse(model.previewImage))
		val frescoDraweeController = Fresco.newDraweeControllerBuilder()
			.setOldController(holder.itemView.image.controller)
			.setAutoPlayAnimations(false)
			.setImageRequest(imageRequest)
			.build()

		holder.itemView.image.controller = frescoDraweeController
		holder.itemView.title.text = model.title
		holder.itemView.description.text = model.description
		holder.itemView.issues_overall_count.text = formTotalPages(model.totalPages, context)
		holder.itemView.unread_issues_count.text = formUnreadPages(model, context)
	}

	private fun formTotalPages(totalPages: Int, context: Context) : String {
		return if (totalPages > 0) quantityStringParser.formatQuantityString(
			R.plurals.catalog_item_issues,
			totalPages,
			totalPages
		) else context.getText(R.string.catalog_item_no_updates) as String
	}

	private fun formUnreadPages(model: FavoriteEntity, context: Context): String {
		if (model.totalPages <= 0) {
			return context.getString(R.string.favorites_no_pages_to_read)
		}
		val unreadPages = model.totalPages - model.readPages
		return if (unreadPages > 0) quantityStringParser.formatQuantityString(
			R.plurals.unread_issues,
			unreadPages,
			unreadPages
		) else context.getText(R.string.favorites_no_new_issues) as String
	}
}