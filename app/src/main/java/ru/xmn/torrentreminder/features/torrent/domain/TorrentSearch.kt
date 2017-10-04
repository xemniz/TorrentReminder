package ru.xmn.torrentreminder.features.torrent.domain

data class TorrentSearch(val createdAt: Long, val id:String, val searchQuery: String, val lastSearchedItems: List<TorrentItem>)