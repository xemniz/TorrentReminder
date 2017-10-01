package ru.xmn.torrentreminder.features.torrent.dataaccess

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.Disposables
import io.realm.*
import io.realm.annotations.PrimaryKey
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import java.util.*
import kotlin.collections.ArrayList

class RealmTorrentSearchRepository : TorrentSearchRepository {
    val searchAllChangeListeners: ArrayList<RealmChangeListener<RealmResults<RealmTorrentSearch>>> = ArrayList()
    val oneSearchChangeListeners: ArrayList<RealmChangeListener<RealmResults<RealmTorrentSearch>>> = ArrayList()

    override fun delete(id: String) {
        Realm.getDefaultInstance().use {
            val realmTorrentSearch = it.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.ID, id)
                    .findFirst()

            if (realmTorrentSearch != null)
                it.executeTransaction {
                    realmTorrentSearch.deleteFromRealm()
                }
        }
    }

    override fun update(id: String, searchQuery: String, dataList: List<TorrentData>) {
        Realm.getDefaultInstance().use { realm ->
            val realmTorrentSearch = realm.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.ID, id)
                    .findFirst()

            if (realmTorrentSearch != null) realmTorrentSearch.let { search ->
                val newItems = dataList.map { TorrentItem(it, false) }
                        .map { it.toRealm() }
                        .filter { newItem -> search.torrentItems.firstOrNull { oldItem -> oldItem.name == newItem.name } == null }
                realm.executeTransaction {
                    search.searchQuery = searchQuery
                    search.torrentItems.addAll(newItems)
                }
            } else {
                Log.d("RealmSearchRepository", "вызван метод update(), Итем не найден")
            }
        }
    }

    override fun insert(searchQuery: String, dataList: List<TorrentData>) {
        Realm.getDefaultInstance().use { realm ->
            val realmTorrentSearch = realm.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.SEARCHQUERY, searchQuery)
                    .findFirst()

            if (realmTorrentSearch == null)
                realm.executeTransaction {
                    it.copyToRealm(RealmTorrentSearch()
                            .apply {
                                this.createdAt = System.currentTimeMillis()
                                this.searchQuery = searchQuery
                                torrentItems = dataList.map { TorrentItem(it, false) }.map { it.toRealm() }.toRealmList()
                            })
                }
            else
                Log.d("RealmSearchRepository", "вызван метод insert(), Итем с таким именем уже существует")
        }
    }

    override fun checkAllItemsInSearchAsViewed(id: String) {
        Realm.getDefaultInstance().use {
            val realmTorrentSearch = it.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.ID, id)
                    .findFirst()

            it.executeTransaction { realmTorrentSearch?.torrentItems?.forEach { it.isViewed = true } }
        }
    }

    override fun subscribeSearch(id: String): Flowable<TorrentSearch> {
        return Flowable.create(FlowableOnSubscribe<List<RealmTorrentSearch>> { emitter ->
            val realm = Realm.getDefaultInstance()
            val realmResults = realm.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.ID, id)
                    .findAllAsync()
            val listener: RealmChangeListener<RealmResults<RealmTorrentSearch>> = object : RealmChangeListener<RealmResults<RealmTorrentSearch>> {
                override fun onChange(results: RealmResults<RealmTorrentSearch>) {
                    if (results.isLoaded && !emitter.isCancelled) {
                        val search = results
                        if (!emitter.isCancelled)
                            emitter.onNext(ArrayList(search))
                    }
                }

            }
            oneSearchChangeListeners += listener
            realmResults.addChangeListener(listener)
            emitter.setDisposable(Disposables.fromAction {
                if (realmResults.isValid)
                    realmResults.removeChangeListener(listener)
                oneSearchChangeListeners -= listener
                realm.close()
            })
        }, BackpressureStrategy.LATEST)
                .filter { it.isNotEmpty() }
                .map { it[0] }
                .map { it.fromRealm() }
    }

    override fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return Flowable.create(FlowableOnSubscribe<List<RealmTorrentSearch>> { emitter ->
            val realm = Realm.getDefaultInstance()
            val realmResults = realm.where(RealmTorrentSearch::class.java).findAllSortedAsync(RealmTorrentSearch.CREATED_AT, Sort.DESCENDING)

            val listener: RealmChangeListener<RealmResults<RealmTorrentSearch>> = RealmChangeListener<RealmResults<RealmTorrentSearch>> { results ->
                if (results.isLoaded && !emitter.isCancelled) {
                    val search = results
                    if (!emitter.isCancelled)
                        emitter.onNext(ArrayList(search))
                }
            }
            searchAllChangeListeners += listener
            realmResults.addChangeListener(listener)
            emitter.setDisposable(Disposables.fromAction {
                if (realmResults.isValid)
                    realmResults.removeChangeListener(listener)
                searchAllChangeListeners -= listener
                realm.close()
            })
        }, BackpressureStrategy.LATEST)
                .map { it.map { it.fromRealm() } }
    }
}

private fun <E : RealmObject> List<E>.toRealmList(): RealmList<E> {
    return RealmList<E>().apply {
        this@toRealmList.forEach { add(it) }
    }
}

fun TorrentItem.toRealm(): RealmTorrentItem {
    return RealmTorrentItem().apply {
        name = this@toRealm.name
        torrentUrl = this@toRealm.torrentUrl
        isViewed = this@toRealm.isViewed
    }
}

fun RealmTorrentSearch.fromRealm(): TorrentSearch {
    return TorrentSearch(this.createdAt, this.id, this.searchQuery, ArrayList(this.torrentItems).map { it.fromRealm() })
}

fun RealmTorrentItem.fromRealm(): TorrentItem {
    return TorrentItem(this.name, this.torrentUrl, this.isViewed)
}

open class RealmTorrentSearch : RealmObject() {
    companion object {
        val ID = "id"
        val SEARCHQUERY = "searchQuery"
        val CREATED_AT: String = "createdAt"
    }

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var searchQuery: String = ""
    var torrentItems: RealmList<RealmTorrentItem> = RealmList()
    var createdAt: Long = System.currentTimeMillis()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RealmTorrentSearch

        if (searchQuery != other.searchQuery) return false

        return true
    }

    override fun hashCode(): Int {
        return searchQuery.hashCode()
    }


}

open class RealmTorrentItem : RealmObject() {
    companion object {
        val NAME = "name"
    }

    var name: String = ""
    var torrentUrl: String = ""
    var isViewed: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RealmTorrentItem

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
