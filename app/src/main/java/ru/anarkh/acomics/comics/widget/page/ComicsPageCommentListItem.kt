package ru.anarkh.acomics.comics.widget.page

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.comics_page_comment_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.model.Comment
import ru.anarkh.acomics.core.list.BaseListElement
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ComicsPageCommentListItem : BaseListElement<Comment>(R.layout.comics_page_comment_item) {

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: Comment) {
		holder.invalidateVisibility()
		val imageRequest = ImageRequest.fromUri(Uri.parse(model.userAvatarUrl))
		val frescoDraweeController = Fresco.newDraweeControllerBuilder()
			.setOldController(holder.itemView.user_avatar.controller)
			.setAutoPlayAnimations(false)
			.setImageRequest(imageRequest)
			.build()
		holder.itemView.user_avatar.controller = frescoDraweeController
		holder.itemView.user_name.text = model.userName
		if (model.userStatus != null) {
			holder.itemView.user_status.visibility = View.VISIBLE
			holder.itemView.user_status.text = model.userStatus
		}
		holder.itemView.comment_date.text = formCommentDate(model)
		holder.itemView.comment_body.text = model.body
		if (model.editedData != null) {
			holder.itemView.comment_edited.visibility = View.VISIBLE
			holder.itemView.comment_edited.text = model.editedData
		}
	}

	private fun formCommentDate(model: Comment): String {
		val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
		return simpleDateFormat.format(Date(TimeUnit.SECONDS.toMillis(model.date))).plus(" года")
	}

	private fun RecyclerView.ViewHolder.invalidateVisibility() {
		itemView.user_status.visibility = View.GONE
		itemView.comment_edited.visibility = View.GONE
	}
}