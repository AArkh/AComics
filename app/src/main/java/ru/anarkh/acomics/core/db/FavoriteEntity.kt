package ru.anarkh.acomics.core.db

import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.anarkh.acomics.core.db.BookmarkType.Companion.FOLLOWING
import ru.anarkh.acomics.core.db.BookmarkType.Companion.SIMPLE
import java.io.Serializable

const val FAVORITES_TABLE = "favorites"
const val BOOKMARK_TYPE_COLUMN = "bookmark_type"

@Entity(tableName = FAVORITES_TABLE)
data class FavoriteEntity(
	@field:PrimaryKey
	val catalogId: String,
	val previewImage: String,
	val totalPages: Int,
	val readPages: Int,
	val title: String,
	val description: String,
	@field:BookmarkType
	@ColumnInfo(defaultValue = "${BookmarkType.FOLLOWING}", name = BOOKMARK_TYPE_COLUMN)
	val bookmarkType: Int
) : Serializable


/**
 * Опции для закладок. [SIMPLE] для ручного выставления закладки, и [FOLLOWING] для
 * автоматического запоминания последней прочитанной странички.
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(BookmarkType.SIMPLE, BookmarkType.FOLLOWING)
annotation class BookmarkType {
	companion object {
		const val SIMPLE = 0
		const val FOLLOWING = 1
	}
}