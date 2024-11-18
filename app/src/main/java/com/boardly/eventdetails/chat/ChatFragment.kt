package com.boardly.eventdetails.chat

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.common.utils.MessagesAdapterObserver
import com.boardly.constants.EVENT_ID
import com.boardly.databinding.FragmentChatBinding
import com.boardly.eventdetails.chat.list.MessagesAdapter
import com.boardly.extensions.getCurrentISODate
import com.boardly.factories.ChatViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ChatFragment : Fragment(), ChatView {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    private var isBatchLoading = false
    private val messagesAdapter = MessagesAdapter()

    private var eventId = ""

    private var init = true

    private lateinit var newMessagesListenerToggleSubject: PublishSubject<Boolean>
    private lateinit var batchFetchTriggerSubject: PublishSubject<String>

    private lateinit var binding: FragmentChatBinding

    private val messagesAdapterObserver: MessagesAdapterObserver by lazy {
        MessagesAdapterObserver { lastItemPosition ->
            if (lastItemPosition == 0) {
                isBatchLoading = false
            } else {
                binding.messagesRecyclerView.scrollToPosition(lastItemPosition)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true

        binding.messagesRecyclerView.layoutManager = layoutManager
        binding.messagesRecyclerView.adapter = messagesAdapter

        binding.messagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(-1) && !isBatchLoading) {
                    isBatchLoading = true
                    batchFetchTriggerSubject.onNext(messagesAdapter.getLastTimestamp())
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        chatViewModel.bind(this, eventId)
        messagesAdapter.registerAdapterDataObserver(messagesAdapterObserver)

        if (init) {
            newMessagesListenerToggleSubject.onNext(true)
            batchFetchTriggerSubject.onNext(getCurrentISODate())
            init = false
        }
    }

    private fun initEmitters() {
        newMessagesListenerToggleSubject = PublishSubject.create()
        batchFetchTriggerSubject = PublishSubject.create()
    }

    override fun onStop() {
        chatViewModel.unbind()
        messagesAdapter.unregisterAdapterDataObserver(messagesAdapterObserver)
        super.onStop()
    }

    override fun onDestroy() {
        newMessagesListenerToggleSubject.onNext(false)
        super.onDestroy()
    }

    override fun render(chatViewState: ChatViewState) {
        with(chatViewState) {
            showProgressBar(progress)
            showIncentiveTextView(messagesList.isEmpty() && !progress)
            messagesAdapter.submitList(messagesList)
        }
    }

    private fun showIncentiveTextView(show: Boolean) {
        binding.incentiveTextView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            binding.messagesRecyclerView.visibility = View.GONE
            binding.messageInputEditText.visibility = View.GONE
            binding.sendMessageButton.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.messagesRecyclerView.visibility = View.VISIBLE
            binding.messageInputEditText.visibility = View.VISIBLE
            binding.sendMessageButton.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun newMessagesListenerToggleEmitter(): Observable<Boolean> =
        newMessagesListenerToggleSubject

    override fun batchFetchTriggerEmitter(): Observable<String> = batchFetchTriggerSubject

    override fun messageEmitter(): Observable<String> {
        return RxView.clicks(binding.sendMessageButton)
            .map { binding.messageInputEditText.text.toString() }
            .doOnNext { binding.messageInputEditText.setText("") }
    }
}