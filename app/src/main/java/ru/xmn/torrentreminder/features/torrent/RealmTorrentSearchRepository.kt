package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class RealmTorrentSearchRepository : TorrentSearchRepository {
    @Synchronized
    override fun delete(result: String) {
    }

    @Synchronized
    override fun insertOrUpdate(searchString: String, dataList: List<TorrentData>) {
    }

    @Synchronized
    override fun checkAllAsViewed() {
    }

    override fun subscribeSearch(query: String): Flowable<TorrentSearch> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class RealmTorrentSearch : RealmObject() {
    @PrimaryKey
    var query: String? = null
    var torrentItems: RealmList<RealmTorrentItem> = RealmList()
}

class RealmTorrentItem : RealmObject() {
    var name: String? = null
    var torrentUrl: String? = null
    var isViwed: Boolean? = null
}
