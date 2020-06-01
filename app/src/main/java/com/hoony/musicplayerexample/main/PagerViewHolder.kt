package com.hoony.musicplayerexample.main

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoony.musicplayerexample.data.Album
import com.hoony.musicplayerexample.databinding.ItemAlbumBinding

class PagerViewHolder(private val binding: ItemAlbumBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(album: Album) {
        binding.album = album
        if (album.uri != null) {
            binding.ivAlbumArt.visibility = View.VISIBLE

            Glide.with(binding.root)
                .load(album.uri)
                .into(binding.ivAlbumArt)
        } else {
            binding.ivAlbumArt.visibility = View.INVISIBLE
        }
    }
}