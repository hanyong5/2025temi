package com.example.mytemi3


import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mytemi3.ui.ApiService
import com.example.mytemi3.ui.Book
import com.robotemi.sdk.Robot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*




class MainActivity : ComponentActivity() {

    private var recognizedText by mutableStateOf("버튼을 눌러 말하세요.")
    private var bookList = mutableStateListOf<Book>()
    private var currentPage = mutableStateOf(1)
    private var totalPages = mutableStateOf(1)
    private var savedKeyword = mutableStateOf("") // 이전 키워드 저장
    val responseType = mutableStateOf(0)
    val responseMessage = mutableStateOf("")

    val isLoading = mutableStateOf(false) //로딩


    // Google STT Launcher
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                Log.d("GoogleSTT", "인식된 텍스트: $spokenText")

                recognizedText = spokenText
                currentPage.value = 1 // 페이지 초기화
                savedKeyword.value = spokenText

                // 서버에서 책 검색
                fetchDataFromServer(spokenText)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //최종삭제
        recognizedText = "공룡찾아줘"
        savedKeyword.value = "공룡찾아줘"
        currentPage.value = 1

        fetchDataFromServer("공룡찾아줘")


        setContent {
            VoiceToTextScreen(
                spokenText = recognizedText,  // ✅ speechText.value 대신 recognizedText 사용
                books = bookList,
                currentPage = currentPage.value,
                totalPages = totalPages.value,
                isLoading = isLoading.value,
                onStartListening = { startVoiceRecognition() }, // ✅ startListening() → startVoiceRecognition()
                onPrevPage = { prevPage() },
                onNextPage = { nextPage() },
                onReset = { resetAll() },
                context = this,
                type = responseType.value,
                message = responseMessage.value

            )
        }
    }

    // Google STT 음성 인식 시작
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "대화 내용을 말씀해 주세요...")
        }

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("GoogleSTT", "Google 음성 인식 기능이 사용 불가능합니다.")
        }
    }

    // 서버에서 책 검색 요청
    private fun fetchDataFromServer(chat: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://101.55.20.4:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    isLoading.value = true // ✅ 로딩 시작
                }

                // ✅ 5초 제한 시간 설정
                val response = withTimeout(5000L) {
                    apiService.searchBook(chat)
                }

                val books = response.books
                    .filterNotNull()
                    .filter { book -> book.bookname.isNotEmpty() }
                val type = response.type
                val message = response.message

                Log.d("DEBUG", "초기 검색 결과: $books")
                Log.d("DEBUG", "초기 검색 결과: $type")
                Log.d("DEBUG", "초기 검색 결과: $message")



                withContext(Dispatchers.Main) {
                    bookList.clear()
                    bookList.addAll(books)

                    savedKeyword.value = response.keyword
                    currentPage.value = response.currentPage
                    totalPages.value = response.totalPages
                    responseType.value = response.type
                    responseMessage.value = response.message
                }

            } catch (e: TimeoutCancellationException) {
                Log.e("Timeout", "요청이 5초 안에 응답되지 않았습니다.")
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "5초 이상 걸렸습니다. 다시 검색해주세요.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("Network Error", "오류 발생: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false // ✅ 로딩 종료
                }
            }
        }
    }

    // 페이지 네이션 시 사용
    private fun fetchDataByKeyword(pageNum: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://101.55.20.4:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 요청 URL 확인을 위한 로그
        val requestUrl = "http://101.55.20.4:8000/keyword_book_search?keyword=${savedKeyword.value}&pageNum=$pageNum"
        Log.d("DEBUG", "요청 URL: $requestUrl")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                isLoading.value = true

                // ⏱ 5초 안에 응답 없으면 TimeoutCancellationException 발생
                val response = withTimeout(5000L) {
                    apiService.searchBookByKeyword(savedKeyword.value, pageNum)
                }

                val books = response.books
                    .filterNotNull()
                    .filter { book -> book.bookname.isNotEmpty() }

                withContext(Dispatchers.Main) {
                    bookList.clear()
                    bookList.addAll(books)
                    currentPage.value = response.currentPage
                    totalPages.value = response.totalPages
                }

            } catch (e: TimeoutCancellationException) {
                Log.e("Network Timeout", "요청 시간이 초과되었습니다.")
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "5초 이상 걸렸습니다. 다시 검색해주세요.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("Network Error", "오류 발생: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }

            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }
    // 이전 페이지
    private fun prevPage() {
        if (currentPage.value > 1) {
            currentPage.value -= 1
            fetchDataByKeyword(currentPage.value)
        }
    }

    // 다음 페이지
    private fun nextPage() {
        if (currentPage.value < totalPages.value) {
            currentPage.value += 1
            fetchDataByKeyword(currentPage.value)
        }
    }
    private fun resetAll() {
        recognizedText = "버튼을 눌러 말하세요."
        bookList.clear()
        currentPage.value = 1
        totalPages.value = 1
        savedKeyword.value = ""
        isLoading.value = false
    }
}
