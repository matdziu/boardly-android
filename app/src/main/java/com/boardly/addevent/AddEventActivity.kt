package com.boardly.addevent

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.factories.AddEventViewModelFactory
import com.boardly.pickgame.PickGameActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_event.pickCityButton
import javax.inject.Inject

class AddEventActivity : BaseActivity(), AddEventView {

    @Inject
    lateinit var addEventViewModelFactory: AddEventViewModelFactory

    private lateinit var addEventViewModel: AddEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_add_event)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        addEventViewModel = ViewModelProviders.of(this, addEventViewModelFactory)[AddEventViewModel::class.java]
        pickCityButton.setOnClickListener { startActivity(Intent(this, PickGameActivity::class.java)) }
    }

    override fun onStart() {
        super.onStart()
        addEventViewModel.bind(this)
    }

    override fun onStop() {
        addEventViewModel.unbind()
        super.onStop()
    }

    override fun render(addEventViewState: AddEventViewState) {

    }
}