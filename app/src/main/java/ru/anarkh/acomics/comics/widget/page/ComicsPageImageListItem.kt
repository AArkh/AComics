package ru.anarkh.acomics.comics.widget.page

import android.content.res.Resources
import android.graphics.drawable.Animatable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.view.FrescoImageViewFactory
import com.github.piasy.biv.view.ImageShownCallback
import kotlinx.android.synthetic.main.comics_page_image_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.model.ComicsPageModel
import ru.anarkh.acomics.core.list.BaseListElement

/**
 * Виджет для отображения непосредственно страницы с комиксом. Содержит кучу важных костылей
 * для корректной работы как [BigImageView], так и загрузки gif с
 * height =[ViewGroup.LayoutParams.WRAP_CONTENT], ибо [BigImageView] в это дело не умеет.
 * fixme Переехать полностью на самописный ZoomableImageView.
 */
class ComicsPageImageListItem(
	resources: Resources
) : BaseListElement<ComicsPageModel>(R.layout.comics_page_image_item) {

	var onPageClickListener: (() -> Unit)? = null
	var onFailureListener: ((page: Int) -> Unit)? = null
	var imageShownListener: ((page: Int) -> Unit)? = null

	private val screenWidth: Int = resources.displayMetrics.widthPixels

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: ComicsPageModel) {
		val image: BigImageView = holder.itemView.image
		val gifImage: SimpleDraweeView = holder.itemView.gif_image
		image.setOnClickListener {
			onPageClickListener?.invoke()
		}
		gifImage.setOnClickListener {
			onPageClickListener?.invoke()
		}
		if (model.imageUrl.contains(".gif")) {
			image.visibility = View.GONE
			gifImage.visibility = View.VISIBLE
			val params = gifImage.layoutParams
			params.height = ViewGroup.LayoutParams.MATCH_PARENT
			gifImage.layoutParams = params
			startLoadingGif(gifImage, model)
		} else {
			image.visibility = View.VISIBLE
			gifImage.visibility = View.GONE
			startLoadingImage(image, model)
		}
	}

	private fun startLoadingImage(image: BigImageView, model: ComicsPageModel) {
		image.setImageViewFactory(FrescoImageViewFactory())
		image.showImage(Uri.parse(model.imageUrl))
		image.setImageShownCallback(object : ImageShownCallback {
			override fun onThumbnailShown() {}
			override fun onMainImageShown() {
				imageShownListener?.invoke(model.page)
			}
		})
	}

	/**
	 * Особенные ласки для имитации wrapContent для гифок.
	 */
	private fun startLoadingGif(gifImage: SimpleDraweeView, model: ComicsPageModel) {
		val imageRequest = ImageRequest.fromUri(model.imageUrl)
		val controllerListener = GifControllerListener()
		controllerListener.onFailureListener = {
			onFailureListener?.invoke(model.page)
		}
		controllerListener.onFinalImageSet = { imageInfo: ImageInfo? ->
			val layoutParams: ViewGroup.LayoutParams = gifImage.layoutParams
			layoutParams.height = if (imageInfo == null) {
				ViewGroup.LayoutParams.MATCH_PARENT
			} else {
				val aspectRatio: Float = (screenWidth / imageInfo.width.toFloat())
				(imageInfo.height * aspectRatio).toInt()
			}
			gifImage.layoutParams = layoutParams
		}
		val controller = Fresco.newDraweeControllerBuilder()
			.setOldController(gifImage.controller)
			.setControllerListener(controllerListener)
			.setAutoPlayAnimations(true)
			.setImageRequest(imageRequest)
			.build()
		gifImage.controller = controller
	}
}

private class GifControllerListener : ControllerListener<ImageInfo> {

	var onFinalImageSet: ((imageInfo: ImageInfo?) -> Unit)? = null
	var onFailureListener: (() -> Unit)? = null

	override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
		onFinalImageSet?.invoke(imageInfo)
	}

	override fun onFailure(id: String?, throwable: Throwable?) {
		onFailureListener?.invoke()
	}

	override fun onRelease(id: String?) {}
	override fun onSubmit(id: String?, callerContext: Any?) {}
	override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {}
	override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {}
}