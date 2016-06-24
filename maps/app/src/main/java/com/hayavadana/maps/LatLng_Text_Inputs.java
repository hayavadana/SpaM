
package com.hayavadana.maps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LatLng_Text_Inputs extends AppCompatActivity {
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latlng_textview);
        mydb = new DBHelper(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tVLatLngToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().getTitle();

    }
     public void onbSaveClick(View v) {
         final EditText eText1= (EditText) findViewById(R.id.editText_Name);
         final EditText eText2=(EditText) findViewById(R.id.editText_Lattitude);
         final EditText eText3=(EditText) findViewById(R.id.editText_Longitude);
         Button button = (Button)findViewById(R.id.button_Save);
         button.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 mydb.insertContact(eText1.getText().toString(),Double.parseDouble(eText2.getText().toString()), Double.parseDouble(eText3.getText().toString()));
                 Toast.makeText(getApplicationContext(), "Contact inserted successfully", Toast.LENGTH_LONG).show();
                 EditText myTextView1 = (EditText) findViewById(R.id.editText_Name);
                 EditText myTextView2 = (EditText) findViewById(R.id.editText_Lattitude);
                 EditText myTextView3 = (EditText) findViewById(R.id.editText_Longitude);
                 myTextView1.setText("");
                 myTextView2.setText("");
                 myTextView3.setText("");

             }
         });


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
