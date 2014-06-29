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

import com.stacservices.stealthmessenger.db.SsmsAccount;
import com.stacservices.stealthmessenger.db.Ssms.Ssmss;
import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;

public class SSMSProvider extends ContentProvider {

    private static final String TAG = "SSMSProvider";

    private static final String DATABASE_NAME = "ssms.db";

    private static final int DATABASE_VERSION = 1;

    public static final String SSMS_TABLE_NAME = "ssms";

    public static final String AUTHORITY = 
    	"com.stacservices.stealthmessenger.providers.SSMSProvider";

    private static final UriMatcher sUriMatcher;

    private static final int SSMS = 1;

    private static HashMap<String, String> ssmsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SSMS_TABLE_NAME + " (" + Ssmss.SSMS_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                    + Ssmss.SSMSACCT_ID + " INTEGER,"
                    + Ssmss.ADDRESS + " VARCHAR(255)," 
                    + Ssmss.DATE + " VARCHAR(255),"
                    + Ssmss.READ + " BOOLEAN,"
                    + Ssmss.STATUS + " VARCHAR(255),"
                    + Ssmss.TYPE + " VARCHAR(255),"
                    + Ssmss.SEEN + " BOOLEAN,"
                    + Ssmss.BODY + " TEXT)");
                        
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
                return Ssmss.CONTENT_TYPE;

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
        long rowId = db.insert(SSMS_TABLE_NAME, Ssmss.BODY, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Ssmss.CONTENT_URI, rowId);
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
    {
        	
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

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SSMS_TABLE_NAME, SSMS);

        ssmsProjectionMap = new HashMap<String, String>();
        ssmsProjectionMap.put(Ssmss.SSMS_ID, Ssmss.SSMS_ID);
        ssmsProjectionMap.put(Ssmss.SSMSACCT_ID, Ssmss.SSMSACCT_ID);        
        ssmsProjectionMap.put(Ssmss.ADDRESS, Ssmss.ADDRESS);
        ssmsProjectionMap.put(Ssmss.DATE, Ssmss.DATE);
        ssmsProjectionMap.put(Ssmss.READ, Ssmss.READ);
        ssmsProjectionMap.put(Ssmss.STATUS, Ssmss.STATUS);
        ssmsProjectionMap.put(Ssmss.TYPE, Ssmss.TYPE);        
        ssmsProjectionMap.put(Ssmss.SEEN, Ssmss.SEEN);
        ssmsProjectionMap.put(Ssmss.BODY, Ssmss.BODY);        

    }
}
