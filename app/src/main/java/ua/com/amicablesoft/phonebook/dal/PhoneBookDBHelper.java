package ua.com.amicablesoft.phonebook.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ua.com.amicablesoft.phonebook.dal.PhoneBookContract.ContactEntry.TABLE_NAME;

/**
 * Created by lapa on 04.10.16.
 */

public class PhoneBookDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "PhoneBook.db";

    public PhoneBookDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + PhoneBookContract.ContactEntry.TABLE_NAME + " (" +
                PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_ID + " TEXT NOT NULL, " +
                PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_NAME + " TEXT NOT NULL, " +
                PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_LAST_NAME +" TEXT NOT NULL, " +
                PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHONE + " TEXT NOT NULL, " +
                PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHOTO_PATH + " TEXT);";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
