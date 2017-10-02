package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

/**
 * Created by Michael on 02.10.2017.
 *
 */
class SearchFragmentUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository){

    fun addNewItem(searchQuery: String) {
        Completable.fromCallable { torrentSearchRepository.insert(searchQuery, emptyList()) }
                .subscribeOn(Schedulers.io())
                .subscribe { }
    }

    fun firstSearchOnItem(searchQuery: String): Flowable<List<TorrentData>> {
        return Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .subscribeOn(Schedulers.io())
    }

}