package com.example.davidzonefiscal.entities

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File

class IrregularidadePhotosUpload(val localPaths: Array<Uri>) {
    val storage = Firebase.storage
    val imagesRef = storage.reference.child("fotosIrregularidades")
    val remotePaths: Array<String?> = arrayOfNulls(4)

    fun upload(): UploadTask {
        return imagesRef.putFile(localPaths[0])
    }

    fun uploadPhoto(name: String) {

    }

    fun rollback() {
        remotePaths.forEach {
            if(it !is String) return

            val imageRef = imagesRef.child(it)
        }
    }
}