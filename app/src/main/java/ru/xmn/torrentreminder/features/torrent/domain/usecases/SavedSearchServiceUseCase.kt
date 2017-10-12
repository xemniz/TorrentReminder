package ru.xmn.torrentreminder.features.torrent.domain.usecases

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.SavedSearchesFragment
import javax.inject.Inject

class SavedSearchServiceUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    fun updateAllItems(): Single<ArrayList<TorrentSearch>> {
        val oldItems = getCurrentSavedSearches()

        return oldItems.flatMap { search ->
            Flowable.fromCallable { torrentSearcher.searchTorrents(search.searchQuery) }
                    .flatMap { resultList ->
                        torrentSearchRepository.update(search.id, search.searchQuery, resultList)
                        Flowable.just(true)
                    }
                    .onErrorReturn { false }
                    .subscribeOn(Schedulers.io())
        }
                //сначала обновляем всё, потом сравниваем с обновленным результатом
                .toList()
                .toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Flowable.zip<TorrentSearch, TorrentSearch, Result>(oldItems, getCurrentSavedSearches(), BiFunction { t1, t2 ->
                        return@BiFunction if (t1 != t2) Result.hasUpdates(t2) else Result.hasNotUpdates
                    })
                }
                .reduce(ArrayList<TorrentSearch>(), { list, result -> if (result is Result.hasUpdates) list.add(result.search); list })
    }

    private fun getCurrentSavedSearches(): Flowable<TorrentSearch> {
        return torrentSearchRepository.subscribeAllSearches()
                .take(1)
                .flatMap { Flowable.fromIterable(it) }
                .cache()
    }

    fun notification(context: Context, it: ArrayList<TorrentSearch>) {

        val intent = Intent(context, SavedSearchesFragment::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val builder = NotificationCompat.Builder(context)

        builder.setContentIntent(pIntent)
                .setSmallIcon(android.R.drawable.ic_media_ff)
                .setContentTitle("Обновлено ${it.size} торрентов")
                .setContentText("")
                .setDefaults(Notification.DEFAULT_VIBRATE)

        val notification = NotificationCompat
                .BigTextStyle(builder)
                .bigText(updatedItems(it))
                .build()
        NotificationManagerCompat.from(context).notify(0, notification)
    }

    private fun updatedItems(list: ArrayList<TorrentSearch>): String {
        var s = ""
        list.forEach { s += it.searchQuery + " +" + it.lastSearchedItems.size + "\n" }
        return s
    }

    sealed class Result {
        class hasUpdates(val search: TorrentSearch) : Result()
        object hasNotUpdates : Result()
    }
}