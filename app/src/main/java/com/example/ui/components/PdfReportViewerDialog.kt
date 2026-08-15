package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.model.ErpBottleneck
import com.example.data.model.RealtimeMarketDataFeed
import com.example.data.pdf.PdfReportGenerator
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Composable
fun PdfReportViewerDialog(
    bottleneck: ErpBottleneck,
    marketFeed: RealtimeMarketDataFeed? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isGenerating by remember { mutableStateOf(true) }
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var pageBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var currentPageIndex by remember { mutableStateOf(0) }
    var zoomLevel by remember { mutableStateOf(1f) }

    // Clean up Bitmaps on disposal
    DisposableEffect(Unit) {
        onDispose {
            pageBitmaps.forEach { bm ->
                if (!bm.isRecycled) {
                    bm.recycle()
                }
            }
        }
    }

    // Generate PDF and render bitmaps on background thread
    LaunchedEffect(bottleneck) {
        withContext(Dispatchers.IO) {
            try {
                val file = PdfReportGenerator.generateVentureReportPdf(context, bottleneck, marketFeed)
                pdfFile = file

                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                val bitmaps = mutableListOf<Bitmap>()

                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    // High resolution render for crisp text
                    val scale = 1.5f
                    val bitmap = Bitmap.createBitmap(
                        (page.width * scale).toInt(),
                        (page.height * scale).toInt(),
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps.add(bitmap)
                    page.close()
                }
                renderer.close()
                pfd.close()

                withContext(Dispatchers.Main) {
                    pageBitmaps.forEach { bm ->
                        if (!bm.isRecycled && !bitmaps.contains(bm)) {
                            bm.recycle()
                        }
                    }
                    pageBitmaps = bitmaps
                    isGenerating = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isGenerating = false
                    Toast.makeText(context, "Error generating PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val sharePdf = {
        pdfFile?.let { file ->
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "ProcessFoundry Investment Memo - ${bottleneck.suggestedVentureIdea.name}")
                    putExtra(Intent.EXTRA_TEXT, "Attached is the institutional investment memorandum and ERP analysis for ${bottleneck.suggestedVentureIdea.name}.")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Investment Memo PDF"))
            } catch (e: Exception) {
                Toast.makeText(context, "Could not share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val printPdf = {
        pdfFile?.let { file ->
            try {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = object : PrintDocumentAdapter() {
                    override fun onLayout(
                        oldAttributes: PrintAttributes?,
                        newAttributes: PrintAttributes?,
                        cancellationSignal: CancellationSignal?,
                        callback: LayoutResultCallback?,
                        extras: Bundle?
                    ) {
                        if (cancellationSignal?.isCanceled == true) {
                            callback?.onLayoutCancelled()
                            return
                        }
                        val pdi = PrintDocumentInfo.Builder("ProcessFoundry_${bottleneck.suggestedVentureIdea.name}.pdf")
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(pageBitmaps.size.coerceAtLeast(1))
                            .build()
                        callback?.onLayoutFinished(pdi, true)
                    }

                    override fun onWrite(
                        pages: Array<out PageRange>?,
                        destination: ParcelFileDescriptor?,
                        cancellationSignal: CancellationSignal?,
                        callback: WriteResultCallback?
                    ) {
                        try {
                            val input = FileInputStream(file)
                            val output = FileOutputStream(destination?.fileDescriptor)
                            val buf = ByteArray(1024)
                            var bytesRead: Int
                            while (input.read(buf).also { bytesRead = it } > 0) {
                                output.write(buf, 0, bytesRead)
                            }
                            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                            input.close()
                            output.close()
                        } catch (e: Exception) {
                            callback?.onWriteFailed(e.message)
                        }
                    }
                }
                printManager?.print("ProcessFoundry Memo", printAdapter, PrintAttributes.Builder().build())
            } catch (e: Exception) {
                Toast.makeText(context, "Print service unavailable: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = SophisticatedDarkBg,
            border = BorderStroke(1.dp, SophisticatedBorder)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedLavender.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
                            ) {
                                Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF Memo",
                                        tint = SophisticatedLavender,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "PDF REPORT PREVIEW",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedLavender,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "${bottleneck.suggestedVentureIdea.name} Deal Memo",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedTextPrimary,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }

                        // Top Action Icons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Print Button
                            IconButton(
                                onClick = { printPdf() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(SophisticatedSurfaceVariant, CircleShape)
                                    .border(1.dp, SophisticatedBorder, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Print PDF",
                                    tint = SophisticatedTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Share Button
                            IconButton(
                                onClick = { sharePdf() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(SophisticatedSurfaceVariant, CircleShape)
                                    .border(1.dp, SophisticatedBorder, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share PDF",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Close Button
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(SophisticatedSurfaceVariant, CircleShape)
                                    .border(1.dp, SophisticatedBorder, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = SophisticatedTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                if (isGenerating) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                color = SophisticatedLavender,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(42.dp)
                            )
                            Text(
                                text = "Compiling Institutional PDF Memorandum...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                } else if (pageBitmaps.isNotEmpty()) {
                    // PDF Document Viewer Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF090B10))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val currentBitmap = pageBitmaps.getOrNull(currentPageIndex)
                        if (currentBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                    border = BorderStroke(1.dp, SophisticatedBorder)
                                ) {
                                    Image(
                                        bitmap = currentBitmap.asImageBitmap(),
                                        contentDescription = "Page ${currentPageIndex + 1}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Navigation & Thumbnail Strip
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SophisticatedSurface,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Thumbnail Strip
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                pageBitmaps.forEachIndexed { index, bitmap ->
                                    val isSelected = index == currentPageIndex
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SophisticatedDarkBg,
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) SophisticatedLavender else SophisticatedBorder
                                        ),
                                        modifier = Modifier
                                            .width(44.dp)
                                            .height(62.dp)
                                            .clickable { currentPageIndex = index }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = "Thumb ${index + 1}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.65f),
                                                shape = RoundedCornerShape(bottomEnd = 4.dp),
                                                modifier = Modifier.align(Alignment.TopStart)
                                            ) {
                                                Text(
                                                    text = "${index + 1}",
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 8.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Controls Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Previous
                                OutlinedButton(
                                    onClick = { if (currentPageIndex > 0) currentPageIndex-- },
                                    enabled = currentPageIndex > 0,
                                    shape = RoundedCornerShape(100.dp),
                                    border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Page",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Prev", fontSize = 11.sp)
                                }

                                // Page Counter
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = SophisticatedSurfaceVariant,
                                    border = BorderStroke(1.dp, SophisticatedBorder)
                                ) {
                                    Text(
                                        text = "Page ${currentPageIndex + 1} of ${pageBitmaps.size}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SophisticatedTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                // Next
                                Button(
                                    onClick = { if (currentPageIndex < pageBitmaps.size - 1) currentPageIndex++ },
                                    enabled = currentPageIndex < pageBitmaps.size - 1,
                                    shape = RoundedCornerShape(100.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SophisticatedLavender,
                                        contentColor = SophisticatedLavenderDark
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Next", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Page",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
