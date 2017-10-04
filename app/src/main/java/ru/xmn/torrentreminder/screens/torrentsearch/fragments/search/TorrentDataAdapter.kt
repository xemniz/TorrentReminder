package ru.xmn.torrentreminder.screens.torrentsearch.fragments.search

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_torrent.view.*
import ru.xmn.common.extensions.inflate
import ru.xmn.common.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import kotlin.properties.Delegates

class TorrentDataAdapter(val torrentDownload: (String) -> Unit) : RecyclerView.Adapter<TorrentDataAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items by Delegates.observable(emptyList<TorrentData>()) { _, old, new ->
        autoNotify(old, new) { a, b -> a.name == b.name }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], torrentDownload)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.item_torrent))

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(torrentData: TorrentData, torrentDownload: (String) -> Unit) {
            with(itemView) {
                name.text = torrentData.name
                download.setOnClickListener { torrentDownload(torrentData.torrentUrl) }
            }
        }
    }
}