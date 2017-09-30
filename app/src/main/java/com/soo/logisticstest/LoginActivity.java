package com.soo.logisticstest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.soo.logisticstest.Common.GoggleMapAPI;
import com.soo.logisticstest.Common.MySQLiteOpenHelper;
import com.soo.logisticstest.Common.PostOfficeAPI;
import com.soo.logisticstest.Model.TrackingModel;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static android.os.Build.VERSION_CODES.M;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    // UI references.
    private AutoCompleteTextView delvNoText;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Spinner CompanySpinner;

    //송장번호 리스트
    private ArrayList<String> LIST_MENU = new ArrayList<String>();
    ListView listview;

    MySQLiteOpenHelper mySql;
    InvoiceListViewAdapter InvoiceAdapter;

    //    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);//alert창
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //택배회사 선택
        CompanySpinner = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CompanySpinner.setAdapter(adapter);

        CompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    Toast.makeText(getBaseContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                    CompanySpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mySql = new MySQLiteOpenHelper(getBaseContext());
        createInvoiceList();


        // Set up the login form.
        delvNoText = (AutoCompleteTextView) findViewById(R.id.delvNo);
        populateAutoComplete();

        //검색 버튼 눌렀을 경우
        Button SearchInButton = (Button) findViewById(R.id.search_in_button);
        SearchInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();

                //cj대한통운
                //http://nplus.doortodoor.co.kr/web/detail.jsp?slipno=운송장번호
                //http://nplus.doortodoor.co.kr/web/info.jsp?slipno=699195818822

                final String inputInvoice = delvNoText.getText().toString();

                if(inputInvoice.length() == 0){
                    Toast.makeText(getBaseContext(), "송장번호를 입력해 주세요", Toast.LENGTH_LONG).show();
                    return;
                }

                showProgress(true);

                //우체국
                PostOfficeAPI postOffice = new PostOfficeAPI(inputInvoice, getBaseContext(), new PostOfficeAPI.AsyncResponse() {
                    @Override
                    public void processFinish(boolean check, ArrayList<TrackingModel> TrackingList) {

                        if(check){
                            final ArrayList<TrackingModel> TrackList = TrackingList;
                            String addrss = TrackList.get(TrackList.size()-1).getLOCATE();

                            //경도 위도 가져와 지도 화면 뿌려주기
                            GoggleMapAPI api = (GoggleMapAPI) new GoggleMapAPI(addrss, getBaseContext(), new GoggleMapAPI.AsyncResponse() {

                                @Override
                                public void processFinish(Double lat, Double lng) {

                                    //db저장
                                    if(mySql.CheckInvoice(inputInvoice) == 0){//검색한 송장이 저장도어 있는지 체크
                                        if(!mySql.setInvoice(inputInvoice)){
                                            Toast.makeText(getBaseContext(), "송장번호 저장하는데 실패하였습니다.", Toast.LENGTH_LONG).show();
                                        }else{
                                            LIST_MENU.add(inputInvoice);
                                        }
                                    }

                                    showProgress(false);
                                    //위도, 경도를 보내준다.
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra("lat", lat);
                                    intent.putExtra("lng", lng);
                                    intent.putExtra("invoiceNo", inputInvoice);
                                    intent.putParcelableArrayListExtra("List", TrackList);

                                    startActivity(intent);



                                }
                            }).execute((Void) null);


                        }else{
                            showProgress(false);
                            Toast.makeText(getBaseContext(), "해당 송장에 대한 정보가 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                postOffice.execute((Void) null);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    public void createInvoiceList(){

        LIST_MENU = mySql.getInvoiceList();
        InvoiceAdapter = new InvoiceListViewAdapter(LIST_MENU);

        listview = (ListView) findViewById(R.id.invoiceList) ;
        listview.setAdapter(InvoiceAdapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String strText = (String) parent.getItemAtPosition(position);
                showProgress(true);
                //우체국
                PostOfficeAPI postOffice = new PostOfficeAPI(strText, getBaseContext(), new PostOfficeAPI.AsyncResponse() {
                    @Override
                    public void processFinish(boolean check, ArrayList<TrackingModel> TrackingList) {
                        final ArrayList<TrackingModel> TrackList = TrackingList;

                        String locationName = TrackList.get(TrackList.size()-1).getLOCATE();
                        if(check){
                            //경도 위도 가져와 지도 화면 뿌려주기
                            GoggleMapAPI api = (GoggleMapAPI) new GoggleMapAPI(locationName, getBaseContext(), new GoggleMapAPI.AsyncResponse() {
                                @Override
                                public void processFinish(Double lat, Double lng) {
                                    showProgress(false);
                                    //위도, 경도를 보내준다.
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra("lat", lat);
                                    intent.putExtra("lng", lng);
                                    intent.putExtra("invoiceNo", strText);
                                    intent.putParcelableArrayListExtra("List", TrackList);

                                    startActivity(intent);

                                }
                            }).execute((Void) null);

                        }else{
                            showProgress(false);
                            Toast.makeText(getBaseContext(), "해당 송장에 대한 정보가 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                postOffice.execute((Void) null);


                Log.v("listView", "내용 = "+strText);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();


        createInvoiceList();
        //InvoiceAdapter.notifyDataSetChanged();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(delvNoText, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }




    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

       // addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


}

