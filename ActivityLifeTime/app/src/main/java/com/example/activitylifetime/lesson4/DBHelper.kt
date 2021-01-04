
import android.content.Context
import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper


class DBHelper  //数据库版本号
    (context: Context?) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {

        // 创建两个表格:用户表 和职业表
        db.execSQL("CREATE TABLE IF NOT EXISTS $USER_TABLE_NAME(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $JOB_TABLE_NAME(_id INTEGER PRIMARY KEY AUTOINCREMENT, job TEXT)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    companion object {
        // 数据库名
        private const val DATABASE_NAME = "finch.db"

        // 表名
        const val USER_TABLE_NAME = "user"
        const val JOB_TABLE_NAME = "job"
        private const val DATABASE_VERSION = 1
    }
}