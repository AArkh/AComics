package ru.anarkh.acomics.core.image

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.Px
import androidx.annotation.WorkerThread
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSources
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder

class ImageUploader{

	@WorkerThread
	fun loadBitmap(
		imageUri: Uri,
		@Px requiredHeight: Int,
		@Px requiredWidth: Int
	): Bitmap? {
		val imageRequest = ImageRequestBuilder.newBuilderWithSource(imageUri)
			.setResizeOptions(ResizeOptions.forDimensions(requiredWidth, requiredHeight))
			.build()
		val dataSource = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, this)
		try {
			val closeableRef: CloseableReference<CloseableImage>? = DataSources
				.waitForFinalResult(dataSource)
			return (closeableRef?.get() as? CloseableBitmap)?.underlyingBitmap
		} finally {
			dataSource.close()
		}
		return null
	}
}