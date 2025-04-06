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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    isLoading: Boolean, // ✅ 추가
    onStartListening: () -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onReset: () -> Unit, // ✅ 여기 추가!
    context: Context // ✅ Temi 이동을 위한 Context 추가

) {
    var selectedBook by remember { mutableStateOf<Book?>(null) } // ✅ 선택한 책 정보 저장
    var showResults by remember { mutableStateOf(false) } // ✅ 검색 후 결과 표시 여부 저장

    if (isLoading) {
        Dialog(onDismissRequest = { }) { // 백버튼 등으로 닫히지 않게
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.Red,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("로딩 중입니다...", fontSize = 16.sp)
                }
            }
        }
    }

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
//                        .offset(x = 300.dp, y = 100.dp) // 📌 위치 지정
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

//                            .offset(x = 10.dp, y = -350.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))

                    ) {
                        Column(

                            modifier = Modifier.padding(16.dp).heightIn(min = 500.dp)
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
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
                            ) {
                                Button(onClick = onPrevPage, enabled = currentPage > 1) {
                                    Text(text = "이전 페이지")
                                }

                                Text(
                                    text = "$currentPage / $totalPages",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                Button(onClick = onNextPage, enabled = currentPage < totalPages) {
                                    Text(text = "다음 페이지")
                                }
                            }

                            // ❌ **검색 결과 닫기 버튼**
                            Button(
                                onClick = {
                                    showResults = false
                                    selectedBook = null
                                    onReset() // ✅ 전체 상태 초기화
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,         // 🔴 배경 색상
                                    contentColor = Color.White          // 🔤 텍스트 색상
                                )
                            ) {
                                Text(text = "검색내용닫기", fontSize = 25.sp)
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
                val dialogHeight = min(screenHeight * 0.80f, 900.dp)

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
                                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
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

                                    StudyRoomInfo(
                                        studyRoom = selectedBook!!.studyRoom,
                                        author = selectedBook!!.author,
                                        publisher = selectedBook!!.publisher,
                                        code = selectedBook!!.code,
                                        context = context


                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // ✅ Temi 이동 버튼 추가된 위치 정보
                                   // BookLocationInfo(selectedBook!!.code, context)

                                    Spacer(modifier = Modifier.height(10.dp))
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


}



@Composable
fun StudyRoomInfo(
    studyRoom: String,
    author: String,
    publisher: String,
    code:String,
    context: Context
) {
    var showDialog by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
        ) {



            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleBox(title = "위치")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = studyRoom,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 작가 정보 줄
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleBox(title = "작가")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = author,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 출판사 정보 줄
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleBox(title = "출판사")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = publisher,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 코드정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleBox(title = "CODE")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = code,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            // 도서관 책 위치
            if (studyRoom != "[강서]어린이실") {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleBox1(title = "본 도서는 어린이실에 없습니다.")

                }
            }else{
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleBox1(title = "본 도서는 어린이실 위치하고 있습니다.")

                }


                //도서관 위치정보
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {

                        showDialog = true


                    } // ✅ 클릭 시 다이얼로그 표시
                ) {
                    TitleBox(title = "도서 위치정보")

                }


            }
        }



    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ){
            Surface(
                modifier = Modifier
                    .fillMaxSize()                 // ✅ 가로 전체 채우기
                    .width(700.dp)             // ✅ 세로는 내용만큼
                    .padding(30.dp),                 // ✅ 가장자리 여백
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()              // ✅ 내부도 가로 전체
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CODE 정보",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "이 책의 코드 값은 \"$code\" 입니다.")
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showDialog = false }) {
                        Text("닫기")
                    }
                    val temiController = TemiController(context)
                    // 💬 Temi가 말하도록
                    LaunchedEffect(Unit) {
                        temiController.speak("이 책의 코드 값은 $code 입니다.")
                    }
                }
            }
        }
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

        if (location != "알 수 없음") {
            Button(
//                onClick = { temiController.moveToLocation(location) },

                onClick = { temiController.speak("안내할 장소는 한성용 입니다. 입니다.") },
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // 버튼 배경색
                    contentColor = Color.White // 버튼 텍스트 색상
                )
            ) {
                Text(text = "정확한 위치 보기", fontSize = 18.sp)
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

@Composable
fun TitleBox(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null // nullable로 설정
) {
    val boxModifier = modifier
//        .padding(16.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF5F9FD1))
        .then(
            if (onClick != null) Modifier
                .clickable { onClick() }
                .shadow(6.dp, RoundedCornerShape(12.dp))
            else Modifier
        )
        .padding(horizontal = 20.dp, vertical = 10.dp)

    Box(modifier = boxModifier) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun TitleBox1(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null // nullable로 설정
) {
    val boxModifier = modifier
//        .padding(16.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFFF2593A))
        .then(
            if (onClick != null) Modifier
                .clickable { onClick() }
                .shadow(6.dp, RoundedCornerShape(12.dp))
            else Modifier
        )
        .padding(horizontal = 20.dp, vertical = 10.dp)

    Box(modifier = boxModifier) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}