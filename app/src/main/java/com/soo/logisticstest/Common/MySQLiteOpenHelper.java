package com.soo.logisticstest.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 이수연 on 2017-09-27.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String dbName = "Logistics.db";
    private static final int dbVersion = 1;

    public MySQLiteOpenHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table INVOICETABLE(ID integer primary key autoincrement, COMPANY integer, INVOICE text);";
        db.execSQL(sql);
    }

    //버전바뀌었을때 기존 데이터 베이스를 어떻게 변경할 것인지 넣어주는 메소드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getInvoiceList(){
        ArrayList<String> invoiceList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("INVOICETABLE", null, null, null, null, null, null);
        try {
            int cnt = 0;
            while (c.moveToNext()) {
                String invoice = c.getString(c.getColumnIndex("INVOICE"));

                invoiceList.add(invoice);

            }
        }catch (Exception e){
            Log.e("getInvoiceList", e.toString());
        }
        return invoiceList;
    }

    public boolean setInvoice(String invoice){
        boolean result = true;
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();
            values.put("INVOICE", invoice);
            values.put("COMPANY", 1);       //우체국만 있으므로 임의로 넣어줌

            db.insert("INVOICETABLE", null, values);
        }catch (Exception e){
            result = false;
        }

        return result;
    }

    public int CheckInvoice(String invoice){
        int length = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from INVOICETABLE WHERE INVOICE = '"+invoice+"'";
        Cursor c;
        try {
            c = db.rawQuery(sql, null);
            length = c.getCount();
            //db.execSQL(sql)
        } catch (SQLException e) {
            e.printStackTrace();
            length = -1;
        }
        return length;

    }

}
