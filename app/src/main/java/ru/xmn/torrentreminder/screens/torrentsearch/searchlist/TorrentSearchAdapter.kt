package ru.xmn.torrentreminder.screens.torrentsearch.searchlist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.torrent_search_item.view.*
import ru.xmn.common.extensions.gone
import ru.xmn.common.extensions.inflate
import ru.xmn.common.extensions.invisible
import ru.xmn.common.extensions.visible
import ru.xmn.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import kotlin.properties.Delegates

class TorrentSearchAdapter( val torrentSearchStart: (String, String) -> Unit) : RecyclerView.Adapter<TorrentSearchAdapter.ViewHolder>(), AutoUpdatableAdapter {
    var items by Delegates.observable(emptyList<TorrentSearch>()) { property, oldValue, newValue ->
        autoNotify(oldValue, newValue) { a, b -> a.id == b.id }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], torrentSearchStart)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.torrent_search_item))

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(torrentSearch: TorrentSearch, torrentSearchStart: (String, String) -> Unit) {
            with(itemView) {
                torrentNameEditorButton.setOnClickListener { torrentSearchStart(torrentSearch.id, torrentNameEditor.text.toString()) }
                torrentName.text = torrentSearch.searchQuery
                torrentNameEditor.setText(torrentSearch.searchQuery)
                torrentUpdatedInfo.text = when (torrentSearch.hasUpdates) {
                    true -> "Есть обновления"
                    false -> "Обновлений нет"
                }

                when (torrentSearch.searchQuery == "") {
                    true -> {
                        torrentName.invisible()
                        torrentNameEditor.visible()
                        torrentNameEditorButton.visible()
                        torrentUpdatedInfo.invisible()
                    }
                    false -> {
                        torrentName.visible()
                        torrentNameEditorButton.invisible()
                        torrentNameEditor.invisible()
                        torrentUpdatedInfo.visible()
                    }
                }
            }
        }
    }
}

