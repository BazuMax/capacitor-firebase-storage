package com.bazumax.capacitor.firebase.storage

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


@CapacitorPlugin(name = "FirebaseStorage")
public class FirebaseStoragePlugin : Plugin() {
    private val implementation: FirebaseStorage = FirebaseStorage()

    override fun load() {
        super.load()
        implementation.load()
    }
    fun sendState(state: String) {
        val obj = JSObject()
        obj.put("state", state)
        notifyListeners("onUploadStateChanged", obj)
    }

    fun selectImage(call: PluginCall) {
        val title: String = call.getString("title", "Выберите аватар")!!

        val intent = implementation.createSelectImageRequest(title)
        startActivityForResult(call, intent, "selectImageCallback")

    }

    suspend fun compressThenUpload(call: PluginCall, uri: Uri) {
        val compressedFileUri = comressImage(uri, activity)
        uploadRealFile(call, compressedFileUri)
    }

    fun uploadRealFile(call: PluginCall, uri: Uri) {
        val storagePath = call.getString("storagePath", "avatar")!!

        sendState("uploading")
        implementation.uploadFile(uri, storagePath) { result: Boolean, uriOrMessage: String ->
            if (result) {
                val obj = JSObject()
                obj.put("url", uriOrMessage)
                call.resolve(obj)
            } else {
                call.reject(uriOrMessage)
            }
        }
    }

    @ActivityCallback
    fun selectImageCallback(call: PluginCall, result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            call.reject("Invalid result code: " + result.resultCode)
            return
        }

        var uri = result.data!!.data!!

        GlobalScope.launch(Dispatchers.Main) {
            sendState("compressing")
            if (call.getBoolean("compress", false) == true) {
                compressThenUpload(call, uri)
                return@launch
            }

            uploadRealFile(call, uri)
        }
    }

    @PluginMethod
    public fun uploadFile(call: PluginCall) {
        if (call.getBoolean("withImagePicker") == true) {
            selectImage(call)
            return
        }

        // TODO: base upload
    }

    @PluginMethod
    public fun getDownloadUrl(call: PluginCall) {
        val path = call.getString("path")
        if (path == null) {
            call.reject("path is null")
            return
        }
        implementation.getDownloadUrl(path) { result: Boolean, uriOrMessage: String ->
            if (result) {
                val obj = JSObject()
                obj.put("url", uriOrMessage)
                call.resolve(obj)
            } else {
                call.reject(uriOrMessage)
            }
        }
    }

    @PluginMethod
    public fun deleteFile(call: PluginCall) {
        val path = call.getString("path")
        if (path == null) {
            call.reject("path is null")
            return
        }

        implementation.deleteFile(path) { result: Boolean, message: String ->
            if (result) {
                val obj = JSObject()
                obj.put("message", message)
                call.resolve(obj)
            } else {
                call.reject(message)
            }
        }
    }

    @PluginMethod
    public fun watchFileCreation(call: PluginCall) {
        val path = call.getString("path")
        if (path == null) {
            call.reject("path is null")
            return
        }

        implementation.watchFileCreation(path) { result: Boolean, urlOrMessage: String ->
            if (result) {
                val obj = JSObject()
                obj.put("url", urlOrMessage)
                call.resolve(obj)
            } else {
                call.reject(urlOrMessage)
            }
        }
    }
}