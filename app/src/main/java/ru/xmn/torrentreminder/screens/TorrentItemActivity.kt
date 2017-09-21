package ru.xmn.torrentreminder.screens

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View

import kotlinx.android.synthetic.main.torrent_list.*
import ru.xmn.common.adapter.TorrentItem
import ru.xmn.common.adapter.TorrentItemAdapter
import ru.xmn.torrentreminder.R


class TorrentItemActivity : AppCompatActivity() {

    val list = ArrayList<TorrentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torrent_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        init()

        val recycleView: RecyclerView = findViewById<View>(R.id.list) as RecyclerView
        val adapter = TorrentItemAdapter(this, list)

        recycleView.adapter = adapter
    }

    fun init(){
        (1..20).mapTo(list) { TorrentItem("Фильм $it", "Обновлений для фильма $it нет") }
    }

}