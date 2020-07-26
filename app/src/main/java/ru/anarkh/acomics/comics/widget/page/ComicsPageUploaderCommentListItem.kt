package ru.anarkh.acomics.comics.widget.page

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.comics_page_uploader_comment_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.model.UploaderData
import ru.anarkh.acomics.core.list.BaseListElement

class ComicsPageUploaderCommentListItem : BaseListElement<UploaderData>(
	R.layout.comics_page_uploader_comment_item
) {

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: UploaderData) {
		val imageRequest = ImageRequest.fromUri(Uri.parse(model.uploaderAvatarUrl))
		val frescoDraweeController = Fresco.newDraweeControllerBuilder()
			.setOldController(holder.itemView.uploader_avatar.controller)
			.setAutoPlayAnimations(false)
			.setImageRequest(imageRequest)
			.build()
		holder.itemView.uploader_avatar.controller = frescoDraweeController
		holder.itemView.uploader_name.text = model.uploaderName
		if (model.uploaderComment.isBlank()) {
			holder.itemView.uploader_comment_body.visibility = View.GONE
		} else {
			holder.itemView.uploader_comment_body.visibility = View.VISIBLE
			holder.itemView.uploader_comment_body.text = model.uploaderComment
		}
	}
}