package net.rachel030219.poweramplrc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile


class PathActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("path_request") && intent.getIntExtra("path_request", 0) == LrcService.REQUEST_PATH) {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), LrcService.REQUEST_PATH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LrcService.REQUEST_PATH && resultCode == RESULT_OK && data != null) {
            val treeUri = data.data
            if (treeUri != null) {
                android.util.Log.d("DEBUG-URI", treeUri.toString())
                val takeFlags = (data.flags
                        and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                contentResolver.takePersistableUriPermission(treeUri, takeFlags)
                DocumentFile.fromTreeUri(this, treeUri)!!.listFiles().forEach {
                    if (it.isDirectory) {
                        it.listFiles().forEach { inner ->
                            if (inner.isDirectory) {
                                inner.listFiles().forEach { file ->
                                    android.util.Log.d("DEBUG-PATH ORIGORIG", file.uri.toString())
                                }
                            }
                        }
                    }
                }
                getSharedPreferences(intent.getStringExtra("key"), Context.MODE_PRIVATE).edit().putString("path", "$treeUri/document/").apply()
                finish()
            }
        }
    }
}