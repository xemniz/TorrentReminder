package ru.xmn.torrentreminder.screens.torrentsearch.searchlist

import org.jetbrains.anko.*
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.item_torrent_search.view.*
import ru.xmn.common.extensions.*
import ru.xmn.common.ui.adapter.AutoUpdatableAdapter
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.screens.torrentlist.TorrentListActivity
import kotlin.properties.Delegates


class TorrentSearchListAdapter(val torrentSearchStart: (String, String) -> Unit, val deleteItem: (String) -> Unit) : RecyclerView.Adapter<TorrentSearchListAdapter.ViewHolder>(), AutoUpdatableAdapter {
    var items by Delegates.observable(emptyList<TorrentSearch>()) { property, oldValue, newValue ->
        autoNotify(oldValue, newValue) { a, b -> a.id == b.id }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], torrentSearchStart, deleteItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.item_torrent_search))

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var views: List<View>

        fun bind(torrentSearch: TorrentSearch, torrentSearchStart: (String, String) -> Unit, deleteItem: (String) -> Unit) {
            with(itemView) {
                views = listOf(torrentNameEditor, torrentNameEditorButton, torrentName, torrentUpdatedInfo)
                torrentDeleteItem.setOnClickListener {
                    deleteItem(torrentSearch.id)
                }
                when {
                    torrentSearch.searchQuery == "" -> bindAsNewSearch(torrentSearch, torrentSearchStart)
                    else -> bindAsCommonSearch(torrentSearch)
                }
            }
        }

        private fun View.bindAsCommonSearch(torrentSearch: TorrentSearch) {
            card_view.setOnClickListener {
                context.startActivity<TorrentListActivity>(TorrentListActivity.ID to torrentSearch.id)
            }
            views.visibleOnly(torrentName, torrentUpdatedInfo)
            torrentUpdatedInfo.text = context.getString(R.string.item_updated_info, torrentSearch.lastSearchedItems.size, torrentSearch.lastSearchedItems.filter { !it.isViewed }.size)

            torrentName.text = torrentSearch.searchQuery
        }

        private fun View.bindAsNewSearch(torrentSearch: TorrentSearch, torrentSearchStart: (String, String) -> Unit) {
            views.visibleOnly(torrentNameEditor)
            updateTorrentNameEditorButton()

            torrentNameEditorButton.setOnClickListener {
                torrentSearchStart(torrentSearch.id, torrentNameEditor.text.toString())
                hideKeyboard()
            }

            torrentNameEditor.apply {
                setText(torrentSearch.searchQuery)
                setOnEditorActionListener(object : TextView.OnEditorActionListener {
                    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                            torrentSearchStart(torrentSearch.id, torrentNameEditor.text.toString())
                            hideKeyboard()
                        }
                        return false
                    }
                })
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        updateTorrentNameEditorButton()
                    }

                })
                onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(p0: View?, p1: Boolean) {
                        if (!p1)
                            hideKeyboard()
                    }

                }
            }

            //задержка нужна, чтобы ресайклер успел доскроллиться вверх.
            Handler().postDelayed({
                torrentNameEditor.isFocusable = true;
                torrentNameEditor.isFocusableInTouchMode = true;
                torrentNameEditor.requestFocus()
                torrentNameEditor.showKeyboard()
            }, 200)
        }

        private fun updateTorrentNameEditorButton() {
            with(itemView){
                if (torrentNameEditor.text.length > 2)
                    torrentNameEditorButton.visible() else torrentNameEditorButton.invisible()
            }
        }
    }
}

