package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

class SavedSearchDetailsUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentListRepository: TorrentSearchRepository) {

    fun getTorrentList(id: String): Flowable<TorrentSearch> {
        return torrentListRepository.subscribeSearch(id)
    }

    fun updateSearch(id: String): Completable {
        return torrentListRepository
                .subscribeSearch(id)
                .take(1)
                .observeOn(Schedulers.io())
                .flatMap { search ->
                    Flowable
                            .fromCallable { torrentSearcher.searchTorrents(search.searchQuery) }
                            .map { Pair(search, it) }
                            .subscribeOn(Schedulers.io())
                }
                .flatMapCompletable { Completable.fromCallable { torrentListRepository.update(it.first.id, it.first.searchQuery, it.second) } }
    }
}