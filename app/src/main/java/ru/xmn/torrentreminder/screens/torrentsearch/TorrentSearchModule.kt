package ru.xmn.torrentreminder.screens.torrentsearch

import dagger.Module
import dagger.Provides
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.xmn.torrentreminder.features.torrent.DocumentProvider
import ru.xmn.torrentreminder.features.torrent.JsoupTorrentSearcher
import ru.xmn.torrentreminder.features.torrent.TorrentSearcher


@Module
class TorrentSearchModule {
    @Provides
    fun provideDocumentProvider(): DocumentProvider = object : DocumentProvider {
        override fun provide(q: String): Document {
            return Jsoup.connect("http://live-rutor.org/search/$q/").get()
        }
    }

    @Provides
    fun provideTorrentSearch(documentProvider: DocumentProvider): TorrentSearcher = JsoupTorrentSearcher(documentProvider)
}