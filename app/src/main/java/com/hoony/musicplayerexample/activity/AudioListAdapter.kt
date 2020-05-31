package com.hoony.musicplayerexample.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.data.Album

class AudioListAdapter(private val albumList: List<Album>) : RecyclerView.Adapter<AudioListItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioListItem {
        return AudioListItem(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_album,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    override fun onBindViewHolder(holder: AudioListItem, position: Int) {
        val album = albumList[position]
        holder.binding.let {
            it.tvArtist.text = album.artist
            it.tvNumOfSong.text = album.numOfSong.toString()

            if (album.uri != null) {
                it.ivAlbumArt.visibility = View.VISIBLE
                Glide.with(it.root.context)
                    .load(album.uri)
                    .thumbnail(0.5f)
                    .centerCrop()
                    .into(it.ivAlbumArt)
            } else {
                it.ivAlbumArt.visibility = View.INVISIBLE
            }
        }
    }
}