package com.davenet.notely.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.WorkManager
import com.davenet.notely.database.NotesDatabase
import com.davenet.notely.database.asDomainModel
import com.davenet.notely.domain.NoteEntry
import com.davenet.notely.domain.asDataBaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteListRepository(private val database: NotesDatabase) {
    val notes: LiveData<List<NoteEntry>> = Transformations.map(database.noteDao.getAllNotes()) {
        it.asDomainModel()
    }

    suspend fun deleteAllNotes() {
        withContext(Dispatchers.IO) {
            database.noteDao.deleteAllNotes()
        }
    }

    suspend fun deleteNote(note: NoteEntry) {
        withContext(Dispatchers.IO) {
            database.noteDao.deleteNote(note.id)
        }
    }

    suspend fun restoreNote(note: NoteEntry) {
        withContext(Dispatchers.IO) {
            database.noteDao.insert(note.copy())
        }
    }

    suspend fun insertAllNotes(noteList: List<NoteEntry>) {
        withContext(Dispatchers.IO) {
            database.noteDao.insertNotesList(noteList.asDataBaseModel())
        }
    }

    fun cancelAlarm(context: Context, note: NoteEntry) {
        val workName = "Work ${note.id}"
        val instanceWorkManager = WorkManager.getInstance(context)
        instanceWorkManager.cancelUniqueWork(workName)
    }
}