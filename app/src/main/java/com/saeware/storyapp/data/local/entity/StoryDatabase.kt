package com.saeware.storyapp.data.local.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saeware.storyapp.data.local.room.RemoteKeysDao
import com.saeware.storyapp.data.local.room.StoryDao

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}