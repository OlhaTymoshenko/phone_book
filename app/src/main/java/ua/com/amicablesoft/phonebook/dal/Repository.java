package ua.com.amicablesoft.phonebook.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ua.com.amicablesoft.phonebook.ui.ContactsUpdatedEvent;
import ua.com.amicablesoft.phonebook.model.Contact;
import ua.com.amicablesoft.phonebook.model.User;

/**
 * Created by lapa on 04.10.16.
 */

public class Repository {

    private PhoneBookDBHelper phoneBookDBHelper;
    private Context context;

    public Repository(Context context) {
        phoneBookDBHelper = new PhoneBookDBHelper(context);
        this.context = context;
    }

    public ArrayList<Contact> findContacts() {
        SQLiteDatabase database = phoneBookDBHelper.getReadableDatabase();
        Cursor cursor = database.query(
                PhoneBookContract.ContactEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<Contact> contacts = readContactsFromCursor(cursor);
        cursor.close();
        database.close();
        return contacts;
    }

    public ArrayList<Contact> readContactsFromCursor(Cursor cursor){
        ArrayList<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHONE));
            String photoPath = null;
            if (!cursor.isNull(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHOTO_PATH))) {
                photoPath = cursor.getString(cursor.getColumnIndexOrThrow
                        (PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHOTO_PATH));
            }
            Contact contact = new Contact(id, name, lastName, phone, photoPath);
            contacts.add(contact);
        }
        return contacts;
    }

    public void addContact(Contact contact) {
        SQLiteDatabase database = phoneBookDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_ID, contact.getId());
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_NAME, contact.getName());
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_LAST_NAME, contact.getLastName());
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHONE, contact.getPhone());
        if (contact.getPhotoPath() != null) {
            values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHOTO_PATH, contact.getPhotoPath());
        }
        database.insert(PhoneBookContract.ContactEntry.TABLE_NAME, null, values);
        database.close();
        EventBus.getDefault().post(new ContactsUpdatedEvent());
    }

    public ArrayList<Contact> findContactsbyId(String id) {
        SQLiteDatabase database = phoneBookDBHelper.getReadableDatabase();
        String selection = PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_ID + " =?";
        String[] selectionArgs = {id};
        Cursor cursor = database.query(
                PhoneBookContract.ContactEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        ArrayList<Contact> contacts = readContactsFromCursor(cursor);
        cursor.close();
        database.close();
        return contacts;
    }

    public void updateContact(String id, String name, String lastName, String phone, String photoPath) {
        SQLiteDatabase database = phoneBookDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_NAME, name);
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_LAST_NAME, lastName);
        values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHONE, phone);
        if (photoPath != null) {
            values.put(PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_PHOTO_PATH, photoPath);
        }
        String whereClause = PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_ID + " =?";
        String[] whereArgs = {id};
        database.update(PhoneBookContract.ContactEntry.TABLE_NAME, values, whereClause, whereArgs);
        database.close();
        EventBus.getDefault().post(new ContactsUpdatedEvent());
    }

    public void deleteContact(String id) {
        SQLiteDatabase database = phoneBookDBHelper.getWritableDatabase();
        String whereClause = PhoneBookContract.ContactEntry.COLUMN_NAME_CONTACT_ID + " =?";
        String[] whereArgs = {id};
        database.delete(PhoneBookContract.ContactEntry.TABLE_NAME, whereClause, whereArgs);
        database.close();
        EventBus.getDefault().post(new ContactsUpdatedEvent());
    }

    public User findUser() {
        SQLiteDatabase database = phoneBookDBHelper.getReadableDatabase();
        Cursor cursor = database.query(
                PhoneBookContract.UserEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        User user = readUserFromCursor(cursor);
        cursor.close();
        database.close();
        return user;
    }

    public User readUserFromCursor(Cursor cursor){
        ArrayList<User> users = new ArrayList<>();
        while (cursor.moveToNext()) {
            String login = cursor.getString(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.UserEntry.COLUMN_NAME_LOGIN));
            String password = cursor.getString(cursor.getColumnIndexOrThrow
                    (PhoneBookContract.UserEntry.COLUMN_NAME_PASSWORD));
            User user = new User(login, password);
            users.add(user);
        }
        return users.get(0);
    }
}
