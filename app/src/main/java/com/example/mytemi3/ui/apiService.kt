package com.example.mytemi3.ui


import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // 음성 인식 후 첫 검색 요청
    @GET("chat_book_search")
    suspend fun searchBook(
        @Query("chat") chat: String
    ): BookResponse

    // 페이지 네이션 시 사용 (이전 keyword 유지)
    @GET("keyword_book_search")
    suspend fun searchBookByKeyword(
        @Query("keyword") keyword: String,
        @Query("pageNum") pageNum: Int,
    ): BookResponse
}