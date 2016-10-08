package ua.com.amicablesoft.phonebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ua.com.amicablesoft.phonebook.dal.Repository;
import ua.com.amicablesoft.phonebook.model.Contact;
import ua.com.amicablesoft.phonebook.service.DeleteContactService;
import ua.com.amicablesoft.phonebook.service.EditContactService;

public class EditContactActivity extends AppCompatActivity
        implements DeleteContactDialogFragment.DeleteContactDialogListener,
        ChangePhotoDialogFragment.ChangePhotoDialogListener {

    private String id;
    private ImageView imageView;
    private String picturePath;
    private static final int PERMISSIONS_REQUEST = 1;
    private static final int REQUEST_PHOTO_CAPTURE = 0;
    private static final int CHOOSE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        imageView = (ImageView) findViewById(R.id.edit_contact_image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangePhotoDialogFragment().show(getFragmentManager(), "single_choice");
            }
        });
        setContact();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                new DeleteContactDialogFragment().show(getFragmentManager(), "dialog");
                return true;
            case R.id.action_done:
                attemptSaveEditedContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOKButtonClick() {
        Intent intent = new Intent(this, DeleteContactService.class);
        intent.putExtra("id", id);
        startService(intent);
        finish();
    }

    @Override
    public void onTakePhotoClick() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED)) {
            try {
                takePhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(EditContactActivity.this,
                    new String[] {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onChoosePhotoClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select photo"), CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                takePhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(findViewById(R.id.activity_new_contact), R.string.snackbar_permissions,
                    Snackbar.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Picasso.with(getApplicationContext()).load(new File(picturePath))
                        .resize(0, imageView.getLayoutParams().height).into(imageView);
            }
        }
    }

    private void setContact() {
        Repository repository = new Repository(getApplicationContext());
        ArrayList<Contact> contacts = repository.findContactsbyId(id);
        Contact contact = contacts.get(0);
        TextInputEditText nameView = (TextInputEditText) findViewById(R.id.edit_name_edit_text);
        nameView.setText(contact.getName());
        TextInputEditText lastNameView = (TextInputEditText) findViewById(R.id.edit_last_name_edit_text);
        lastNameView.setText(contact.getLastName());
        TextInputEditText phoneView = (TextInputEditText) findViewById(R.id.edit_phone_edit_text);
        phoneView.setText(contact.getPhone());
        if (contact.getPhotoPath() != null) {
            Picasso.with(getApplicationContext()).load(new File(contact.getPhotoPath()))
                    .resize(0, imageView.getLayoutParams().height).into(imageView);
        }
    }

    private void attemptSaveEditedContact() {
        TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.edit_name_input_layout);
        nameLayout.setError(null);
        TextInputLayout lastNameLayout = (TextInputLayout) findViewById(R.id.edit_last_name_input_layout);
        lastNameLayout.setError(null);
        TextInputLayout phoneLayout = (TextInputLayout) findViewById(R.id.edit_phone_input_layout);
        phoneLayout.setError(null);
        TextInputEditText nameView = (TextInputEditText) findViewById(R.id.edit_name_edit_text);
        String name = nameView.getText().toString();
        TextInputEditText lastNameView = (TextInputEditText) findViewById(R.id.edit_last_name_edit_text);
        String lastName = lastNameView.getText().toString();
        TextInputEditText phoneView = (TextInputEditText) findViewById(R.id.edit_phone_edit_text);
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
            Intent intent = new Intent(this, EditContactService.class);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("last_name", lastName);
            intent.putExtra("phone", phone);
            intent.putExtra("photo_path", picturePath);
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

    private void takePhoto() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoPath = getApplicationContext()
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            assert photoPath != null;
            if (!photoPath.exists()) {
                photoPath.mkdirs();
            }
            String fileName = createPictureName();
            File photoFile = new File(photoPath, fileName);
            picturePath = photoFile.getAbsolutePath();
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    "ua.com.amicablesoft.phonebook.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION & Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);
        }
    }

    private String createPictureName() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return timeStamp + ".jpg";
    }
}
