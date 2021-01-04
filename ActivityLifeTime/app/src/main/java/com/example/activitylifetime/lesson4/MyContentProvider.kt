package com.example.activitylifetime.lesson4

import DBHelper
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri


class MyContentProvider : ContentProvider() {
    private var mContext: Context? = null
    var mDbHelper: DBHelper? = null
    var db: SQLiteDatabase? = null

    private val AUTOHORITY = "com.example.provider"
    // 设置ContentProvider的唯一标识

    // 设置ContentProvider的唯一标识
    private val User_Code = 1
    private val Job_Code = 2

    // UriMatcher类使用:在ContentProvider 中注册URI
    private var mMatcher: UriMatcher? = null

    init {
        mMatcher = UriMatcher(UriMatcher.NO_MATCH)
        // 初始化
        mMatcher!!.addURI(AUTOHORITY, "user", User_Code)
        mMatcher!!.addURI(AUTOHORITY, "job", Job_Code)
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
        // 该方法在最下面
        // 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
        // 该方法在最下面
        val table: String? = getTableName(uri)

        // 向该表添加数据

        // 向该表添加数据
        db?.insert(table, null, values)

        // 当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）

        // 当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）
        mContext?.contentResolver?.notifyChange(uri, null)

//        // 通过ContentUris类从URL中获取ID
//        long personid = ContentUris.parseId(uri);
//        System.out.println(personid);


//        // 通过ContentUris类从URL中获取ID
//        long personid = ContentUris.parseId(uri);
//        System.out.println(personid);
        return uri
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
        // 该方法在最下面
        val table = getTableName(uri)

//         通过ContentUris类从URL中获取ID
//        long personid = ContentUris.parseId(uri);
//        System.out.println(personid);

        // 查询数据
        return db!!.query(table, projection, selection, selectionArgs, null, null, sortOrder, null)
    }

    override fun onCreate(): Boolean {
        mContext = getContext();
        // 在ContentProvider创建时对数据库进行初始化
        // 运行在主线程，故不能做耗时操作,此处仅作展示
        mDbHelper = DBHelper(getContext());
        db = mDbHelper?.getWritableDatabase();

        // 初始化两个表的数据(先清空两个表,再各加入一个记录)
        db?.execSQL("delete from user");
        db?.execSQL("insert into user values(1,'Carson');");
        db?.execSQL("insert into user values(2,'Kobe');");

        db?.execSQL("delete from job");
        db?.execSQL("insert into job values(1,'Android');");
        db?.execSQL("insert into job values(2,'iOS');");

        return true;
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    /**
     * 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
     */
    private fun getTableName(uri: Uri): String? {
        var tableName: String? = null
        when (mMatcher?.match(uri)) {
            User_Code ->
                tableName = DBHelper.USER_TABLE_NAME
            Job_Code ->
                tableName = DBHelper.JOB_TABLE_NAME
        }
        return tableName
    }
}