package ru.xmn.torrentreminder.features.torrent

data class TorrentSearch(val time: Long, val id:String, val searchQuery: String, val lastSearchedItems: List<TorrentItem>) {
    val hasUpdates: Boolean
        get() = lastSearchedItems.find { !it.isViewed } != null
}