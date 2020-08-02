package ru.anarkh.acomics.main.favorite.widget

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.catalog_item.view.add_to_favorites_item
import kotlinx.android.synthetic.main.catalog_item.view.container
import kotlinx.android.synthetic.main.catalog_item.view.description
import kotlinx.android.synthetic.main.catalog_item.view.image
import kotlinx.android.synthetic.main.catalog_item.view.title
import kotlinx.android.synthetic.main.favorite_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.db.FavoriteEntity
import ru.anarkh.acomics.core.list.BaseListElement
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser

class FavoriteListElement(
	private val quantityStringParser: FixedLocaleQuantityStringParser
) : BaseListElement<FavoriteEntity>(R.layout.favorite_item) {

	var onItemClickListener: ((catalogId: String, pagesAmount: Int) -> Unit)? = null
	var onFavoriteClickListener: ((catalogId: String) -> Unit)? = null

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: FavoriteEntity) {
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
		holder.itemView.unread_issues_count.text = formReadPages(model, context)
	}

	private fun formTotalPages(totalPages: Int, context: Context) : String {
		return if (totalPages > 0) quantityStringParser.formatQuantityString(
			R.plurals.catalog_item_issues,
			totalPages,
			totalPages
		) else context.getText(R.string.catalog_item_no_updates) as String
	}

	private fun formReadPages(model: FavoriteEntity, context: Context): String {
		if (model.totalPages <= model.readPages) {
			return context.getString(R.string.favorites_no_pages_to_read)
		}
		return if (model.readPages > 0) quantityStringParser.formatQuantityString(
			R.plurals.read_issues,
			model.readPages,
			model.readPages
		) else context.getText(R.string.favorites_no_new_issues) as String
	}
}