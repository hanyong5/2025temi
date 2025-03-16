package com.example.mytemi3.ui

data class BookResponse(
    val keyword: String = "",
    val books: List<Book> = emptyList(),  // 최상위 필드
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalItems: Int = 0
)

data class Book(
    val id: Int = 0,
    val code: String = "정보 없음",
    val bookname: String = "제목 없음",
    val author: String = "저자 미상",
    val publisher: String = "출판사 정보 없음",
    val year: Int = 0,
    val loc: String = "위치 정보 없음",
    val studyRoom: String = "열람실 정보 없음",
    val bImg: String = "https://via.placeholder.com/150",
    val state: String = "대출 정보 없음"
)
