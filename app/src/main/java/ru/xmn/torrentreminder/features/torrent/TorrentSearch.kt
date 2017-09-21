package ru.xmn.torrentreminder.features.torrent

data class TorrentSearch(val searchQuery: String, val lastSearchedItems: List<TorrentItem>) {
    val hasUpdates: Boolean
        get() = lastSearchedItems.firstOrNull { !it.isViewed } != null
}