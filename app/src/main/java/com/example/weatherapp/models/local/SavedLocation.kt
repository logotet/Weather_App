package com.example.weatherapp.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_locations")
class SavedLocation(
    @PrimaryKey(autoGenerate = false)
    val name: String,
)