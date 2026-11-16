package com.example.bookshelf

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookshelf.data.*

private val Context.dataStore by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private val vm: BookViewModel by viewModels { BookViewModel.Factory(PreferencesRepository(applicationContext.dataStore)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by vm.uiState.collectAsStateWithLifecycle()
            // Original preference: dark theme toggle is separate; here "hide low rated" is the original one.
            MaterialTheme { BookScreen(state, vm) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(state: BookUiState, vm: BookViewModel) {
    val p = state.prefs
    Scaffold(topBar = { TopAppBar(title = { Text("BookShelf") }) }) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 16.dp)) {
            Text("Show", style = MaterialTheme.typography.labelLarge)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReadFilter.entries.forEach {
                    FilterChip(p.readFilter == it, { vm.setReadFilter(it) }, { Text(it.name.lowercase().replaceFirstChar(Char::uppercase)) })
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Sort by", style = MaterialTheme.typography.labelLarge)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(SortOrder.TITLE to "Title", SortOrder.RATING to "Rating", SortOrder.DATE_ADDED to "Date added").forEach { (o, label) ->
                    FilterChip(p.sortOrder == o, { vm.setSortOrder(o) }, { Text(label) })
                }
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Hide books rated below $LOW_RATING_THRESHOLD", Modifier.weight(1f))
                Switch(p.hideLowRated, { vm.setHideLowRated(it) })
            }
            HorizontalDivider()
            if (state.books.isEmpty()) Text("No books match.", Modifier.padding(16.dp))
            LazyColumn {
                items(state.books, key = { it.id }) { b ->
                    ListItem(
                        headlineContent = { Text(b.title) },
                        supportingContent = { Text("${b.author} · ★ ${b.rating} · added ${b.dateAdded}") },
                        trailingContent = { Text(if (b.isRead) "Read" else "Unread") },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
