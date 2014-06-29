package com.stacservices.stealthmessenger.providers;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;;

public class SSMSAccountProvider extends ContentProvider {

    private static final String TAG = "SSMSAccountProvider";

    private static final String DATABASE_NAME = "ssmsaccount.db";

    private static final int DATABASE_VERSION = 1;

    public static final String SSMS_TABLE_NAME = "ssmsaccount";

    public static final String AUTHORITY = 
    	"com.stacservices.stealthmessenger.providers.SSMSAccountProvider";

    private static final UriMatcher sUriMatcher;

    private static final int SSMS = 1;

    private static HashMap<String, String> ssmsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SSMS_TABLE_NAME + " (" + SsmsAccounts.SSMSACCT_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                    + SsmsAccounts.SSMSCT_ID + " INTEGER,"
                    + SsmsAccounts.SSMSACCT_NAME + " VARCHAR(255),"
                    + SsmsAccounts.SSMSACCT_PHONE + " VARCHAR(255),"                    
                    + SsmsAccounts.SSMSPIN + " VARCHAR(255)," 
                    + SsmsAccounts.SSMSACCT_RECEIVE_ENC + " VARCHAR(255), " 
                    + SsmsAccounts.SSMSACCT_SEND_ENC + " VARCHAR(255),"
                    + SsmsAccounts.SSMSACCT_ENC_KEY + " VARCHAR(255))");
                        
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SSMS_TABLE_NAME);
            onCreate(db);
        }
    }

	private DatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SSMS:
                count = db.delete(SSMS_TABLE_NAME, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SSMS:
                return SsmsAccounts.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != SSMS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(SSMS_TABLE_NAME, SsmsAccounts.SSMSPIN, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(SsmsAccounts.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case SSMS:
                qb.setTables(SSMS_TABLE_NAME);
                qb.setProjectionMap(ssmsProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    public Cursor selectAccount(Uri uri, String pin) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case SSMS:
                qb.setTables(SSMS_TABLE_NAME);
                qb.setProjectionMap(ssmsProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String mySql = "SELECT * from " + SSMS_TABLE_NAME + " where " + SsmsAccounts.SSMSPIN + "='" + pin + "'";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(mySql, null);
        return c;
    }    

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SSMS:
                count = db.update(SSMS_TABLE_NAME, values, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static 
    {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SSMS_TABLE_NAME, SSMS);

        ssmsProjectionMap = new HashMap<String, String>();
        ssmsProjectionMap.put(SsmsAccounts.SSMSACCT_ID, SsmsAccounts.SSMSACCT_ID);
        ssmsProjectionMap.put(SsmsAccounts.SSMSCT_ID, SsmsAccounts.SSMSCT_ID);
        ssmsProjectionMap.put(SsmsAccounts.SSMSACCT_NAME, SsmsAccounts.SSMSACCT_NAME);
        ssmsProjectionMap.put(SsmsAccounts.SSMSACCT_PHONE, SsmsAccounts.SSMSACCT_PHONE);
        ssmsProjectionMap.put(SsmsAccounts.SSMSPIN, SsmsAccounts.SSMSPIN);
        ssmsProjectionMap.put(SsmsAccounts.SSMSACCT_RECEIVE_ENC, SsmsAccounts.SSMSACCT_RECEIVE_ENC);
        ssmsProjectionMap.put(SsmsAccounts.SSMSACCT_SEND_ENC, SsmsAccounts.SSMSACCT_SEND_ENC);
        ssmsProjectionMap.put(SsmsAccounts.SSMSACCT_ENC_KEY, SsmsAccounts.SSMSACCT_ENC_KEY);

    }
}
