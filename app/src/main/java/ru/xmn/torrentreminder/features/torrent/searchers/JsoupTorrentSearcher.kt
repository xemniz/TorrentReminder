package ru.xmn.torrentreminder.features.torrent.searchers

import ru.xmn.torrentreminder.features.torrent.domain.DocumentProvider
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher

class JsoupTorrentSearcher(val documentProvider: DocumentProvider) : TorrentSearcher {
    override fun searchTorrents(query: String): List<TorrentData> {
        val doc = documentProvider.provide(query)
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