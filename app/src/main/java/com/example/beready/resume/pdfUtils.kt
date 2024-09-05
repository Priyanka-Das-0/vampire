package com.example.beready.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfUtils {

    fun createPdf(context: Context, profileData: Map<String, String>) {
        val pdfPath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Profile.pdf")
        try {
            PdfWriter(pdfPath.absolutePath).use { pdfWriter ->
                PdfDocument(pdfWriter).use { pdfDocument ->
                    val document = Document(pdfDocument)
                    val font: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
                    document.setFont(font)

                    profileData.forEach { (key, value) ->
                        document.add(Paragraph("$key: $value").setFontSize(12f))
                    }

                    document.close()
                }
            }
            // Notify user
            Toast.makeText(context, "PDF saved to ${pdfPath.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to create PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
