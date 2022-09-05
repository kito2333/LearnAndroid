package com.example.helloworld.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.helloworld.data.Book

@Dao
interface BookDao {
    @Insert
    fun insertBook(book: Book): Long

    @Query("select * from Book")
    fun loadAllBooks(): List<Book>
}