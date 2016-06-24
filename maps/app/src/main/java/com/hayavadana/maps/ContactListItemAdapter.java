package com.hayavadana.maps;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactListItemAdapter extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<ContactInfo> contactInfoList;

    public ContactListItemAdapter(Activity context, int res, ArrayList<ContactInfo> contactInfo) {
        super(context, res, contactInfo);
        this.context = context;
        this.contactInfoList = contactInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.contact_list_item, null, true);
        TextView tv = (TextView )rowView.findViewById(R.id.contName);
        String tempstr = contactInfoList.get(position).getContactName();
        tv.setText(tempstr);
        return rowView;
    }



}

