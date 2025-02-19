package com.app.notetaker.media

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import com.app.notetaker.MainActivity
import com.app.notetaker.database.AppDatabase
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

class DocScan {

    companion object {
        private var scanner: GmsDocumentScanner? = null
        fun getScannerClient(): GmsDocumentScanner {
            val options = GmsDocumentScannerOptions.Builder()
                .setScannerMode(SCANNER_MODE_FULL)
                .setGalleryImportAllowed(true)
                .setPageLimit(5)
                .setResultFormats(RESULT_FORMAT_PDF, RESULT_FORMAT_PDF)
                .build()
            val scan = GmsDocumentScanning.getClient(options)
            scanner = scan
            return scan
        }
    }
}