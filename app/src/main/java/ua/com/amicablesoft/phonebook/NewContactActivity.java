package ua.com.amicablesoft.phonebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class NewContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_new_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_save:
                attemptSaveNewContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSaveNewContact() {
        TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.name_input_layout);
        nameLayout.setError(null);
        TextInputLayout lastNameLayout = (TextInputLayout) findViewById(R.id.last_name_input_layout);
        lastNameLayout.setError(null);
        TextInputLayout phoneLayout = (TextInputLayout) findViewById(R.id.phone_input_layout);
        phoneLayout.setError(null);
        TextInputEditText nameView = (TextInputEditText) findViewById(R.id.name_edit_text);
        String name = nameView.getText().toString();
        TextInputEditText lastNameView = (TextInputEditText) findViewById(R.id.last_name_edit_text);
        String lastName = lastNameView.getText().toString();
        TextInputEditText phoneView = (TextInputEditText) findViewById(R.id.phone_edit_text);
        String phone = phoneView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError(getString(R.string.error_required_field));
            focusView = phoneLayout;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            phoneLayout.setError(getString(R.string.error_invalid_phone));
            focusView = phoneLayout;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameLayout.setError(getString(R.string.error_required_field));
            focusView = lastNameLayout;
            cancel = true;
        } else if (!isLastNameValid(lastName)) {
            lastNameLayout.setError(getString(R.string.error_short_last_name));
            focusView = lastNameLayout;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_required_field));
            focusView = nameLayout;
            cancel = true;
        } else if (!isNameValid(name)) {
            nameLayout.setError(getString(R.string.error_short_name));
            focusView = nameLayout;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent intent = new Intent(this, AddNewContactService.class);
            intent.putExtra("name", name);
            intent.putExtra("last_name", lastName);
            intent.putExtra("phone", phone);
            startService(intent);
            finish();
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 10 || phone.length() == 13;
    }

    private boolean isLastNameValid(String lastName) {
        return lastName.length() > 1;
    }

    private boolean isNameValid(String name) {
        return name.length() > 1;
    }
}