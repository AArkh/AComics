package ru.anarkh.acomics.comics.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.R

class ComicsPageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
	LayoutInflater.from(parent.context).inflate(R.layout.comics_page_item, parent, false)
)