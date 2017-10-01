package ru.xmn.torrentreminder.features.torrent.domain

data class TorrentSearch(val createdAt: Long, val id:String, val searchQuery: String, val lastSearchedItems: List<TorrentItem>) {
    val hasUpdates: Boolean
        get() = lastSearchedItems.find { !it.isViewed } != null
}