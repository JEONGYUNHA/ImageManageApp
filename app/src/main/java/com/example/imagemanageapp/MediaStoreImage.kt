package com.example.imagemanageapp

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.Date

data class MediaStoreImage(
    val id: Int,
    val title: String,
    val path: String,
    val date: Long,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MediaStoreImage>() {
            override fun areItemsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage) =
                oldItem == newItem
        }
    }
}