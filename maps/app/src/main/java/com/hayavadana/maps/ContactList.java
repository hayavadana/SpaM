package com.hayavadana.maps;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ContactList extends AppCompatActivity {

    DBHelper mydb;
    ContactListItemAdapter contactListItemAdapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);

        mydb = new DBHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cltoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MapsActivity.allContacts = mydb.getAllContacts();
        contactListItemAdapter = new ContactListItemAdapter(ContactList.this,R.layout.contact_list_item, MapsActivity.allContacts);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(contactListItemAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                MapsActivity.currentMode = MapsActivity.CONTACT_SELECTED_VIEW;
                int idToSearch = MapsActivity.allContacts.get(arg2).getId();

                Bundle dataBundle = new Bundle();
                dataBundle.putDoubleArray("locData", getSelectedLocation(idToSearch));
                dataBundle.putString("contName", MapsActivity.allContacts.get(arg2).getContactName());

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(dataBundle);
                startActivity(intent);


            }
        });

        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            // setting onItemLongClickListener and passing the position to the function
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {

                removeItemFromList(index);
                contactListItemAdapter.notifyDataSetChanged();


                Toast.makeText(getApplicationContext(), "Long press event", Toast.LENGTH_SHORT).show();


                return true;
            }
        });

    }
    // method to remove list item
    protected void removeItemFromList(final int index) {
        final int deletePosition = index;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                ContactList.this);
        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this Contact?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                // main code on after clicking yes
                mydb.deleteContact(MapsActivity.allContacts.get(index).getId(), getApplicationContext());
                MapsActivity.allContacts = mydb.getAllContacts();

                contactListItemAdapter.clear();
                contactListItemAdapter.addAll(MapsActivity.allContacts);
                // fire the event
                contactListItemAdapter.notifyDataSetChanged();
            contactListItemAdapter.notifyDataSetInvalidated();

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        alert.show();

    }



    @Override
    protected void onResume() {
        super.onResume();


    }

    private double[] getSelectedLocation(int contId){
        double [ ] latlng = new double [2];
                Cursor rs = mydb.getData(contId);
                rs.moveToFirst();

                String lati = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_Latitude));
                latlng[0] = Double.parseDouble(lati);
                String lngi= rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_Longitude));
                latlng[1] = Double.parseDouble(lngi);


                return latlng;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

