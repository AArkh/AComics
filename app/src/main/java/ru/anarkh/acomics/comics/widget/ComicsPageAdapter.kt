package ru.anarkh.acomics.comics.widget

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.FrescoImageViewFactory
import kotlinx.android.synthetic.main.comics_page_item.view.*
import ru.anarkh.acomics.comics.model.ComicsPageModel
import java.io.File

class ComicsPageAdapter(
	private val issues: List<ComicsPageModel>
) : RecyclerView.Adapter<ComicsPageHolder>() {

	var onPageClickListener: (() -> Unit)? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsPageHolder {
		val pageHolder = ComicsPageHolder(parent)
		pageHolder.itemView.image.setImageViewFactory(FrescoImageViewFactory())
		pageHolder.itemView.image.setCustomProgressIndicator(CustomProgressBar())
		pageHolder.itemView.image.setOnClickListener {
			onPageClickListener?.invoke()
		}
		return pageHolder
	}

	override fun getItemCount(): Int = issues.size

	override fun onBindViewHolder(holder: ComicsPageHolder, position: Int) {
		val page: ComicsPageModel = issues[position]
		holder.itemView.image.removeAllViews()
		holder.itemView.image.showImage(Uri.parse(page.imageUrl))
		holder.itemView.image.wasDetachedFromWindow = false
		holder.itemView.image.setImageLoaderCallback(FailCallback {
			showRetryButton(holder)
		})
		hideRetryButton(holder)
		holder.itemView.retry_button.setOnClickListener {
			hideRetryButton(holder)
			holder.itemView.image.showImage(Uri.parse(page.imageUrl))
		}
	}

	private fun showRetryButton(holder: ComicsPageHolder) {
		holder.itemView.image.visibility = View.GONE
		holder.itemView.retry_button.visibility = View.VISIBLE
	}

	private fun hideRetryButton(holder: ComicsPageHolder) {
		holder.itemView.image.visibility = View.VISIBLE
		holder.itemView.retry_button.visibility = View.GONE
	}
}

private class FailCallback(
	private val listener: () -> Unit
) : ImageLoader.Callback {

	override fun onFail(error: Exception?) {
		error?.printStackTrace()
		listener.invoke()
	}

	override fun onFinish() {}
	override fun onSuccess(image: File?) {}
	override fun onCacheHit(imageType: Int, image: File?) {}
	override fun onCacheMiss(imageType: Int, image: File?) {}
	override fun onProgress(progress: Int) {}
	override fun onStart() {}
}