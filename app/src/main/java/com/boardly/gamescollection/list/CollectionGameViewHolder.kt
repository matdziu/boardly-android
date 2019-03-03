package com.boardly.gamescollection.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.View
import com.boardly.R
import com.boardly.constants.RPG_TYPE
import com.boardly.extensions.clearFromType
import com.boardly.extensions.isOfType
import com.boardly.extensions.loadImageFromUrl
import com.boardly.gamescollection.GamesCollectionView
import com.boardly.gamescollection.Mode
import com.boardly.gamescollection.models.CollectionGame
import kotlinx.android.synthetic.main.item_collection_game.view.boardGameImageView
import kotlinx.android.synthetic.main.item_collection_game.view.gameNameTextView

class CollectionGameViewHolder(itemView: View, private val mode: Mode) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

    private lateinit var gameId: String

    fun bind(collectionGame: CollectionGame) = with(itemView) {
        gameId = collectionGame.id
        gameNameTextView.text = collectionGame.name
        context.loadImageFromUrl(boardGameImageView, collectionGame.imageUrl, R.drawable.board_game_placeholder)
        if (mode == Mode.VIEW) {
//            setOnClickListener { openBoardGameInfoPage(collectionGame.id, context) }
        } else if (mode == Mode.MANAGE) {
            itemView.setOnCreateContextMenuListener(this@CollectionGameViewHolder)
        }
    }

    private fun openBoardGameInfoPage(gameId: String, context: Context) {
        with(context) {
            val endpoint = if (gameId.isOfType(RPG_TYPE)) "rpg/${gameId.clearFromType(RPG_TYPE)}" else "boardgame/$gameId"
            val infoPageIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://boardgamegeek.com/$endpoint"))
            startActivity(infoPageIntent)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.setHeaderTitle(R.string.select_game_action)
        menu.add(0, 0, 0, R.string.game_action_delete)
                .setOnMenuItemClickListener {
                    (view.context as GamesCollectionView).emitGameDeletion(gameId)
                    true
                }
    }
}