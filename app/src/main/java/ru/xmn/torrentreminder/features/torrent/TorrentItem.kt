package ru.xmn.torrentreminder.features.torrent

data class TorrentItem(private val item: TorrentData, val isViewed: Boolean) : TorrentDataOwner by item {
    constructor(name: String, torrentUrl: String, isViewed: Boolean) : this(TorrentData(name, torrentUrl), isViewed)
}