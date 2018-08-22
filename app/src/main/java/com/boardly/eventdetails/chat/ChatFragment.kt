package com.boardly.eventdetails.chat

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.constants.EVENT_ID
import com.boardly.factories.ChatViewModelFactory
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ChatFragment : Fragment(), ChatView {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    companion object {
        fun newInstance(eventId: String): ChatFragment {
            val chatFragment = ChatFragment()
            val arguments = Bundle()
            arguments.putString(EVENT_ID, eventId)
            chatFragment.arguments = arguments
            return chatFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        chatViewModel = ViewModelProviders.of(this, chatViewModelFactory)[ChatViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        chatViewModel.bind(this)
    }

    private fun initEmitters() {

    }

    override fun onStop() {
        chatViewModel.unbind()
        super.onStop()
    }

    override fun render(chatViewState: ChatViewState) {

    }
}