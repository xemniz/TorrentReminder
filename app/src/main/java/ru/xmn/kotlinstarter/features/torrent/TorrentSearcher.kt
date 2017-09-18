package ru.xmn.kotlinstarter.features.torrent

import org.jsoup.Jsoup
import org.jsoup.nodes.Document


interface TorrentSearcher {
    fun searchTorrents(query: String): List<TorrentItem>
}

data class TorrentItem(val name: String, val torrentUrl: String)

class JsoupTorrentSearcher(val documentProvider: (String) -> Document) : TorrentSearcher {
    override fun searchTorrents(query: String): List<TorrentItem> {
        val doc = documentProvider(query)
        val newsHeadlines = doc.select("#mp-itn b a")

        return emptyList()
    }
}
