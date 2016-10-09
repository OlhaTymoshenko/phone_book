package ua.com.amicablesoft.phonebook;

import android.Manifest;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.phonebook.model.Contact;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private Contact contact;
    private static final int LOADER_ID = 1;
    public static final String BROADCAST_ACTION = "contact_is_deleted";
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewContactActivity.class);
                startActivity(intent);
            }
        });
        final ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext());
        itemAdapter.setOnContactEditListener(new ItemAdapter.OnContactEditListener() {
            @Override
            public void onContactEdit(Contact contact) {
                Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
                String id = contact.getId();
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        itemAdapter.setOnContactCallListener(new ItemAdapter.OnContactCallListener() {
            @Override
            public void onContactCall(Contact contact) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
                    callToContact(contact);
                } else {
                    MainActivity.this.contact = contact;
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST);
                }

            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<ArrayList<Contact>>() {
            @Override
            public Loader<ArrayList<Contact>> onCreateLoader(int id, Bundle args) {
                return new ContactsLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Contact>> loader, ArrayList<Contact> data) {
                TextView textView = (TextView) findViewById(R.id.empty_view);
                if (data.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }
                itemAdapter.setContacts(data);
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Contact>> loader) {

            }
        });
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Snackbar.make(findViewById(R.id.activity_main), R.string.snackbar_deleted_contact,
                        Snackbar.LENGTH_SHORT).show();
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("contact", contact);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutDeveloperActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callToContact(contact);
            contact = null;
        } else {
            Snackbar.make(findViewById(R.id.activity_new_contact), R.string.snackbar_permissions,
                    Snackbar.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void callToContact(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact.getPhone()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Created by lapa on 04.10.16.
     */
    public static class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<Contact> contacts = new ArrayList<>();
        private OnContactEditListener onContactEditListener;
        private OnContactCallListener onContactCallListener;
        private Context context;
        private final int ITEM_TYPE_GROUP = 0;
        private final int ITEM_TYPE_FOOTER = 1;

        public ItemAdapter(Context context) {
            this.context = context;
        }

        public void setOnContactEditListener(OnContactEditListener onContactEditListener) {
            this.onContactEditListener = onContactEditListener;
        }

        public void setOnContactCallListener(OnContactCallListener onContactCallListener) {
            this.onContactCallListener = onContactCallListener;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            final ImageView circleImageView;
            final TextView textView;
            final ImageView editImageView;
            Contact contact;

            public ItemViewHolder(View view) {
                super(view);
                circleImageView = (ImageView) view.findViewById(R.id.contact_photo);
                textView = (TextView) view.findViewById(R.id.item_text_view);
                editImageView = (ImageView) view.findViewById(R.id.edit_image_view);
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public interface OnContactEditListener {
            void onContactEdit(Contact contact);
        }

        public interface OnContactCallListener {
            void onContactCall(Contact contact);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_GROUP) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_for_recycle_view, parent, false);
                final ItemViewHolder holder = new ItemViewHolder(view);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onContactCallListener.onContactCall(holder.contact);
                    }
                });
                holder.editImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onContactEditListener.onContactEdit(holder.contact);
                    }
                });
                return holder;
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.footer_for_recycler_view, parent, false);
                return new FooterViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Contact contact = contacts.get(position);
                String photoPath = contact.getPhotoPath();
                if (photoPath != null) {
                    Picasso.with(context).load(new File(photoPath))
                            .resize(itemViewHolder.circleImageView.getLayoutParams().width,
                                    itemViewHolder.circleImageView.getLayoutParams().height)
                            .centerCrop().transform(CropCircleTransformation.INSTANCE)
                            .into(itemViewHolder.circleImageView);
                } else {
                    itemViewHolder.circleImageView.setImageResource(R.drawable.ic_account_circle_grey600_36dp);
                }
                String fullName = contact.getName() + " " + contact.getLastName();
                itemViewHolder.textView.setText(fullName);
                itemViewHolder.contact = contact;
            }
        }

        @Override
        public int getItemCount() {
            if (contacts.size() > 0) {
                return contacts.size() + 1;
            } else {
                return contacts.size();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (contacts.size() != 0 && contacts.size() == position) {
                return ITEM_TYPE_FOOTER;
            } else {
                return ITEM_TYPE_GROUP;
            }
        }

        public void setContacts(ArrayList<Contact> contacts) {
            this.contacts.clear();
            this.contacts.addAll(contacts);
        }
    }
}
