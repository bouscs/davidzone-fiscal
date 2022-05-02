package com.example.davidzonefiscal.entities

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class IrregularidadePhotosUpload(val localPaths: ArrayList<String>) {
    val storage = Firebase.storage
    val imagesRef = storage.reference.child("fotosIrregularidades")
    val remotePaths: ArrayList<String> = arrayListOf()

    var onProgressCallback: (finishedCounter: Int) -> Unit = { }

    fun start(callback: (remotePaths: ArrayList<String>) -> Unit) {
        var finishedCounter = 0

        val tasks = arrayListOf<UploadTask>()

        for (path in localPaths) {
            val file = Uri.fromFile(File(path))
            val photoRef = imagesRef.child(UUID.randomUUID().toString())

            val uploadTask = photoRef.putFile(file)
            tasks.add(uploadTask)

            uploadTask.addOnFailureListener {
                Log.e("IrregularidadeUpload", it.toString())
            }.addOnSuccessListener { taskSnapshot ->
                finishedCounter += 1
                onProgressCallback(finishedCounter)
                val meta = taskSnapshot.metadata
                if(meta != null)
                    remotePaths.add(meta.path)

                Log.i("IrregularidadeUpload", meta.toString())
                Log.i("IrregularidadeUpload", "finishedCounter = $finishedCounter")

                if(finishedCounter >= localPaths.size)
                    callback(remotePaths)
            }
        }
    }

    fun rollback() {
        remotePaths.forEach {
            if(it !is String) return

            val imageRef = imagesRef.child(it)
        }
    }
}