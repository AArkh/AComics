package ru.anarkh.acomics.core.list.convenience

import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.list.BaseListElement

class LoadingElement : BaseListElement<LoadingElement.Stub>(R.layout.loading_item) {

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: Stub) {}

	object Stub
}