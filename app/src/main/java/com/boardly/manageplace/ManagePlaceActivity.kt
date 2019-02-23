package com.boardly.manageplace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseDrawerActivity

class ManagePlaceActivity : BaseDrawerActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ManagePlaceActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_manage_place)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.manage_place_item)
    }
}