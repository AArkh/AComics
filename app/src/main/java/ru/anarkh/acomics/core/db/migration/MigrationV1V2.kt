package ru.anarkh.acomics.core.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.anarkh.acomics.core.db.BOOKMARK_TYPE_COLUMN
import ru.anarkh.acomics.core.db.BookmarkType
import ru.anarkh.acomics.core.db.FAVORITES_TABLE

class MigrationV1V2 : Migration(1, 2) {

	override fun migrate(database: SupportSQLiteDatabase) {
		database.execSQL("""
			ALTER TABLE $FAVORITES_TABLE
			ADD COLUMN $BOOKMARK_TYPE_COLUMN INTEGER NOT NULL DEFAULT ${BookmarkType.FOLLOWING}
		""".trimIndent())
	}
}