package ru.xmn.torrentreminder.screens.torrentitem

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import ru.xmn.common.extensions.inflate
import ru.xmn.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import kotlin.properties.Delegates

class TorrentItemAdapter() : RecyclerView.Adapter<TorrentItemAdapter.ViewHolder>(), AutoUpdatableAdapter {
    var items by Delegates.observable(emptyList<TorrentSearchViewItem>()) { property, oldValue, newValue ->
        autoNotify(oldValue, newValue) { a, b -> a.uniqueIdentifier == b.uniqueIdentifier }
    }

    val viewTypes = listOf<TorrentSearchViewItem.itemCompanion>(
            TorrentSearchViewItem.Common.Companion,
            TorrentSearchViewItem.NewItem.Companion
    )

    override fun getItemViewType(position: Int) = items[position].type

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = viewTypes.first { viewType == it.TYPE }.createViewHolder(parent)

    override fun getItemCount() = items.size

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(torrentItem: TorrentSearchViewItem)
    }

    class CommonViewHolder(view: View) : ViewHolder(view) {
        override fun bind(torrentItem: TorrentSearchViewItem) {
            torrentItem as TorrentSearchViewItem.Common
            itemView.torrentName.text = torrentItem.torrentSearch.searchQuery
            itemView.torrentUpdatedInfo.text = when (torrentItem.torrentSearch.hasUpdates) {
                true -> "Есть обновления"
                false -> "Обновлений нет"
            }
        }
    }

    class NewItemViewHolder(view: View) : ViewHolder(view) {
        override fun bind(torrentItem: TorrentSearchViewItem) {
            torrentItem as TorrentSearchViewItem.NewItem
        }
    }
}

sealed class TorrentSearchViewItem(val uniqueIdentifier: String) {
    abstract val type: Int

    interface itemCompanion {
        val TYPE: Int
        fun createViewHolder(parent: ViewGroup): TorrentItemAdapter.ViewHolder
    }

    class Common(val torrentSearch: TorrentSearch) : TorrentSearchViewItem(torrentSearch.searchQuery) {
        companion object : itemCompanion {
            override val TYPE = 1
            override fun createViewHolder(parent: ViewGroup) =
                    TorrentItemAdapter.CommonViewHolder(parent.inflate(R.layout.list_item))
        }

        override val type: Int
            get() = TYPE
    }

    class NewItem : TorrentSearchViewItem("NewItem") {
        companion object : itemCompanion {
            override val TYPE = 2
            override fun createViewHolder(parent: ViewGroup) =
                    TorrentItemAdapter.NewItemViewHolder(parent.inflate(R.layout.new_torrent_search_item))
        }

        override val type: Int
            get() = TYPE

    }
}