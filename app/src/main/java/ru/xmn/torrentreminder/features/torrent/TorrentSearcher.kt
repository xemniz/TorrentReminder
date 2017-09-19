package ru.xmn.torrentreminder.features.torrent

import org.jsoup.nodes.Document


interface TorrentSearcher {
    fun searchTorrents(query: String): List<TorrentData>
}

data class TorrentData(val name: String, val torrentUrl: String)

class JsoupTorrentSearcher(val documentProvider: (String) -> Document) : TorrentSearcher {
    override fun searchTorrents(query: String): List<TorrentData> {
        val doc = documentProvider(query)
        val newsHeadlines = doc.select("div#index")
                .select("table")
                .select("tbody")
                .select("tr")
                .select("td")

        val name = newsHeadlines.select("a[href*=/torrent/]")
        val torrentUrl = newsHeadlines.select("a.downgif")

        if (!newsHeadlines.isEmpty()) {

            val torrentData: List<TorrentData> = List(name.size,
                    init = { i -> TorrentData(name[i].html(), torrentUrl[i].attr("href")) })

            return torrentData
        } else {
            return emptyList()
        }
    }
}


