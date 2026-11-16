package com.example.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val LOW_RATING_THRESHOLD = 3.5

data class BookUiState(val prefs: UserPreferences = UserPreferences(), val books: List<Book> = emptyList())

class BookViewModel(private val repo: PreferencesRepository) : ViewModel() {

    /** Every DataStore change re-emits here, so the list is recomputed and the UI recomposes. */
    val uiState: StateFlow<BookUiState> = repo.preferences
        .map { BookUiState(it, applyPreferences(sampleBooks, it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookUiState(books = applyPreferences(sampleBooks, UserPreferences())))

    fun setReadFilter(f: ReadFilter) = viewModelScope.launch { repo.setReadFilter(f) }
    fun setSortOrder(o: SortOrder) = viewModelScope.launch { repo.setSortOrder(o) }
    fun setHideLowRated(h: Boolean) = viewModelScope.launch { repo.setHideLowRated(h) }

    class Factory(private val repo: PreferencesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = BookViewModel(repo) as T
    }
}

fun applyPreferences(books: List<Book>, p: UserPreferences): List<Book> {
    val filtered = books
        .filter {
            when (p.readFilter) {
                ReadFilter.ALL -> true
                ReadFilter.READ -> it.isRead
                ReadFilter.UNREAD -> !it.isRead
            }
        }
        .filter { !p.hideLowRated || it.rating >= LOW_RATING_THRESHOLD }
    return when (p.sortOrder) {
        SortOrder.TITLE -> filtered.sortedBy { it.title.lowercase() }
        SortOrder.RATING -> filtered.sortedByDescending { it.rating }
        SortOrder.DATE_ADDED -> filtered.sortedByDescending { it.dateAdded }
    }
}
