package ua.com.amicablesoft.phonebook;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.com.amicablesoft.phonebook.model.Contact;

public class MainActivity extends AppCompatActivity {

    private static final int LOADER_ID = 1;

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
        final ItemAdapter itemAdapter = new ItemAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        }));
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<ArrayList<Contact>>() {
            @Override
            public Loader<ArrayList<Contact>> onCreateLoader(int id, Bundle args) {
                return new ContactsLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Contact>> loader, ArrayList<Contact> data) {
                itemAdapter.setContacts(data);
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Contact>> loader) {

            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Created by lapa on 04.10.16.
     */
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private final List<Contact> contacts = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            final CircleImageView circleImageView;
            final TextView textView;
            Contact contact;

            public ViewHolder(View view) {
                super(view);
                circleImageView = (CircleImageView) view.findViewById(R.id.contact_photo);
                textView = (TextView) view.findViewById(R.id.item_text_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_for_recycle_view, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = contacts.get(holder.getAdapterPosition());
                    Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
                    String id = contact.getId();
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            if (contact.getPhotoPath() != null) {
                Uri uri = new Uri.Builder().path(contact.getPhotoPath()).build();
                holder.circleImageView.setImageURI(uri);
            } else {
                holder.circleImageView.setImageResource(R.drawable.ic_account_circle_grey600_36dp);
            }
            String fullName = contact.getName() + " " + contact.getLastName();
            holder.textView.setText(fullName);
            holder.contact = contact;
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public void setContacts(ArrayList<Contact> contacts) {
            this.contacts.clear();
            this.contacts.addAll(contacts);
        }

        public Contact getContact(int position) {
            return contacts.get(position);
        }
    }
}
