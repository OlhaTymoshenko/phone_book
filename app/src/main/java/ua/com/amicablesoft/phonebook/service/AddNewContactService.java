package ua.com.amicablesoft.phonebook.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.UUID;

import ua.com.amicablesoft.phonebook.dal.Repository;
import ua.com.amicablesoft.phonebook.model.Contact;

/**
 * Created by lapa on 05.10.16.
 */

public class AddNewContactService extends IntentService {

    public AddNewContactService() {
        super("AddNewContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra("name");
        String lastName = intent.getStringExtra("last_name");
        String phone = intent.getStringExtra("phone");
        String id = UUID.randomUUID().toString();
        Contact contact = new Contact(id, name, lastName, phone, null);
        Repository repository = new Repository(getApplicationContext());
        repository.addContact(contact);
    }
}
