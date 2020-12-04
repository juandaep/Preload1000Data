package com.example.mypreloaddata.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns._ID
import com.example.mypreloaddata.DatabaseContract.MahasiswaColumns.Companion.NAMA
import com.example.mypreloaddata.DatabaseContract.MahasiswaColumns.Companion.NIM
import com.example.mypreloaddata.DatabaseContract.TABLE_NAME
import com.example.mypreloaddata.model.MahasiswaModel

class MahasiswaHelper(context: Context) {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private var INSTANCE: MahasiswaHelper? = null

        fun getInstance(context: Context): MahasiswaHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MahasiswaHelper(context)
                    }
                }
            }

            return INSTANCE as MahasiswaHelper
        }
    }

    //untuk membuka dan menutup koneksi dari database yang sudah dibuat.
    @Throws(SQLiteException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun getAllData(): ArrayList<MahasiswaModel> {
        val cursor = database.query(TABLE_NAME, null, null, null, null, null, "$_ID ASC", null)
        cursor.moveToFirst()
        val arrayList = ArrayList<MahasiswaModel>()
        var mahasiswaModel: MahasiswaModel
        if (cursor.count > 0) {
            do {
                mahasiswaModel = MahasiswaModel()
                mahasiswaModel.id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                mahasiswaModel.name = cursor.getString(cursor.getColumnIndexOrThrow(NAMA))
                mahasiswaModel.nim = cursor.getString(cursor.getColumnIndexOrThrow(NIM))

                arrayList.add(mahasiswaModel)
                cursor.moveToNext()
            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    //menambahkan perintah terhadap database yang sudah dibuat
    // sebelumnya seperti membaca dan menulis ke database mahasiswa.
    fun insert(mahasiswaModel: MahasiswaModel): Long {
        val initialValues = ContentValues()
        initialValues.put(NAMA, mahasiswaModel.name)
        initialValues.put(NIM, mahasiswaModel.nim)
        return database.insert(TABLE_NAME, null, initialValues)
    }

    //menambahkan kode berikut untuk melakukan pencarian berdasarkan nama.
    // Ini akan berguna ketika Anda membutuhkannya.
    fun getDataByName(nama: String): ArrayList<MahasiswaModel> {
        val cursor = database.query(
            TABLE_NAME,
            null,
            "$NAMA LIKE ?",
            arrayOf(nama),
            null,
            null,
            "$_ID ASC",
            null)
        cursor.moveToFirst()
        val arrayList = ArrayList<MahasiswaModel>()
        var mahasiswaModel: MahasiswaModel
        if (cursor.count > 0) {
            do {
                mahasiswaModel = MahasiswaModel()
                mahasiswaModel.id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                mahasiswaModel.name = cursor.getString(cursor.getColumnIndexOrThrow(NAMA))
                mahasiswaModel.nim = cursor.getString(cursor.getColumnIndexOrThrow(NIM))

                arrayList.add(mahasiswaModel)
                cursor.moveToNext()
            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    fun beginTransaction() {
        database.beginTransaction()
    }

    fun setTransactionSuccess() {
        database.setTransactionSuccessful()
    }

    fun endTransaction() {
        database.endTransaction()
    }

    fun insertTransaction(mahasiswaModel: MahasiswaModel) {
        val sql = ("INSERT INTO $TABLE_NAME ($NAMA, $NIM) VALUES (?, ?)" )
        val stmt = database.compileStatement(sql)
        stmt.bindString(1, mahasiswaModel.name)
        stmt.bindString(2, mahasiswaModel.nim)
        stmt.execute()
        stmt.clearBindings()
    }
}