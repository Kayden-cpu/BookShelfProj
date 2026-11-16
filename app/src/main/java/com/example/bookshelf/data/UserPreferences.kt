package com.example.bookshelf.data

enum class ReadFilter { ALL, READ, UNREAD }
enum class SortOrder { TITLE, RATING, DATE_ADDED }

/** Defaults are used on a fresh install and when DataStore cannot be read (IOException). */
data class UserPreferences(
    val readFilter: ReadFilter = ReadFilter.ALL,
    val sortOrder: SortOrder = SortOrder.TITLE,
    val hideLowRated: Boolean = false, // original preference: hide books rated below 3.5
)
