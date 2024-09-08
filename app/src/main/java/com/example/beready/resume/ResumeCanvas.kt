package com.example.beready.resume

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.beready.profile.ProfileData
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class ResumeCanvas(
    context: Context,
    private val profileDataFlow: StateFlow<ProfileData>
) : View(context) {

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 5f
    }

    // Hold the latest profile data
    private var profileData: ProfileData? = null

    init {
        MainScope().launch {
            profileDataFlow.collect { newData ->
                profileData = newData
                invalidate()  // Trigger a re-draw whenever new data arrives
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        profileData?.let {
            drawResumeContent(canvas, it)
        }
    }

    private fun drawResumeContent(canvas: Canvas, data: ProfileData) {

            val canvasWidth = canvas.width.toFloat() - 40f // margin on both sides
            val leftMargin = 20f
            var yPosition = 60f // Adjusted for title positioning
             canvas.drawText("Resume", leftMargin, yPosition, titlePaint)
            yPosition += 50f
            // Title

            canvas.drawText(data.name, leftMargin, yPosition, titlePaint)
             yPosition += 50f
            canvas.drawLine(leftMargin, yPosition, canvasWidth, yPosition, linePaint)
            yPosition += 50f

            // GET IN CONTACT

            canvas.drawText("GET IN CONTACT", leftMargin, yPosition, titlePaint)
        yPosition += 35f

            // Email Contact Info
            canvas.drawText("Email: ${data.email.replace("_",".")}", leftMargin, yPosition, textPaint)
            yPosition += 70f

            // EDUCATION HISTORY
            canvas.drawText("EDUCATION HISTORY", leftMargin, yPosition, titlePaint)

            canvas.drawText(    "${data.education}   \n${data.clgName}\n   ${data.year}   CGPA: ${data.cgpa}", leftMargin, yPosition + 30f, textPaint)
            yPosition += 70f

            // SKILLS
            canvas.drawText("SKILLS", leftMargin, yPosition, titlePaint)
            // Extra gap after skills
            canvas.drawText(data.skill, leftMargin, yPosition + 30f, textPaint)
            yPosition += 100f // Increased gap after skills

            // INTERNSHIPS
            canvas.drawText("INTERNSHIPS", leftMargin, yPosition, titlePaint)
            // Extra gap after internships
            canvas.drawText(data.internship, leftMargin, yPosition + 30f, textPaint)
            yPosition += 100f // Increased gap after internships

            // BADGES
            canvas.drawText("BADGES", leftMargin, yPosition, titlePaint)
        canvas.drawText(data.badge, leftMargin, yPosition, textPaint)
        yPosition += 70f
            // Final line
            canvas.drawLine(leftMargin, yPosition, canvasWidth, yPosition, linePaint)


    }

    fun createPdf() {
        val pdfDocument = PdfDocument()

        // Use the measured width and height for the PDF page
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        // Draw the content of ResumeCanvas onto the PDF's canvas
        // Replace 'canvas' with your own method to draw the content
        draw(page.canvas)

        pdfDocument.finishPage(page)

        try {
            val file: File
            val outputStream: OutputStream

            // For Android 10 and above, use MediaStore API to save to Downloads folder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentResolver = context.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "resume.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

                outputStream = contentResolver.openOutputStream(uri!!)!!
            } else {
                // For Android 9 and below, save directly to the Downloads folder
                val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                file = File(downloadsFolder, "resume.pdf")
                outputStream = FileOutputStream(file)
            }

            pdfDocument.writeTo(outputStream)
            outputStream.close()


            Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }

    }
}
