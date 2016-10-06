package ua.com.amicablesoft.phonebook;

import android.app.IntentService;
import android.content.Intent;

import ua.com.amicablesoft.phonebook.dal.Repository;

/**
 * Created by lapa on 06.10.16.
 */

public class DeleteContactService extends IntentService{

    public DeleteContactService() {
        super("DeleteContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String id = intent.getStringExtra("id");
        Repository repository = new Repository(getApplicationContext());
        repository.deleteContact(id);
        Intent intent1 = new Intent(MainActivity.BROADCAST_ACTION);
        sendBroadcast(intent1);
    }
}
