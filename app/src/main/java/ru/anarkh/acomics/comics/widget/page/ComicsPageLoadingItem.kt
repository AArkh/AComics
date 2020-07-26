package ru.anarkh.acomics.comics.widget.page

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.facebook.cache.common.CacheKey
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.comics_custom_progress_bar_indicator.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.model.ComicsPageUiModel
import ru.anarkh.acomics.core.list.BaseListElement

class ComicsPageLoadingItem : BaseListElement<ComicsPageUiModel>(
	R.layout.comics_custom_progress_bar_indicator
) {

	var successListener: ((page: Int) -> Unit)? = null
	var failedListener: ((page: Int) -> Unit)? = null

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: ComicsPageUiModel) {
		holder.itemView.retry_button.setOnClickListener {
			startUpload(holder, model)
		}
		when(model.state) {
			ComicsPageUiModel.State.Loading -> {
				startUpload(holder, model)
			}
			ComicsPageUiModel.State.Failed -> {
				showRetryButton(holder)
			}
			ComicsPageUiModel.State.Content -> {
				successListener?.invoke(model.comicsPageModel.page)
			}
		}
	}

	private fun startUpload(
		holder: RecyclerView.ViewHolder,
		model: ComicsPageUiModel
	) {
		val request: ImageRequest = ImageRequest.fromUri(model.comicsPageModel.imageUrl)
			?: return
		if (request.isInCache()) {
			successListener?.invoke(model.comicsPageModel.page)
			return
		}
		holder.itemView.progress_bar.visibility = View.VISIBLE
		holder.itemView.retry_button.visibility = View.GONE
		holder.itemView.progress_bar.progress = 0
		val prefetchSubscriber = ImagePrefetchSubscriber()
		prefetchSubscriber.onSuccessListener = {
			successListener?.invoke(model.comicsPageModel.page)
		}
		prefetchSubscriber.onProgressListener = { progress: Int ->
			holder.itemView.progress_bar.progress = progress
		}
		prefetchSubscriber.onFailListener = {
			failedListener?.invoke(model.comicsPageModel.page)
		}
		val source: DataSource<Void> = Fresco.getImagePipeline()
			.prefetchToBitmapCache(request, false)
		source.subscribe(
			prefetchSubscriber,
			UiThreadImmediateExecutorService.getInstance()
		)
	}

	private fun showRetryButton(holder: RecyclerView.ViewHolder) {
		holder.itemView.progress_bar.visibility = View.GONE
		holder.itemView.retry_button.visibility = View.VISIBLE
	}

	private fun ImageRequest.isInCache(): Boolean {
		val mainFileCache = ImagePipelineFactory
			.getInstance()
			.mainFileCache
		val cacheKey: CacheKey = DefaultCacheKeyFactory
			.getInstance()
			.getEncodedCacheKey(this, false)
		return mainFileCache.hasKey(cacheKey) && mainFileCache.getResource(cacheKey) != null
	}
}

private class ImagePrefetchSubscriber : DataSubscriber<Void> {

	var onSuccessListener: (() -> Unit)? = null
	var onProgressListener: ((progress: Int) -> Unit)? = null
	var onFailListener: (() -> Unit)? = null

	override fun onFailure(dataSource: DataSource<Void>) {
		onFailListener?.invoke()
	}

	override fun onCancellation(dataSource: DataSource<Void>) {
		onFailListener?.invoke()
	}

	override fun onProgressUpdate(dataSource: DataSource<Void>) {
		onProgressListener?.invoke((dataSource.progress * 100).toInt())
	}

	override fun onNewResult(dataSource: DataSource<Void>) {
		onSuccessListener?.invoke()
	}
}