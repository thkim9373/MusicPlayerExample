package com.hoony.musicplayerexample.main

import androidx.recyclerview.widget.RecyclerView
import com.hoony.musicplayerexample.BR
import com.hoony.musicplayerexample.data.Music
import com.hoony.musicplayerexample.databinding.ItemMusicBinding

class MusicItemViewHolder(private val binding: ItemMusicBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(music: Music) {
        binding.music = music

        binding.tvNumber.text = (adapterPosition + 1).toString()

        val min = music.duration / 60000
        val sec = (music.duration % 60000) / 1000
        val durationString = String.format("%d:%02d", min, sec)

        binding.tvPlayTime.text = durationString

        binding.setVariable(BR.music, music)
        binding.executePendingBindings()
    }

    fun setClickListener(listener: MusicListAdapter.OnItemClickListener) {
        binding.clContainer.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}