package ua.com.amicablesoft.phonebook.dal;

import android.provider.BaseColumns;

/**
 * Created by lapa on 04.10.16.
 */

public final class PhoneBookContract {

    private PhoneBookContract() {}

    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_CONTACT_NAME = "contact_name";
        public static final String COLUMN_NAME_CONTACT_LAST_NAME = "contact_last_name";
        public static final String COLUMN_NAME_CONTACT_PHONE = "contact_phone";
        public static final String COLUMN_NAME_CONTACT_PHOTO_PATH = "contact_photo_path";
    }
}
