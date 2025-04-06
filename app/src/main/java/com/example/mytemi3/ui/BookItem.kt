package com.example.mytemi3.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter


@Composable
fun BookItem(book: Book, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .width(310.dp)
            .padding(10.dp)
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },  // 클릭 이벤트 추가
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            // 이미지 로딩
//            val imageUrl = if (book.bImg.isNotEmpty()) book.bImg else "https://via.placeholder.com/150"
            val imageUrl = book.bImg.replace("http://", "https://")
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Book Cover",
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 책 제목
            Text(
                text = book.bookname,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                textAlign = TextAlign.Left
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 저자 정보
            Text(
                text = "[저자] ${book.author}",
                fontSize = 18.sp,
                maxLines = 1,
                textAlign = TextAlign.Left
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 위치 정보
            Text(
                text = "[위치] ${book.studyRoom}",
                fontSize = 18.sp,
                maxLines = 1,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(4.dp))

            // 출판사 정보
            Text(
                text = "[출판사] ${book.publisher}",
                fontSize = 18.sp,
                maxLines = 1,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(4.dp))

            // 상태 정보
            Text(
                text = "[상태] ${book.state}",
                fontSize = 18.sp,
                textAlign = TextAlign.Left
            )
        }
    }
}
