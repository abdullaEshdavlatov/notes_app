package uz.abdulla.notes.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DatabaseConfig.DATABASE_NAME,null, DatabaseConfig.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE ${DatabaseConfig.TABLE_NAME}(${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, ${DatabaseConfig.TITLE_COL} TEXT," +
                " ${DatabaseConfig.DESCRIPTION_COL} TEXT, ${DatabaseConfig.COLOR_COL} INTEGER, ${DatabaseConfig.CREATED_DATE_COL} TEXT, ${DatabaseConfig.CHECK_NOTIFICATION} INTEGER)"
        db!!.execSQL(createTableQuery)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConfig.DATABASE_NAME}")
    }

    fun insertNote(title: String, description: String, color: Int, createdDate: String, notification: Int): Boolean{
        val db:SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseConfig.TITLE_COL,title)
        contentValues.put(DatabaseConfig.DESCRIPTION_COL,description)
        contentValues.put(DatabaseConfig.COLOR_COL,color)
        contentValues.put(DatabaseConfig.CREATED_DATE_COL,createdDate)
        contentValues.put(DatabaseConfig.CHECK_NOTIFICATION,notification)
        val isInserted = db.insert(DatabaseConfig.TABLE_NAME,null,contentValues)
        return isInserted == -1L
    }

    fun readNote(): Cursor?{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseConfig.TABLE_NAME}",null)
        return cursor
    }
    fun deleteNote(id:String): Boolean{
        val db:SQLiteDatabase = this.writableDatabase
        val deleteNoteData = db.delete(DatabaseConfig.TABLE_NAME,"${BaseColumns._ID} = ?", arrayOf(id))
        return deleteNoteData != -1
    }

    fun updateNote(id:String, title: String, description: String): Boolean{
        val db:SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseConfig.TITLE_COL,title)
        contentValues.put(DatabaseConfig.DESCRIPTION_COL,description)
        val updateNote = db.update(DatabaseConfig.TABLE_NAME,contentValues,"${BaseColumns._ID} = ?", arrayOf(id))
        return updateNote != -1
    }
    fun updateNote(id: String, notification: Int): Boolean{
        val db:SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseConfig.CHECK_NOTIFICATION,notification)
        val updateNote = db.update(DatabaseConfig.TABLE_NAME,contentValues,"${BaseColumns._ID} = ?", arrayOf(id))
        return updateNote != -1
    }
    fun updateColor(id: String, color: Int): Boolean{
        val db:SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseConfig.COLOR_COL,color)
        val updateNote = db.update(DatabaseConfig.TABLE_NAME,contentValues,"${BaseColumns._ID} = ?", arrayOf(id))
        return updateNote != -1
    }
}