package com.example.mytemi3.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter





@Composable
fun VoiceToTextScreen(
    spokenText: String,
    books: List<Book>,
    currentPage: Int,
    totalPages: Int,
    onStartListening: () -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onStartListening) {
            Text(text = "음성 인식 시작")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = spokenText, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // 책 목록 가로 스크롤
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(books) { book ->
                BookItem(
                    book = book,
                    onClick = {
                        println("클릭한 책: ${book.bookname}")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 페이지 네이션
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onPrevPage, enabled = currentPage > 1) {
                Text(text = "이전 페이지")
            }

            Text(text = "$currentPage / $totalPages", fontSize = 18.sp)

            Button(onClick = onNextPage, enabled = currentPage < totalPages) {
                Text(text = "다음 페이지")
            }
        }
    }
}
