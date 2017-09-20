package ru.xmn.torrentreminder.features.torrent

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey

class RealmTorrentSearchRepository : TorrentSearchRepository {
    override fun delete(searchQuery: String) {
        Realm.getDefaultInstance().use {
            val realmTorrentSearch = it.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.QUERY, searchQuery)
                    .findFirst()

            if (realmTorrentSearch != null)
                it.executeTransaction {
                    realmTorrentSearch.deleteFromRealm()
                }
        }
    }

    override fun insertOrUpdate(searchQuery: String, dataList: List<TorrentData>) {
        Realm.getDefaultInstance().use { realm ->
            val realmTorrentSearch = realm.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.QUERY, searchQuery)
                    .findFirst()

            if (realmTorrentSearch == null)
                realm.executeTransaction {
                    it.copyToRealm(RealmTorrentSearch()
                            .apply {
                                this.searchQuery = searchQuery
                                torrentItems = dataList.map { TorrentItem(it, false) }.map { it.toRealm() }.toRealmList()
                            })
                }
            else
                realmTorrentSearch.apply { ->
                    val newItems = dataList.map { TorrentItem(it, false) }
                            .map { it.toRealm() }
                            .filter { newItem -> torrentItems.firstOrNull { oldItem -> oldItem.name == newItem.name } == null }
                    realm.executeTransaction { torrentItems.addAll(newItems) }
                }
        }
    }

    override fun checkAllAsViewed(searchQuery: String) {
        Realm.getDefaultInstance().use {
            val realmTorrentSearch = it.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.QUERY, searchQuery)
                    .findFirst()

            it.executeTransaction { realmTorrentSearch?.torrentItems?.forEach { it.isViewed = true } }
        }
    }

    override fun subscribeSearch(searchQuery: String): Flowable<TorrentSearch> {
        return asFlowable { realm ->
            realm.where(RealmTorrentSearch::class.java)
                    .equalTo(RealmTorrentSearch.QUERY, searchQuery)
                    .findAllAsync()
        }
                .filter { it.isNotEmpty() }
                .map { it[0] }
                .map { it.fromRealm() }
                .subscribeOn(Schedulers.io())
    }

    override fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return asFlowable { realm ->
            realm.where(RealmTorrentSearch::class.java)
                    .findAllAsync()
        }
                .map { it.map { it.fromRealm() } }
                .subscribeOn(Schedulers.io())
    }

    private fun <T : RealmObject> asFlowable(query: (Realm) -> RealmResults<T>): Flowable<List<T>> {
        return Flowable.create({ emitter ->
            val realm = Realm.getDefaultInstance()
            val realmResults = query(realm)
            emitter.setDisposable(Disposables.fromAction {
                if (realmResults.isValid)
                    realmResults.removeAllChangeListeners()
                realm.close()
            })
            realmResults.addChangeListener { results ->
                if (results.isLoaded && !emitter.isCancelled) {
                    val search = results
                    if (!emitter.isCancelled)
                        emitter.onNext(ArrayList(search))
                }

            }
        }, BackpressureStrategy.LATEST)
    }
}

private fun <E : RealmObject> List<E>.toRealmList(): RealmList<E> {
    return RealmList<E>().apply {
        this@toRealmList.forEach { add(it) }
    }
}

fun TorrentItem.toRealm(): RealmTorrentItem {
    return RealmTorrentItem().apply {
        name = this@toRealm.item.name
        torrentUrl = this@toRealm.item.torrentUrl
        isViewed = this@toRealm.isViewed
    }
}

fun RealmTorrentSearch.fromRealm(): TorrentSearch {
    return TorrentSearch(this.searchQuery, ArrayList(this.torrentItems).map { it.fromRealm() })
}

fun RealmTorrentItem.fromRealm(): TorrentItem {
    return TorrentItem(TorrentData(this.name, this.torrentUrl), this.isViewed)
}

open class RealmTorrentSearch : RealmObject() {
    companion object {
        val QUERY = "searchQuery"
    }

    @PrimaryKey
    var searchQuery: String = ""
    var torrentItems: RealmList<RealmTorrentItem> = RealmList()

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
