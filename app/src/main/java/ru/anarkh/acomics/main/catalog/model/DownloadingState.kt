package ru.anarkh.acomics.main.catalog.model

import androidx.annotation.IntRange
import java.io.Serializable

sealed class DownloadingState : Serializable

object NoThanks : DownloadingState()

object Downloaded : DownloadingState()

data class InProgress(@IntRange(from = 0L, to = 99L) val progress: Int) : DownloadingState()