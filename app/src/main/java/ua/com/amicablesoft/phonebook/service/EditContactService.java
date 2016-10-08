package ua.com.amicablesoft.phonebook.service;

import android.app.IntentService;
import android.content.Intent;

import ua.com.amicablesoft.phonebook.dal.Repository;

/**
 * Created by lapa on 06.10.16.
 */

public class EditContactService extends IntentService {

    public EditContactService() {
        super("EditContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String lastName = intent.getStringExtra("last_name");
        String phone = intent.getStringExtra("phone");
        String photoPath = intent.getStringExtra("photo_path");
        Repository repository = new Repository(getApplicationContext());
        repository.updateContact(id, name, lastName, phone, photoPath);
    }
}
