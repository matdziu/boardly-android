package com.boardly.home

import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.editprofile.EditProfileActivity

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, EditProfileActivity::class.java))
    }
}