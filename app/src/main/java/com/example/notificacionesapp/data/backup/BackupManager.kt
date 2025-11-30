package com.example.notificacionesapp.data.backup


import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.notificacionesapp.data.entities.Nota
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(private val context: Context) {

    private val gson = Gson()
    private val dateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun exportNotes(notes: List<Nota>, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val writer = BufferedWriter(OutputStreamWriter(outputStream))
                    val backupData = BackupData(
                        notes = notes,
                        exportDate = Date(),
                        version = 1
                    )
                    writer.write(gson.toJson(backupData))
                    writer.close()
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun importNotes(uri: Uri): List<Nota> {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val json = reader.readText()
                    val backupData = gson.fromJson(json, BackupData::class.java)
                    reader.close()
                    backupData.notes
                } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    fun generateBackupFileName(): String {
        val timestamp = dateFormatter.format(Date())
        return "notas_backup_$timestamp.json"
    }
}

data class BackupData(
    val notes: List<Nota>,
    val exportDate: Date,
    val version: Int
)