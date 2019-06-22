package ru.anarkh.acomics.catalog.widget

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.controller.CatalogItemsDiffsValidator
import ru.anarkh.acomics.catalog.model.CatalogComicsItem


class CatalogAdapter: PagedListAdapter<CatalogComicsItem, CatalogViewHolder>(CatalogItemsDiffsValidator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        return CatalogViewHolder(parent, R.layout.catalog_item)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val model = getItem(position) as CatalogComicsItem
        holder.title.text = model.title.comicsTitle
        holder.description.text = model.description
        holder.lastUpdated.text = "Последнее обновление ${model.lastUpdated}"
    }
}