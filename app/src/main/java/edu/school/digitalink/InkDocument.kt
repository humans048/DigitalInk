package edu.school.digitalink

/** 一筆包含按下、移動、抬起的座標與時間；與 ML Kit 的 Ink 對應。 */
data class InkPoint(val x: Float, val y: Float, val time: Long)

class InkDocument {
    var strokes: List<List<InkPoint>> = emptyList()
        private set
    var currentStroke: List<InkPoint> = emptyList()
        private set
    var revision: Long = 0
        private set

    fun start(point: InkPoint) {
        finish()
        currentStroke = listOf(point)
        revision++
    }
    fun move(point: InkPoint) {
        if (currentStroke.isNotEmpty()) {
            currentStroke = currentStroke + point
            revision++
        }
    }
    fun finish() {
        if (currentStroke.isNotEmpty()) {
            strokes = strokes + listOf(currentStroke)
            currentStroke = emptyList()
            revision++
        }
    }
    fun cancel() {
        if (currentStroke.isNotEmpty()) {
            currentStroke = emptyList()
            revision++
        }
    }
    fun clear() {
        strokes = emptyList()
        currentStroke = emptyList()
        revision++
    }
    fun snapshot(): List<List<InkPoint>> = strokes + listOfNotNull(
        currentStroke.takeIf { it.isNotEmpty() }
    )
}
