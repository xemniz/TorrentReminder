package ru.xmn.torrentreminder.features.torrent

import org.jsoup.nodes.Document


interface TorrentSearcher {
    fun searchTorrents(query: String): List<TorrentData>
}

data class TorrentData(override val name: String, override val torrentUrl: String): TorrentDataOwner

interface TorrentDataOwner {
    val name:String
    val torrentUrl:String
}

class JsoupTorrentSearcher(val documentProvider: (String) -> Document) : TorrentSearcher {
    override fun searchTorrents(query: String): List<TorrentData> {
        val doc = documentProvider(query)
        val newsHeadlines = doc.select("div#index")
                .select("table")
                .select("tbody")
                .select("tr")
                .select("td")

        val names = newsHeadlines.select("a[href*=/torrent/]")
        val torrentUrls = newsHeadlines.select("a.downgif")

        if (!newsHeadlines.isEmpty()) {
            return names.zip(torrentUrls){name, url -> TorrentData(name.html(), url.attr("href")) }
        } else {
            return emptyList()
        }
    }
}


