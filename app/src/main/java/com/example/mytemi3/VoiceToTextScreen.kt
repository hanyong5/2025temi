package com.example.mytemi3


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.mytemi3.ui.Book
import com.example.mytemi3.ui.BookItem
@Composable
fun VoiceToTextScreen(
    spokenText: String,
    books: List<Book>,
    currentPage: Int,
    totalPages: Int,
    onStartListening: () -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    context: Context // ✅ Temi 이동을 위한 Context 추가
) {
    var selectedBook by remember { mutableStateOf<Book?>(null) } // ✅ 선택한 책 정보 저장
    var showResults by remember { mutableStateOf(false) } // ✅ 검색 후 결과 표시 여부 저장

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.background_image01),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔍 **음성 인식 시작 버튼**
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(77.dp)
                    .clickable {
                        onStartListening()
                        showResults = true
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_icon01),
                    contentDescription = "도서검색하기",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(text = spokenText, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // 🔍 **검색 결과 표시**
            if (showResults) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // 📚 **책 목록 가로 스크롤**
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            items(books.take(5)) { book ->
                                BookItem(
                                    book = book,
                                    onClick = { selectedBook = book }
                                )
                            }
                        }

                        // 📄 **페이지 네이션**
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(26.dp),
                        ) {
                            Button(onClick = onPrevPage, enabled = currentPage > 1) {
                                Text(text = "이전 페이지")
                            }

                            Text(text = "$currentPage / $totalPages", fontSize = 18.sp, color = Color.Black)

                            Button(onClick = onNextPage, enabled = currentPage < totalPages) {
                                Text(text = "다음 페이지")
                            }
                        }

                        // ❌ **검색 결과 닫기 버튼**
                        Button(
                            onClick = { showResults = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = "검색 결과 닫기", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }

    // 📖 **선택한 책 정보 다이얼로그**
    if (selectedBook != null) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val dialogWidth = min(screenWidth * 0.90f, 1000.dp)
        val dialogHeight = min(screenHeight * 0.70f, 800.dp)

        Dialog(
            onDismissRequest = { selectedBook = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(dialogWidth)
                        .height(dialogHeight)
                        .padding(30.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp)
                    ) {
                        val imageUrl = selectedBook!!.bImg.replace("http://", "https://")
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "책 표지",
                            modifier = Modifier
                                .width(250.dp)
                                .height(400.dp)
                                .shadow(4.dp, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(24.dp))

                        Column(
                            modifier = Modifier.weight(1f).padding(16.dp)
                        ) {
                            Text(
                                text = selectedBook!!.bookname,
                                fontSize = 40.sp,
                                color = Color.Black,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            StudyRoomInfo(selectedBook!!.studyRoom)
                            Spacer(modifier = Modifier.height(20.dp))

                            // ✅ Temi 이동 버튼 추가된 위치 정보
                            BookLocationInfo(selectedBook!!.code, context)

                            Button(
                                onClick = { selectedBook = null },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(text = "닫기", fontSize = 25.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StudyRoomInfo(studyRoom: String) {
    if (studyRoom == "[강서]어린이실") {
        Button(
            onClick = { /* 버튼 클릭 시 동작 추가 가능 */ },
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Magenta, // 버튼 배경색
                contentColor = Color.White  // 버튼 내부 글자색
            )
        ) {
            Text(
                text = "어린이실",
                fontSize = 20.sp
            )
        }
    } else {
        Text(
            text = "위치정보: $studyRoom",
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun BookLocationInfo(bookCode: String, context: Context) {
    // 코드 값을 기반으로 location 및 row 설정
    val (location, row) = getLocationAndRow(bookCode)
    val temiController = TemiController(context)

    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = "위치: $location, 줄: $row",
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ Temi 이동 버튼 추가 (location이 "알 수 없음"이 아닐 경우만 표시)
        if (location != "알 수 없음") {
            Button(
                onClick = { temiController.moveToLocation(location) },
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green, // 버튼 배경색
                    contentColor = Color.White // 버튼 텍스트 색상
                )
            ) {
                Text(text = "Temi로 이동", fontSize = 18.sp)
            }
        }
    }
}

// 코드 값을 분석하여 location과 row를 반환하는 함수
fun getLocationAndRow(code: String): Pair<String, String> {
    return when (code) {
        "아457-ㄷ57ㄱ=2" -> "입구" to "3줄"
        else -> "알 수 없음" to "알 수 없음"
    }
}

