package ru.anarkh.acomics.comics.widget

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comics_page_item.view.*
import ru.anarkh.acomics.comics.model.ComicsPageData

class ComicsPageAdapter(
	@IntRange(from = 1L) pagesCount: Int
) : RecyclerView.Adapter<ComicsPageHolder>() {

	private val list = Array<ComicsPageData?>(pagesCount) { null } //Пустой массив

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsPageHolder {
		val pageHolder = ComicsPageHolder(parent)
		pageHolder.itemView.image.setProgressIndicator(CustomProgressBar())
		return pageHolder
	}

	override fun getItemCount(): Int = list.size

	override fun onBindViewHolder(holder: ComicsPageHolder, position: Int) {
		val page = list[position]
		holder.itemView.image.removeAllViews()
		if (page != null) {
			holder.itemView.image.visibility = View.VISIBLE
			holder.itemView.image.showImage(Uri.parse(page.imageUrl))
		} else {
			holder.itemView.image.visibility = View.INVISIBLE
		}
	}

	fun putPage(pageIndex: Int, pageData: ComicsPageData) {
		list[pageIndex] = pageData
		notifyItemChanged(pageIndex)
	}
}