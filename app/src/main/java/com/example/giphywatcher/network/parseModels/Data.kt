package com.example.giphywatcher.network.parseModels

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import javax.annotation.Nonnull

@Entity(tableName = "giphyData")
data class Data(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "url") val images: Images?,
    @ColumnInfo(name = "title") val title: String?
)