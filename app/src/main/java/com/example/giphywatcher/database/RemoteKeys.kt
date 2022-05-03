package com.example.giphywatcher.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remoteKeys")
data class RemoteKeys(
    @PrimaryKey
    @NonNull
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)