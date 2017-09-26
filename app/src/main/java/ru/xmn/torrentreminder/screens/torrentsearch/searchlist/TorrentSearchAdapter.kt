package ru.xmn.torrentreminder.screens.torrentsearch.searchlist

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import kotlinx.android.synthetic.main.torrent_search_item.view.*
import ru.xmn.common.extensions.*
import ru.xmn.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import kotlin.properties.Delegates


class TorrentSearchAdapter(val torrentSearchStart: (String, String) -> Unit, val deleteItem: (String) -> Unit) : RecyclerView.Adapter<TorrentSearchAdapter.ViewHolder>(), AutoUpdatableAdapter {
    var items by Delegates.observable(emptyList<TorrentSearch>()) { property, oldValue, newValue ->
        autoNotify(oldValue, newValue) { a, b -> a.id == b.id }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], torrentSearchStart, deleteItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.torrent_search_item))

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(torrentSearch: TorrentSearch, torrentSearchStart: (String, String) -> Unit, deleteItem: (String) -> Unit) {
            with(itemView) {
                torrentDeleteItem.setOnClickListener {
                    deleteItem(torrentSearch.id)
                }
                torrentNameEditorButton.setOnClickListener {
                    torrentSearchStart(torrentSearch.id, torrentNameEditor.text.toString())
                }
                torrentName.text = torrentSearch.searchQuery
                torrentNameEditor.setText(torrentSearch.searchQuery)
                torrentNameEditor.setOnEditorActionListener(object : OnEditorActionListener {
                    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                            torrentSearchStart(torrentSearch.id, torrentNameEditor.text.toString())
                            hideKeyboard()
                        }
                        return false
                    }
                })
                torrentNameEditor.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(p0: View?, p1: Boolean) {
                        if (!p1)
                            hideKeyboard()
                    }

                }
                torrentUpdatedInfo.text = when (torrentSearch.hasUpdates) {
                    true -> "Есть обновления ${torrentSearch.lastSearchedItems.size}"
                    false -> "Обновлений нет"
                }

                when (torrentSearch.searchQuery == "") {
                    true -> {
                        torrentNameEditor.visible()
                        torrentNameEditorButton.visible()
                        torrentName.invisible()
                        torrentUpdatedInfo.invisible()

                        //задержка нужна, чтобы ресайклер успел доскроллиться вверх.
                        Handler().postDelayed({
                            torrentNameEditor.isFocusable = true;
                            torrentNameEditor.isFocusableInTouchMode = true;
                            torrentNameEditor.requestFocus()
                            torrentNameEditor.showKeyboard()
                        }, 200)
                    }
                    false -> {
                        torrentName.visible()
                        torrentNameEditorButton.invisible()
                        torrentNameEditor.invisible()
                        torrentUpdatedInfo.visible()
                    }
                }
            }
        }
    }
}

