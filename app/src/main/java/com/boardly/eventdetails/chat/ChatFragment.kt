package com.boardly.eventdetails.chat

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.utils.MessagesAdapterObserver
import com.boardly.constants.EVENT_ID
import com.boardly.eventdetails.chat.list.MessagesAdapter
import com.boardly.factories.ChatViewModelFactory
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_chat.messageInputEditText
import kotlinx.android.synthetic.main.fragment_chat.messagesRecyclerView
import kotlinx.android.synthetic.main.fragment_chat.progressBar
import kotlinx.android.synthetic.main.fragment_chat.sendMessageButton
import javax.inject.Inject

class ChatFragment : Fragment(), ChatView {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    private var isBatchLoading = false
    private val messagesAdapter = MessagesAdapter()

    private var eventId = ""

    private val messagesAdapterObserver: MessagesAdapterObserver by lazy {
        MessagesAdapterObserver { lastItemPosition ->
            if (lastItemPosition == 0) {
                isBatchLoading = false
            } else {
                messagesRecyclerView.scrollToPosition(lastItemPosition)
            }
        }
    }

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
        eventId = arguments?.getString(EVENT_ID) ?: ""

        chatViewModel = ViewModelProviders.of(this, chatViewModelFactory)[ChatViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true

        messagesRecyclerView.layoutManager = layoutManager
        messagesRecyclerView.adapter = messagesAdapter

        messagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(-1) && !isBatchLoading) {
                    isBatchLoading = true
                    // here you should emit batch trigger with last timestamp key
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        chatViewModel.bind(this)
        messagesAdapter.registerAdapterDataObserver(messagesAdapterObserver)
    }

    private fun initEmitters() {

    }

    override fun onStop() {
        chatViewModel.unbind()
        messagesAdapter.unregisterAdapterDataObserver(messagesAdapterObserver)
        super.onStop()
    }

    override fun render(chatViewState: ChatViewState) {
        with(chatViewState) {
            showProgressBar(progress)
            messagesAdapter.submitList(messagesList)
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            messagesRecyclerView.visibility = View.GONE
            messageInputEditText.visibility = View.GONE
            sendMessageButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            messagesRecyclerView.visibility = View.VISIBLE
            messageInputEditText.visibility = View.VISIBLE
            sendMessageButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}