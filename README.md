# BookShelf

Kotlin + Jetpack Compose app that lists 8 books. Filter (All/Read/Unread), sort (Title/Rating/Date added), and an original preference: **hide books rated below 3.5**. All three are saved with Preferences DataStore.

Build: open in Android Studio (JDK 17+) and run, or `./gradlew :app:assembleDebug`.

## Architecture
- `data/PreferencesRepository.kt` – only place that touches DataStore. Exposes `preferences: Flow<UserPreferences>`; writes via `suspend` functions. `IOException` while reading -> defaults (`emptyPreferences()`); any other exception is rethrown.
- `BookViewModel.kt` – maps the preferences Flow to `BookUiState` (prefs + filtered/sorted books) as a `StateFlow`.
- `MainActivity.kt` – Compose UI collects the state with `collectAsStateWithLifecycle()`.

## Defaults (fresh install)
Filter = All, Sort = Title, Hide low-rated = off.

## Demo script
1. Tap Unread, Rating, switch on "Hide low rated": the list updates immediately.
2. Swipe the app away and reopen: the chips/switch and list are unchanged.
3. Clear data (`adb shell pm clear com.example.bookshelf` or uninstall/reinstall): defaults appear.
4. Explain: the UI never reads DataStore directly. Tapping a control calls a ViewModel function -> repository `suspend` write -> DataStore emits a new `Preferences` snapshot -> the repository Flow -> ViewModel `StateFlow` -> Compose recomposes. `DataStore.edit {}` is transactional: the block runs serially against the current snapshot, and the result is committed atomically to disk (write to temp file, then rename), so readers see either the old or the new full state, never a partial one, and concurrent edits can't clobber each other.
