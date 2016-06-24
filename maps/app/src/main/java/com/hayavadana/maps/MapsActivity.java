package com.hayavadana.maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements LocationListener, DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    GoogleMap googleMap;


    private final Context mContext;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    String tag = "no provider";
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    LocationManager locationManager;
    Location location;
    double latitude = 999;
    double longitude = 999;
    DBHelper mydb;
    private String streetaddr;
    double staticViewLatitude = 999;
    double staticViewLongitude = 999;
    public static final int NEWCONTACT_LIVE_LOCATION_VIEW = 1;
    public static final int NEWCONTACT_MANUAL_SELECTED_LOCATION_VIEW = 2;
    public static final int CONTACT_SELECTED_VIEW = 3;
    public static final int ALL_CONTACTS_STATIC_VIEW = 4;
    public static ArrayList <ContactInfo> allContacts;
    public static int currentMode = NEWCONTACT_LIVE_LOCATION_VIEW;

    public MapsActivity() {

        mContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = supportMapFragment.getMap();

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        mydb = new DBHelper(this);
        allContacts = mydb.getAllContacts();

        // getting network status

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // getting GPS status

            return;
        }
        googleMap.setPadding(0, 160, 0, 0);
        googleMap.setMyLocationEnabled(true);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestSingleUpdate(bestProvider, this, null);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);

        return;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void getSupportActionBar(Toolbar toolbar) {

    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(MapsActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.eText:
                // search contact
                return true;
            case R.id.iPerson_add:
                // add location to contact

                return true;
            case R.id.iSave:
                // save
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView locationTv = (TextView) findViewById(R.id.latlngLocation);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.clear();
        streetaddr = "Present Location";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 2);
            if (listAddresses != null && listAddresses.size() > 0) {
                streetaddr = listAddresses.get(1).getAddressLine(1).toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        googleMap.addMarker(new MarkerOptions().position(latLng).title(streetaddr));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);

        Toast.makeText(mContext, "Hi Ur location found", Toast.LENGTH_LONG).show();
        stopUsingGPS();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Enable new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void alertMsg(String title, String msg, String posBtnText, String negBtn) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(msg);
        // On pressing Settings button
        alertDialog.setPositiveButton(posBtnText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        // on pressing cancel button
        if (negBtn != null) {
            alertDialog.setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        // Showing Alert Message
        alertDialog.show();

    }


    public void onItem1Click(MenuItem item) {

        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {


            uriContact = data.getData();
            retrieveContactName();
           // retrieveContactPhoto();

        }
    }


    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        EditText eTxt = (EditText) findViewById(R.id.eText);
        eTxt.setText(contactName);
        Toast.makeText(mContext, "Hello", Toast.LENGTH_LONG).show();

    }
    /*private void retrieveContactPhoto() {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = (ImageView)findViewById(R.id.ContactimageView);
                imageView.setImageBitmap(photo);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
*/
    public void onItem2Click(MenuItem item) {
        EditText textField = (EditText) findViewById(R.id.eText);
        String eName = textField.getText().toString();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        double tempLat = 999, tempLong = 999;

        if (currentMode == NEWCONTACT_LIVE_LOCATION_VIEW) {
            tempLat = latitude;
            tempLong = longitude;
        }
        else if (currentMode == NEWCONTACT_MANUAL_SELECTED_LOCATION_VIEW) {
            tempLat = staticViewLatitude;
            tempLong = staticViewLongitude;
        }

        if (tempLat == 999 || tempLong == 999) {
            alertMsg("Error", "GPS or Datanetwork connectivity issue", "OK", null);
        } else {
            if (eName.length() != 0) {
                mydb.insertContact(eName, tempLat, tempLong);
                alertMsg("Info", "Contact inserted successfully", "OK", null);
                EditText myTextView = (EditText) findViewById(R.id.eText);
                myTextView.setText("");

            } else {
                alertMsg("Alert", "Please enter the name of the location", "OK", null);

            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id == R.id.nav_view_item0) { //Home - Live mode
            googleMap.clear();

            currentMode = NEWCONTACT_LIVE_LOCATION_VIEW;

            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }
            locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
            locationManager.requestSingleUpdate(bestProvider, this, null);
        }

        else if (id == R.id.nav_view_item1) {  //List of Contacts

            // Handle the contacts list action
            Intent intent = new Intent(getApplicationContext(), ContactList.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_view_item2) { //Add place from Map
            googleMap.clear();
            currentMode =  NEWCONTACT_MANUAL_SELECTED_LOCATION_VIEW;


            Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();


        } else if (id == R.id.nav_view_item3) {

            Intent intent = new Intent(getApplicationContext(), LatLng_Text_Inputs.class);
            startActivity(intent);
            TextView locationTv = (TextView) findViewById(R.id.latlngLocation);
            locationTv.setText("");
            Toast.makeText(getApplicationContext(),"hai",Toast.LENGTH_LONG).show();
            

        } else if (id == R.id.nav_view_item4) {  //All contacts on map
            googleMap.clear();
            currentMode = ALL_CONTACTS_STATIC_VIEW;
            for(int i = 0; i<allContacts.size();i++) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(allContacts.get(i).getContactLatitude(), allContacts.get(i).getContactLongitude())).anchor(0.5f, 0.5f).title(allContacts.get(i).getContactName()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(allContacts.get(i).getContactLatitude(), allContacts.get(i).getContactLongitude()), 2));

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MapsActivity.currentMode == CONTACT_SELECTED_VIEW) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(MapsActivity.this);
           Bundle extras = getIntent().getExtras();

           if (extras != null) {
                double [] latlng = extras.getDoubleArray("locData");
                String name =extras.getString("contName");
                Toast.makeText(this,name,Toast.LENGTH_LONG).show();
                googleMap.clear();
                TextView locationTv = (TextView) findViewById(R.id.latlngLocation);
                locationTv.setText("selLatitude:" + latlng[0] + "selLongitude:" + latlng[1]);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latlng[0],latlng[1]),10));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latlng[0], latlng[1])).anchor(0.5f, 0.5f).title(name));

            }
            return;

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);


    }

    @Override
    public void onMapClick(LatLng latLng) {


        Toast.makeText(this,"Plz select Options from the Menu",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(currentMode !=  NEWCONTACT_MANUAL_SELECTED_LOCATION_VIEW){
            return;
        }
        googleMap.clear();
        staticViewLatitude = latLng.latitude;
        staticViewLongitude = latLng.longitude;
        String address = "marked";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                address = listAddresses.get(0).getAddressLine(0).toString();
                 Log.i("PlaceInfo", address+","+listAddresses.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        googleMap.addMarker(new MarkerOptions().position(latLng).title(address).draggable(true));
        TextView locationTv = (TextView) findViewById(R.id.latlngLocation);
        locationTv.setText("marked Latitude:" + latLng.latitude + "marked Longitude:" + latLng.longitude);
        Log.i("Place Info", (Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude)));

    }
}







