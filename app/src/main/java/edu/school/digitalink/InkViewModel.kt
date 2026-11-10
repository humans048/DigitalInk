package edu.school.digitalink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.recognition.Ink

class InkViewModel : ViewModel() {
    companion object {
        const val PROMPT = "請在畫布區手寫，再按「開始辨識」按鈕"
    }
    private val document = InkDocument()
    var strokes by mutableStateOf<List<List<InkPoint>>>(emptyList())
        private set
    var message by mutableStateOf(PROMPT)
        private set
    var busy by mutableStateOf(false)
        private set
    private var request = 0L
    private var closed = false
    private val model = DigitalInkRecognitionModelIdentifier.fromLanguageTag("zh-Hant")?.let {
        DigitalInkRecognitionModel.builder(it).build()
    }
    private val recognizer = model?.let {
        DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(it).build())
    }

    private fun publish() { strokes = document.snapshot() }
    private fun invalidateRecognition() {
        request++
        busy = false
        message = PROMPT
    }
    fun start(x: Float, y: Float, time: Long) {
        invalidateRecognition()
        document.start(InkPoint(x, y, time))
        publish()
    }
    fun move(x: Float, y: Float, time: Long) {
        document.move(InkPoint(x, y, time))
        publish()
    }
    fun finish() { document.finish(); publish() }
    fun cancel() { document.cancel(); publish() }
    fun clear() {
        invalidateRecognition()
        document.clear()
        publish()
    }

    fun recognize() {
        if (busy) return
        document.finish()
        publish()
        if (document.strokes.isEmpty()) {
            message = "請先書寫內容"
            return
        }
        val selectedModel = model
        val engine = recognizer
        if (selectedModel == null || engine == null) {
            message = "模型建立失敗"
            return
        }
        val ink = Ink.builder().apply {
            document.strokes.forEach { points ->
                addStroke(Ink.Stroke.builder().apply {
                    points.forEach { addPoint(Ink.Point.create(it.x, it.y, it.time)) }
                }.build())
            }
        }.build()
        val id = ++request
        busy = true
        message = "正在準備辨識模型，首次使用需要網路…"
        RemoteModelManager.getInstance()
            .download(selectedModel, DownloadConditions.Builder().build())
            .addOnSuccessListener {
                if (!isCurrent(id)) return@addOnSuccessListener
                message = "正在辨識中…"
                engine.recognize(ink)
                    .addOnSuccessListener { result ->
                        if (isCurrent(id)) {
                            busy = false
                            message = "辨識結果: " + (result.candidates.firstOrNull()?.text ?: "無法辨識")
                        }
                    }
                    .addOnFailureListener {
                        if (isCurrent(id)) {
                            busy = false
                            message = "辨識失敗，請清除後重寫，再試一次。"
                        }
                    }
            }
            .addOnFailureListener {
                if (isCurrent(id)) {
                    busy = false
                    message = "模型下載失敗，請確認網路後再按「開始辨識」。"
                }
            }
    }

    private fun isCurrent(id: Long) = !closed && request == id

    override fun onCleared() {
        closed = true
        request++
        recognizer?.close()
    }
}
