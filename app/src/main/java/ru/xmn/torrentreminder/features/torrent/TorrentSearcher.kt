package ru.xmn.torrentreminder.features.torrent

import org.jsoup.nodes.Document


interface TorrentSearcher {
    fun searchTorrents(query: String): List<TorrentData>
}

data class TorrentData(val name: String, val torrentUrl: String)

class JsoupTorrentSearcher(val documentProvider: (String) -> Document) : TorrentSearcher {
    override fun searchTorrents(query: String): List<TorrentData> {
        val doc = documentProvider(query)
        val newsHeadlines = doc.select("#mp-itn b a")

        return emptyList()
    }
}
