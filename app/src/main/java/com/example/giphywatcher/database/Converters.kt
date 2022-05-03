package com.example.giphywatcher.database

import androidx.room.TypeConverter
import com.example.giphywatcher.network.parseModels.Images
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun imagesToUrl(imagesData: Images?): String? {
        return Gson().toJson(imagesData)
    }

    @TypeConverter
    fun urlToImages(json: String?): Images? {
        return Gson().fromJson(json, Images::class.java)
    }
}