package ru.anarkh.acomics.catalog.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.R

class CatalogSortItemViewHolder(
	parent: ViewGroup
) : RecyclerView.ViewHolder(
	LayoutInflater.from(parent.context).inflate(R.layout.catalog_sort_item, parent, false)
)