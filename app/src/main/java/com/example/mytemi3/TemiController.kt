package com.example.mytemi3

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener

class TemiController(private val context: Context) : OnGoToLocationStatusChangedListener {
    private val robot: Robot = Robot.getInstance()
    private val handler = Handler(Looper.getMainLooper())
    private var isArrived = false // ✅ Temi가 도착했는지 확인하는 변수

    init {
        robot.addOnGoToLocationStatusChangedListener(this) // ✅ 이동 이벤트 감지 리스너 등록
    }

    fun moveToLocation(locationName: String) {
        if (isArrived) return // ✅ 이미 도착한 경우 다시 이동하지 않음
        Log.d("TemiController", "Temi 이동 시작: $locationName")
        robot.goTo(locationName)
    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        Log.d("TemiController", "이동 상태 변경: location = $location, status = $status")

        when (status.lowercase()) {
            "going" -> {
                if (!isArrived) {
                    Toast.makeText(context, "가는 중입니다: $location", Toast.LENGTH_SHORT).show()
                }
            }
            "complete" -> {
                if (!isArrived) {
                    isArrived = true // ✅ 도착 상태 저장
                    Log.d("TemiController", "Temi 도착 완료: $location")
                    robot.stopMovement() // ✅ 이동 멈추기
                    Toast.makeText(context, "도착했습니다: $location", Toast.LENGTH_SHORT).show()

                    // ✅ 5초 후 홈으로 이동
                    handler.postDelayed({
                        isArrived = false
                        robot.goTo("홈으로")
                    }, 5000)
                }
            }
            "calculation", "idle", "obstacle" -> {
                Log.d("TemiController", "Temi 상태: $status")
            }
        }

        // ✅ "홈으로" 이동 완료 시, 다시 대기 상태로 유지
        if (status.equals("complete", ignoreCase = true) && location == "홈으로") {
            robot.stopMovement() // ✅ Temi 이동 멈추기
            Toast.makeText(context, "홈베이스 도착 완료 - 대기 상태", Toast.LENGTH_SHORT).show()
            isArrived = false // ✅ 다시 이동할 수 있도록 상태 초기화
        }
    }
}