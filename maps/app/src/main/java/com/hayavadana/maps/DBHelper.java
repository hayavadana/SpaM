package com.hayavadana.maps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "MyDBName.db";
        public static final String CONTACTS_TABLE_NAME = "contacts";
        public static final String CONTACTS_COLUMN_ID = "id";

        public static final String CONTACTS_COLUMN_NAME = "name";
        public static final String CONTACTS_COLUMN_Latitude = "latitude";
        public static final String CONTACTS_COLUMN_Longitude = "longitude";
        ArrayList<Integer> array_list_ids = new ArrayList<Integer>();


        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME , null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("create table contacts " +
                            "(id integer primary key, name text,latitude double,longitude double)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }

        public boolean insertContact  (String name,Double lat,Double lng)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("latitude", lat);
            contentValues.put("longitude", lng);
            db.insert("contacts", null, contentValues);
            return true;
        }

        public Cursor getData(int id){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
            return res;
        }

        public int numberOfRows(){
            SQLiteDatabase db = this.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
            return numRows;
        }

        public boolean updateContact (Integer id, String name, Double lat,Double lng)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("latitude", lat);
            contentValues.put("longitude", lng);
            db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
            return true;
        }

        public Integer deleteContact (Integer id, Context con)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            Toast.makeText(con,"id from db is "+id,Toast.LENGTH_LONG).show();

            return db.delete("contacts","id = ? ", new String[] { Integer.toString(id) });

        }

        public ArrayList getAllContacts()
        {
            ArrayList<ContactInfo> allContacts = new ArrayList<ContactInfo>();


            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from contacts order by name asc", null );
            res.moveToFirst();

            while(!res.isAfterLast()){
                ContactInfo aContact = new ContactInfo();
                aContact.setId(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)));
                aContact.setContactName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
                aContact.setContactLatitude(res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_Latitude)));
                aContact.setContactLongitude(res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_Longitude)));
                allContacts.add(aContact);
                res.moveToNext();
            }
            return allContacts;
        }

}

