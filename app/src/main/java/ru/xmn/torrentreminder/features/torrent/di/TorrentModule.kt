package ru.xmn.torrentreminder.features.torrent.di

import dagger.Module
import dagger.Provides
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.xmn.torrentreminder.features.torrent.domain.DocumentProvider
import ru.xmn.torrentreminder.features.torrent.searchers.JsoupTorrentSearcher
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher


@Module
class TorrentModule {
    @Provides
    fun provideDocumentProvider(): DocumentProvider = object : DocumentProvider {
        override fun provide(q: String): Document {
            return Jsoup.connect("http://live-rutor.org/search/$q/").get()
        }
    }

    @Provides
    fun provideTorrentSearch(documentProvider: DocumentProvider): TorrentSearcher = JsoupTorrentSearcher(documentProvider)
}