package ua.com.amicablesoft.phonebook;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import ua.com.amicablesoft.phonebook.dal.Repository;
import ua.com.amicablesoft.phonebook.model.Contact;

/**
 * Created by lapa on 04.10.16.
 */

public class ContactsLoader extends AsyncTaskLoader<ArrayList<Contact>> {

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Contact> loadInBackground() {
        Repository repository = new Repository(getContext());
        return repository.findContacts();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStopLoading() {
        EventBus.getDefault().unregister(this);
        super.onStopLoading();
    }

    @Subscribe
    public void onContactsUpdatedEvent(ContactsUpdatedEvent event) {
        forceLoad();
    }
}
