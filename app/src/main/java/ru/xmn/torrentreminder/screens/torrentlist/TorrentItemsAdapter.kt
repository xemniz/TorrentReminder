package ru.xmn.torrentreminder.screens.torrentlist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_torrent.view.*
import ru.xmn.common.extensions.inflate
import ru.xmn.common.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem
import kotlin.properties.Delegates
import ru.xmn.common.extensions.animateBackground


class TorrentItemsAdapter(val torrentDownload: (String) -> Unit) : RecyclerView.Adapter<TorrentItemsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items by Delegates.observable(emptyList<TorrentItem>()) { _, old, new ->
        autoNotify(old, new,
                changePayload = { oldItemIndex, newItemIndex -> changePayload(old[oldItemIndex], new[newItemIndex]) }
        ) { a, b -> a.name == b.name }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        if (payloads?.isNotEmpty() == true && payloads[0] is TorrentItemChangePayload)
            holder.renderChanges(payloads[0] as TorrentItemChangePayload)
        else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], torrentDownload)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.item_torrent))

    override fun getItemCount() = items.size

    private fun changePayload(oldItem: TorrentItem, newItem: TorrentItem): TorrentItemChangePayload {
        return when {
            !oldItem.isViewed && newItem.isViewed -> TorrentItemChangePayload.ViewedChange(true)
            oldItem.isViewed && !newItem.isViewed -> TorrentItemChangePayload.ViewedChange(false)
            else -> TorrentItemChangePayload.NoChanges
        }
    }

    sealed class TorrentItemChangePayload {
        class ViewedChange(val viewed: Boolean): TorrentItemChangePayload()
        object NoChanges: TorrentItemChangePayload()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val viewedColor = itemView.context.resources.getColor(android.R.color.white)
        private val notViewedColor = itemView.context.resources.getColor(R.color.not_viewed_item_color)

        fun renderChanges(payload: TorrentItemChangePayload) {
            with(itemView) {
                when(payload){
                    is TorrentItemChangePayload.ViewedChange -> {
                        if (payload.viewed)
                            container.animateBackground(notViewedColor, viewedColor, 150)
                        else
                            container.animateBackground(viewedColor, notViewedColor, 150)
                    }
                }
            }
        }

        fun bind(torrentItem: TorrentItem, torrentDownload: (String) -> Unit) {
            with(itemView) {
                name.text = torrentItem.name
                download.setOnClickListener { torrentDownload(torrentItem.torrentUrl) }
                container.setBackgroundColor(
                        if (torrentItem.isViewed)
                            viewedColor
                        else
                            notViewedColor
                )
            }
        }
    }
}

