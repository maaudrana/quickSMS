package quick.sms.quicksms.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseTiles(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, 1){

    // TODO: Android Studio wanted db to have type SQLiteDatabase?
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (recipient_id LONG PRIMARY KEY,tileid INTEGER, prefered_number INTEGER)")
    }

    // TODO: Same Here
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(recipient_id: Long, tileid: Int, prefered_number: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, recipient_id)
        contentValues.put(COL_2, tileid)
        contentValues.put(COL_3, prefered_number)
        db.insert(TABLE_NAME, null, contentValues)
    }

    // TODO: Using null instead of -1 for failure would allow the type system to enforce error checking
    private fun getRecipient(tileid: Int): Long {
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                if (tileid.toString() == it.getString(it.getColumnIndex("tileid"))) {
                    return it.getLong(it.getColumnIndex("recipient_id"))
                }
            }
        }
        return -1L
    }

    fun getAllTiles(): Map<Long, Int> {
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            val tiles = linkedMapOf<Long, Int>()
            while (it.moveToNext()) {
                tiles[it.getLong(it.getColumnIndex("recipient_id"))] = it.getInt(it.getColumnIndex("tileid"))
            }
            return tiles
        }
    }

    fun copyData(fromTileId: Int, toTileId: Int) {

    }

    // TODO: This is just a specialisation on getAllTiles, there may be something that can be done with that
    fun getTile(recipient_id: Long): Int {
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                if (recipient_id.toString() == it.getString(it.getColumnIndex("recipient_id"))) {
                    return it.getInt(it.getColumnIndex("tileid"))
                }
            }
        }
        return -1
    }

    fun deleteTile(tileid: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "tileid = ?", arrayOf(tileid.toString()))
    }

    fun deleteEntireDB() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }

    companion object {
        private const val DATABASE_NAME = "TilesMapping.db"
        private const val TABLE_NAME = "TilesMappingTable"
        private const val COL_1 = "recipient_id"
        private const val COL_2 = "tileid"
        private const val COL_3 = "prefered_number"
    }
}