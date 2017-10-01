package ru.xmn.torrentreminder.screens.torrentlist


import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_torrent.view.*
import ru.xmn.common.extensions.inflate
import ru.xmn.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem
import kotlin.properties.Delegates

/**
 * Created by Michael on 27.09.2017.
 *
 */
class TorrentListAdapter(val torrentDownload: (String) -> Unit): RecyclerView.Adapter<TorrentListAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items by Delegates.observable(emptyList<TorrentItem>()){
        _, old, new -> autoNotify(old, new)  { a, b -> a.name == b.name }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position],torrentDownload)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.item_torrent))

    override fun getItemCount() = items.size

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bind(torrentItem: TorrentItem, torrentDownload: (String) -> Unit){
            with(itemView){
                name.text = torrentItem.name
                download.setOnClickListener { torrentDownload(torrentItem.torrentUrl) }
            }
        }
    }
}