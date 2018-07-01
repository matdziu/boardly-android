package com.boardly.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.boardly.R
import com.boardly.editprofile.EditProfileActivity
import com.boardly.home.HomeActivity

@SuppressLint("Registered")
open class BaseDrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout: DrawerLayout by lazy {
        findViewById<DrawerLayout>(R.id.drawerLayout)
    }
    private val navigationView: NavigationView by lazy {
        findViewById<NavigationView>(R.id.navigationView)
    }

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_opened, R.string.drawer_closed)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return if (!item.isChecked) {
            when (item.itemId) {
                R.id.sign_out_item -> signOut()
                R.id.profile_item -> EditProfileActivity.start(this, true)
                R.id.events_item -> startActivity(Intent(this, HomeActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        } else {
            false
        }
    }

    fun setNavigationSelection(menuItemId: Int) {
        navigationView.setCheckedItem(menuItemId)
    }

    override fun showBackToolbarArrow(show: Boolean, backAction: () -> Unit) {
        if (show) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toggle.setToolbarNavigationClickListener { backAction() }
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            toggle.isDrawerIndicatorEnabled = true
        }
    }

    fun showHamburgerIcon(show: Boolean) {
        if (show) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            toggle.isDrawerIndicatorEnabled = true
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
        }
    }
}