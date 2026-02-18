package com.fincalc.app.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtil {

    fun generateCalculationPdf(
        context: Context,
        calculatorName: String,
        inputLines: List<String>,
        resultLines: List<String>,
        chartBitmap: Bitmap? = null
    ): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(1080, 1920, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 28f }

        var y = 80f
        canvas.drawText("FinCalc - $calculatorName", 40f, y, paint)
        y += 60f

        paint.textSize = 22f
        canvas.drawText("Inputs:", 40f, y, paint)
        y += 40f
        inputLines.forEach {
            canvas.drawText("• $it", 60f, y, paint)
            y += 34f
        }

        y += 20f
        canvas.drawText("Results:", 40f, y, paint)
        y += 40f
        resultLines.forEach {
            canvas.drawText("• $it", 60f, y, paint)
            y += 34f
        }

        if (chartBitmap != null) {
            y += 30f
            val scaled = Bitmap.createScaledBitmap(chartBitmap, 900, 450, true)
            canvas.drawBitmap(scaled, 80f, y, null)
        }

        document.finishPage(page)

        val fileName = "${calculatorName}_${SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())}.pdf"
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) downloadDir.mkdirs()
        val file = File(downloadDir, fileName)
        FileOutputStream(file).use { out -> document.writeTo(out) }
        document.close()
        return file
    }
}
