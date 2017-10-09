package ru.xmn.torrentreminder.features.torrent.searchers

import ru.xmn.torrentreminder.features.torrent.domain.DocumentProvider
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher

class JsoupTorrentSearcher(val documentProvider: DocumentProvider) : TorrentSearcher {
    override fun searchTorrents(query: String): List<TorrentData> {
        val doc = documentProvider.provide(query)
        val newsHeadlines = doc.select("div.wrap")
                .select("table.forumline.tablesorter")
                .select("tbody")
                .select("tr")

        val names = newsHeadlines.select("td.genmed").select("a").select("b")
        val torrentUrls = newsHeadlines.select("td").select("a.genmed").select("a[href*=download]")

        if (!newsHeadlines.isEmpty()) {
            return names.zip(torrentUrls){name, url -> TorrentData(name.html(), url.attr("href")) }
        } else {
            return emptyList()
        }
    }

}