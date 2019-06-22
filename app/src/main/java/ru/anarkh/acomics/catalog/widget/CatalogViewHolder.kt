package ru.anarkh.acomics.catalog.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.R

class CatalogViewHolder(
    parent: ViewGroup,
    @LayoutRes layout: Int
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layout, parent, false)
) {
    val image: ImageView = parent.findViewById(R.id.image)
    val title: TextView = parent.findViewById(R.id.title)
    val description: TextView = parent.findViewById(R.id.description)
    val lastUpdated: TextView = parent.findViewById(R.id.last_updated)
}
