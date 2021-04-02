package com.bazumax.capacitor.firebase.storage

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.component3
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

class FirebaseStorage {
    lateinit var storage: com.google.firebase.storage.FirebaseStorage
    lateinit var storageReference: com.google.firebase.storage.StorageReference

    fun load() {
        storage = Firebase.storage(Firebase.app)
        storageReference = storage.reference
    }

    fun createFileRequest(title: String = "Выберите аватар"): Intent {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
                // https://github.com/hinddeep/capacitor-file-selector/blob/master/android/src/main/java/com/bkon/capacitor/fileselector/FileSelector.java#L51
                .putExtra(Intent.EXTRA_MIME_TYPES, "")
                .setType("*/*")
                .addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, title)
        return chooseFile
    }

    fun createSelectImageRequest(title: String = "Выберите аватар"): Intent {
        return Intent(Intent.ACTION_PICK)
                .setType("image/*")
    }

    fun uploadFile(uri: Uri, storagePath: String, callback: (Boolean, String) -> Unit) {

        val fileRef = storageReference.child(storagePath)
        fileRef.putFile(uri).addOnFailureListener {
            callback(false, it.localizedMessage!!)
        }.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                callback(true, it.toString())
            }
        }
    }

    fun getDownloadUrl(path: String, callback: (Boolean, String) -> Unit) {
        storageReference.child(path).downloadUrl
                .addOnFailureListener {
                    callback(false, it.localizedMessage)
                }
                .addOnSuccessListener {
                    callback(true, it.toString())
                }
    }

    fun deleteFile(path: String, callback: (Boolean, String) -> Unit) {
        storageReference.child(path).delete()
                .addOnFailureListener {
                    callback(false, it.localizedMessage)
                }
                .addOnSuccessListener {
                    callback(true, "success")
                }
    }

    fun watchFileCreation(path: String, tryIndex: Int = 0, callback: (Boolean, String) -> Unit) {
        if (tryIndex > 10) {
            callback(false, "watch timeout");
            return
        }
        storageReference.child(path).downloadUrl
                .addOnSuccessListener {
                    callback(true, it.toString())
                }
                .addOnFailureListener {
                    Timer("next", false).schedule(1000) {
                        watchFileCreation(path, tryIndex+1, callback)
                    }
                }
    }
}