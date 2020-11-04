package ru.anarkh.acomics.comics.widget.page

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comics_page_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.model.ComicsPageModel
import ru.anarkh.acomics.comics.model.ComicsPageUiModel
import ru.anarkh.acomics.comics.model.Comment
import ru.anarkh.acomics.comics.model.UploaderData
import ru.anarkh.acomics.core.list.BaseListElement
import ru.anarkh.acomics.core.list.MultipleVHListAdapter

class ComicsPageListItem(
	context: Context,
	resources: Resources
) : BaseListElement<ComicsPageUiModel>(R.layout.comics_page_item) {

	private val validator = ComicsPageItemsDiffsValidator()
	private val viewPool = RecyclerView.RecycledViewPool()
	private val imageListItem = ComicsPageImageListItem(resources)
	private val loadingListItem = ComicsPageLoadingItem()
	private val uploaderCommentListItem = ComicsPageUploaderCommentListItem(context)
	private val commentListItem = ComicsPageCommentListItem()

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: ComicsPageUiModel) {
		if (holder.itemView.list.adapter == null) {
			initHolder(holder)
		}
		val adapter = holder.itemView.list.adapter as MultipleVHListAdapter
		if (model.state != ComicsPageUiModel.State.Content) {
			adapter.submitList(listOf(model))
		} else {
			val list = ArrayList<Any>(model.comicsPageModel.comments.size + 2)
			list.add(model.comicsPageModel)
			list.add(model.comicsPageModel.uploaderData)
			list.addAll(model.comicsPageModel.comments)
			adapter.submitList(list)
			imageListItem.imageShownListener = {
				// Очередной багофикс отображения piasy.BigImageView
				holder.itemView.list.post {
					holder.itemView.list.scrollToPosition(0)
				}
			}
		}
	}

	private fun initHolder(holder: RecyclerView.ViewHolder) {
		holder.itemView.list.layoutManager = LinearLayoutManager(holder.itemView.context)
		holder.itemView.list.adapter = MultipleVHListAdapter(
			validator,
			imageListItem to ComicsPageModel::class.java,
			loadingListItem to ComicsPageUiModel::class.java,
			uploaderCommentListItem to UploaderData::class.java,
			commentListItem to Comment::class.java
		)
		holder.itemView.list.setRecycledViewPool(viewPool)
	}

	fun setOnPageClickListener(listener: (() -> Unit)) {
		imageListItem.onPageClickListener = listener
	}

	fun setOnPageLoadedListener(listener: ((page: Int) -> Unit)) {
		loadingListItem.successListener = listener
	}

	fun setOnPageLoadFailedListener(listener: ((page: Int) -> Unit)) {
		loadingListItem.failedListener = listener
		imageListItem.onFailureListener = listener
	}

	fun setClickedHyperlinkListener(listener: (clickedLink: String) -> Unit) {
		uploaderCommentListItem.clickedHyperlinkListener = listener
	}
}