package ru.xmn.torrentreminder.features.torrent.domain

import org.jsoup.nodes.Document


interface TorrentSearcher {
    fun searchTorrents(query: String): List<TorrentData>
}

data class TorrentData(override val name: String, override val torrentUrl: String): TorrentDataOwner

interface TorrentDataOwner {
    val name:String
    val torrentUrl:String
}

interface DocumentProvider{
    fun provide(q: String): Document
}


