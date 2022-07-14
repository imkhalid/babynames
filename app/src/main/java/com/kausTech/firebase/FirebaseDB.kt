package com.xeontechnologies.autoassistant.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kausTech.babynames.ui.fragments.Names
import com.kausTech.babynames.util.PrefUtil
import com.kausTech.firebase.FirebaseCollections
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class FirebaseDB<T>(private val tClass: Class<T>, collections: FirebaseCollections) {
    private val TAG = "FirebaseDB"


    var table: CollectionReference
        protected set

    fun subCollection(docKey: String?, collections: FirebaseCollections): FirebaseDB<T> {
        table = table.document(docKey!!).collection(collections.name)
        return this
    }

    fun subCollection(collections: FirebaseCollections): FirebaseDB<T> {
        table = table.document().collection(collections.name)
        return this
    }

    val uniqueID: String
        get() = table.document().id

    //=======================Add Data Functions============================
    fun addData(key: String, obj: T, listener: DefaultListener?) {
        addDataToFirebase(key, obj, listener)
    }

    fun addData(obj: T, listener: DefaultListener?) {
        val key = uniqueID
        addDataToFirebase(key, obj, listener)
    }

    private fun addDataToFirebase(key: String, obj: T, listener: DefaultListener?) {
        table.document(key).set(obj!!)
            .addOnSuccessListener {
                listener?.onSuccess(key)
            }
            .addOnFailureListener { e -> listener?.onFailure(e.message) }
    }

    //=======================Get Data Functions============================
    fun getByKey(key: String, listenForever: Boolean, listener: GetListener<T?>?) {
        if (listenForever) getForeverByKey(key, listener) else getOnceByKey(key, listener)
    }

    fun getByField(fieldName: String?, value: Any?, listener: GetListener<T?>?) {
        table.whereEqualTo(fieldName!!, value)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    val `object` = queryDocumentSnapshots.documents[0].toObject(
                        tClass
                    )
                    listener?.onSuccess(`object`)
                } else {
                    listener?.onFailure("No such document")
                }
            }
            .addOnFailureListener { e -> listener?.onFailure("get failed with " + e.message) }
    }

    private fun getForeverByKey(key: String, listener: GetListener<T?>?) {

        val listener1 = table.document(key).addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                listener?.onFailure("get failed with " + e.message)
            } else {
                if (documentSnapshot != null) {
                    val `object` = documentSnapshot.toObject(tClass)
                    listener?.onSuccess(`object`)
                } else {
                    listener?.onFailure("No such document")
                }
            }
        }
        listener?.onListen(listener1)
    }

    private fun getOnceByKey(key: String, listener: GetListener<T?>?) {
        table.document(key).addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                listener?.onFailure("get failed with " + e.message)
            } else {
                if (documentSnapshot != null) {
                    val `object` = documentSnapshot.toObject(tClass)
                    if (`object` == null) {
                        listener?.onFailure("No such document")
                    } else {
                        listener?.onSuccess(`object`)
                    }
                } else {
                    listener?.onFailure("No such document")
                }
            }
        }
    }

    //=======================Get List of Data Functions============================
    fun getAll(listener: GetAllListener<T>) {
        val listenerRegistration = table
            .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    listener.onFailure(e.message)
                    return@EventListener
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (doc in queryDocumentSnapshots.documentChanges) {
                        val toObject = doc.document.toObject(tClass)
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> {
                                listener.onNew(doc.document.id, toObject)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                listener.onChange(doc.document.id, toObject)
                            }
                            DocumentChange.Type.REMOVED -> {
                                listener.onRemove(doc.document.id)
                            }
                        }
                    }
                } else {
                    listener.onNoData()
                }
            })
        listener.onListenerAttached(listenerRegistration)
    }

    fun getAllList(orderBy: String, listener: GetAllListListener<T>) {
        val lastName= PrefUtil.getPrefs().getString("LAST_STORED_NAME","")
//        val quer = if (lastName != null) {
//            table.orderBy(orderBy).startAfter(lastName).limit(500)
//        } else {
//            table.orderBy(orderBy).limit(500)
//        }

        table.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val list = ArrayList<T>()
                if (task.result != null) {
                    task.result.takeIf { x -> x.isEmpty.not() }?.let {
                        val lastName = task.result.documents.last()[orderBy].toString()
                        PrefUtil.getPrefs().edit().putString("LAST_STORED_NAME",lastName).apply()
                    }
                    for (document in task.result!!) {
                        list.add(document.toObject(tClass))
                    }
                } else
                    listener.onNoData()
                if (!list.isEmpty()) {
                    listener.onSuccess(list)
                } else {
                    listener.onNoData()
                }

            } else {
                listener.onFailure(task.exception!!.message)
            }
        }
    }

    //=======================Get List of Data Functions============================
    fun executeQuerySingle(query: Query, listener: GetListener2<T?>) {
        query.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.documents.isEmpty()) listener.onSuccess(
                    queryDocumentSnapshots.documents[0].toObject(
                        tClass
                    )
                ) else listener.onNoData()
            }
            .addOnFailureListener { e -> listener.onFailure(e.message) }
    }

    fun executeQueryChild(query: Query, listener: GetAllListener<T>) {
        val listenerRegistration =
            query.addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    listener.onFailure(e.message)
                    return@EventListener
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (doc in queryDocumentSnapshots.documentChanges) {
                        try {
                            val toObject = doc.document.toObject(tClass)
                            when (doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    listener.onNew(doc.document.id, toObject)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    listener.onChange(doc.document.id, toObject)
                                }
                                DocumentChange.Type.REMOVED -> {
                                    listener.onRemove(doc.document.id)
                                }
                            }
                        } catch (e1: Exception) {
                        }
                    }
                } else {
                    listener.onNoData()
                }
            })
        listener.onListenerAttached(listenerRegistration)
    }

    fun executeQueryChild2(query: Query, listener: GetAllListener2<T>) {
        val listenerRegistration =
            query.addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    listener.onFailure(e.message)
                    return@EventListener
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (doc in queryDocumentSnapshots.documentChanges) {
                        try {
                            val toObject = doc.document.toObject(tClass)
                            when (doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    listener.onNew(doc.document.id, toObject)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    listener.onChange(doc.document.id, toObject)
                                }
                                DocumentChange.Type.REMOVED -> {
                                    listener.onRemove(doc.document.id)
                                }
                            }
                        } catch (e1: Exception) {
                        }
                    }
                    listener.onFinish()
                } else {
                    listener.onNoData()
                }
            })
        listener.onListenerAttached(listenerRegistration)
    }

    fun executeQueryList(query: Query, listener: GetAllListListener<T>) {
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val list = ArrayList<T>()
                if (task.result != null) for (document in task.result!!) {
                    try {
                        list.add(document.toObject(tClass))
                    } catch (e: Exception) {
                        Log.e(TAG, "executeQueryList-> onComplete: " + e.message)
                    }
                } else listener.onNoData()
                if (!list.isEmpty()) listener.onSuccess(list) else listener.onNoData()
            } else {
                listener.onFailure(task.exception!!.message)
            }
        }
    }

    //=======================Update Data Functions============================
    fun update(key: String, newData: T, listener: GetListener<T?>?) {
        table.document(key).set(newData!!)
            .addOnSuccessListener { listener?.let { getOnceByKey(key, it) } }
            .addOnFailureListener { e -> listener?.onFailure(e.message) }
    }

    fun updateField(key: String, docData: Map<String, Any>?, listener: GetListener<T?>?) {
        table.document(key).update(docData!!)
            .addOnSuccessListener { listener?.let { getOnceByKey(key, it) } }
            .addOnFailureListener { e -> listener?.onFailure(e.message) }
    }


    fun updateFieldSingle(key: String, field: String, value: Any, listener: GetListener<T?>?) {

        if (value is HashMap<*, *>) {
            table.document(key).update(value as HashMap<String, Any>)
                .addOnSuccessListener { listener?.let { getOnceByKey(key, it) } }
                .addOnFailureListener { e -> listener?.onFailure(e.message) }
        } else if (value is FieldValue) {
            table.document(key).update(field!!, value)
                .addOnSuccessListener { listener?.let { getOnceByKey(key, it) } }
                .addOnFailureListener { e -> listener?.onFailure(e.message) }
        } else {
            val docData: MutableMap<String, Any> = HashMap()
            docData[field] = value
            table.document(key).update(docData)
                .addOnSuccessListener { listener?.let { getOnceByKey(key, it) } }
                .addOnFailureListener { e -> listener?.onFailure(e.message) }
        }
    }

    fun updateArray(
        key: String,
        field: String?,
        fieldValue: FieldValue?,
        getUpdatedDataToo: Boolean,
        listener: GetListener<T?>?
    ) {
        table.document(key).update(field!!, fieldValue)
            .addOnSuccessListener {
                if (listener != null) {
                    if (getUpdatedDataToo) getOnceByKey(key, listener) else listener.onSuccess(null)
                }
            }
            .addOnFailureListener { e -> listener?.onFailure(e.message) }
    }

    //=======================Delete Data Functions============================
    fun delete(key: String?, listener: DefaultListener?) {
        table.document(key!!).delete()
            .addOnSuccessListener { listener?.onSuccess(key) }
            .addOnFailureListener { e -> listener?.onFailure(e.message) }
    }

    //=======================Delete Data Functions============================
    fun deleteByField(fieldName: String?, value: Any?, listener: DefaultListener?) {
        table.whereEqualTo(fieldName!!, value)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (documentSnapshot in queryDocumentSnapshots.documents) {
                        table.document(documentSnapshot.id).delete()
                    }
                    listener?.onSuccess("")
                } else {
                    listener?.onFailure("No such document")
                }
            }
            .addOnFailureListener { e -> listener?.onFailure("get failed with " + e.message) }
    }

    fun countDocuments(listener: CountListener) {
        table.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val count = task.result.size()
                listener.onSuccess(count)
            } else {
                listener.onFailure(task.exception!!.message)
            }
        }
    }

    //==============================================================================
    interface DefaultListener {
        fun onSuccess(msg: String?)
        fun onFailure(reason: String?)
    }

    interface CountListener {
        fun onSuccess(count: Int)
        fun onFailure(reason: String?)
    }

    interface GetListener<T> {
        fun onListen(listener1: ListenerRegistration?)
        fun onSuccess(obj: T?)
        fun onFailure(reason: String?)
    }

    interface GetListener2<T> {
        fun onSuccess(obj: T)
        fun onNoData()
        fun onFailure(reason: String?)
    }

    interface GetAllListener<T> {
        fun onListenerAttached(listenerRegistration: ListenerRegistration?)
        fun onNew(key: String?, obj: T)
        fun onChange(key: String?, obj: T)
        fun onNoData()
        fun onRemove(key: String?)
        fun onFailure(reason: String?)
    }

    interface GetAllListener2<T> {
        fun onListenerAttached(listenerRegistration: ListenerRegistration?)
        fun onNew(key: String?, obj: T)
        fun onChange(key: String?, obj: T)
        fun onFinish()
        fun onNoData()
        fun onRemove(key: String?)
        fun onFailure(reason: String?)
    }

    interface GetAllListListener<T> {
        fun onSuccess(list: List<T>?)
        fun onNoData()
        fun onFailure(reason: String?)
    }


    init {
        table = Firebase.firestore.collection(collections.name)
    }
}