package com.example.bookshelf.data

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val rating: Double,
    val dateAdded: Long, // epoch day; larger = added more recently
    val isRead: Boolean,
)

val sampleBooks = listOf(
    Book(1, "To Kill a Mockingbird", "Harper Lee", 4.8, 20250105, true),
    Book(2, "1984", "George Orwell", 4.7, 20250212, true),
    Book(3, "The Hobbit", "J.R.R. Tolkien", 4.6, 20250320, false),
    Book(4, "Dune", "Frank Herbert", 4.5, 20250411, false),
    Book(5, "Pride and Prejudice", "Jane Austen", 4.3, 20250502, true),
    Book(6, "The Catcher in the Rye", "J.D. Salinger", 3.1, 20250618, false),
    Book(7, "Moby-Dick", "Herman Melville", 2.9, 20250703, false),
    Book(8, "The Great Gatsby", "F. Scott Fitzgerald", 3.9, 20250821, true),
)
