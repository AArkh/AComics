package ru.anarkh.acomics.comics.model

import java.io.Serializable

data class ComicsPageModel(
	val page: Int,
	val comicsTitle: String,
	val imageUrl: String,
	val issueName: String?,
	val issueDate: Long?,
	val uploaderData: UploaderData,
	val comments: List<Comment>
) : Serializable

data class UploaderData(
	val uploaderName: String,
	val uploaderAvatarUrl: String,
	val uploaderComment: String
) : Serializable

data class Comment(
	val userName: String,
	val userAvatarUrl: String,
	val userStatus: String?,
	val body: String,
	val date: Long,
	val commentId: String,
	val editedData: String?
) : Serializable