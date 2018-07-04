package com.boardly.base

import android.app.SearchManager
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.boardly.R
import io.reactivex.subjects.PublishSubject

abstract class BaseSearchActivity : BaseActivity() {

    abstract val searchHintResId: Int

    protected var searchInput = PublishSubject.create<String>()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenu = menu?.findItem(R.id.search)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(searchHintResId)
        searchView.requestFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val formattedQuery = query.trim().toLowerCase()
                if (formattedQuery.isNotBlank()) searchInput.onNext(formattedQuery)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        searchMenu.expandActionView()
        searchMenu.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                finish()
                hideSoftKeyboard()
                return false
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }
        })

        return true
    }
}