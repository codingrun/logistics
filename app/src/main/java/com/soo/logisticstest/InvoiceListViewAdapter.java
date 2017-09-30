package com.soo.logisticstest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 이수연 on 2017-09-27.
 */

public class InvoiceListViewAdapter extends BaseAdapter {
    ArrayList<String> invoiceList = new ArrayList<String>();

    public InvoiceListViewAdapter(ArrayList<String> mInvoiceList){
        invoiceList = mInvoiceList;

    }

    @Override
    public int getCount() {
        return invoiceList.size();
    }

    @Override
    public String getItem(int position) {
        return invoiceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.invoice_item, parent, false);
        }

        TextView invoiceText = (TextView) convertView.findViewById(R.id.INVOICE);
        invoiceText.setText(invoiceList.get(position));


        return convertView;
    }


}
