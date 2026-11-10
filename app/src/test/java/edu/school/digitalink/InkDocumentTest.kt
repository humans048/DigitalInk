package edu.school.digitalink

import org.junit.Assert.*
import org.junit.Test

class InkDocumentTest {
    @Test fun multipleStrokesAreNotConnected() {
        val ink = InkDocument()
        ink.start(InkPoint(1f, 2f, 1))
        ink.move(InkPoint(2f, 3f, 2))
        ink.finish()
        ink.start(InkPoint(20f, 30f, 3))
        ink.finish()
        assertEquals(2, ink.strokes.size)
        assertEquals(2, ink.strokes[0].size)
        assertEquals(20f, ink.strokes[1][0].x)
    }
    @Test fun tapIsPreservedAndEmptyFinishDoesNotAddAStroke() {
        val ink = InkDocument()
        ink.finish()
        assertTrue(ink.strokes.isEmpty())
        ink.start(InkPoint(1f, 1f, 1))
        ink.finish()
        ink.finish()
        assertEquals(1, ink.strokes.size)
        assertEquals(1, ink.strokes[0].size)
    }
    @Test fun clearRemovesAllDataAndAllowsWritingAgain() {
        val ink = InkDocument()
        ink.start(InkPoint(1f, 2f, 1))
        ink.finish()
        ink.start(InkPoint(3f, 4f, 2))
        ink.clear()
        assertTrue(ink.snapshot().isEmpty())
        ink.start(InkPoint(5f, 6f, 3))
        ink.finish()
        assertEquals(1, ink.strokes.size)
    }
    @Test fun snapshotIncludesCurrentStrokeWithoutMutatingCompletedData() {
        val ink = InkDocument()
        ink.start(InkPoint(1f, 2f, 1))
        val snapshot = ink.snapshot()
        ink.move(InkPoint(3f, 4f, 2))
        assertEquals(1, snapshot[0].size)
        assertEquals(2, ink.snapshot()[0].size)
        assertTrue(ink.strokes.isEmpty())
    }
    @Test fun cancellationDropsOnlyCurrentStroke() {
        val ink = InkDocument()
        ink.start(InkPoint(1f, 1f, 1))
        ink.finish()
        ink.start(InkPoint(2f, 2f, 2))
        ink.cancel()
        assertEquals(1, ink.snapshot().size)
        assertTrue(ink.currentStroke.isEmpty())
    }
}
