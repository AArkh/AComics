package ru.anarkh.acomics.comics.widget

import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comics_page_item.view.*
import ru.anarkh.acomics.comics.model.ComicsPage

class ComicsPageAdapter(
	private val issues: List<ComicsPage>
) : RecyclerView.Adapter<ComicsPageHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsPageHolder {
		val pageHolder = ComicsPageHolder(parent)
		pageHolder.itemView.image.setProgressIndicator(CustomProgressBar())
		return pageHolder
	}

	override fun getItemCount(): Int = issues.size

	override fun onBindViewHolder(holder: ComicsPageHolder, position: Int) {
		val page = issues[position]
		holder.itemView.image.removeAllViews()
		holder.itemView.image.showImage(Uri.parse(page.imageUrl))
	}
}