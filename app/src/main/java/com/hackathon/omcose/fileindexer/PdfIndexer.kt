package com.hackathon.omcose.fileindexer

import android.content.Context
import android.os.Environment
import android.util.Log
import android.util.TimeUtils
import android.util.TimingLogger
import com.hackathon.omcose.LuceneManager
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import java.io.File
import java.io.IOException
import kotlin.system.measureTimeMillis

class PdfIndexer {
    fun getFilesToIndex(applicationContext: Context?) {
        PDFBoxResourceLoader.init(applicationContext)
        try {
            getPdfFilesToIndex(Environment.getExternalStorageDirectory())
        } catch (e: IOException) {
            Log.e("PdfIndexer", e.message!!)
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getPdfFilesToIndex(dir: File) {
        val pdfPattern = ".pdf"
        val fileList = dir.listFiles()
        if (fileList != null) {
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    getPdfFilesToIndex(fileList[i])
                } else {
                    if (fileList[i].name.endsWith(pdfPattern)) {
                        val pdfFile = File(fileList[i].absolutePath)
                        val elapsedTime = measureTimeMillis {
                            Log.d("PdfIndexer", "Loading PDF... ${pdfFile.absolutePath}")

                            val document = PDDocument.load(pdfFile)
                            Log.d("PdfIndexer", "PDF Loaded.")
                            val reader = PDFTextStripper()
//                            val pdfPageText =StringBuffer();

                            for (j in 0 until document.numberOfPages) {
                                reader.startPage = j
                                reader.endPage = j
//                                pdfPageText.append("\n");
                                val pdfPageText = reader.getText(document)
                                Log.d("PdfIndexer", pdfPageText.toString())
                                LuceneManager.addDoc(
                                    pdfFile.name,
                                    pdfPageText.toString(),
                                    pdfFile.absolutePath
                                )
                            }

                            document.close()
                        }
                        Log.d("PdfIndexer","Time taken to read ${pdfFile.absolutePath} is ${elapsedTime} ms")
                    }
                }
            }
        }
    }
}