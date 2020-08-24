package com.inc.vr.corp.app.jualbeli;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {
    Button btn_login;
    TextInputEditText edit_email, edit_password;
    TextView appversion;
    ProgressDialog pDialog;
    private String url = "login.php";
    int success;
    ConnectivityManager conMgr;
    private static final String TAG = LoginActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;
    Boolean session = false, login=false;
    String string_email, string_id, no_token,db, nama, noid, no_rek, pedagang_code;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        edit_email = findViewById(R.id.email_input);
        edit_password = findViewById(R.id.password_input);
        //appversion = findViewById(R.id.appversion);

        Bundle cek_data = getIntent().getExtras();
        String version = "1.0";
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memeriksa data");
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appversion.setText("App Version " + version);

        //---------- CEK KONEKSI ------------
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        //-------------------------------------
        //-------Cek session login jika TRUE
        // maka langsung buka MainActivity --------------------
        sharedpreferences = getSharedPreferences("kopsan", Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean("session_status", false);
        nama = sharedpreferences.getString("nama", null);
        noid = sharedpreferences.getString("noid", null);
        no_rek= sharedpreferences.getString("no_rek", null);
        if (session) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("noid", string_id);
            intent.putExtra("nama", string_email);
            finish();
            startActivity(intent);
        }
        // ------------------------------
        // ------ BUTTON MASUK KLIK ------------
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ----------- CEK YANG MASIH KOSONG ------
                if (edit_email.getText().toString().trim().length() > 0 && edit_password.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        cekLogin fillList = new cekLogin();
                        fillList.execute("");
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    // ------ FUNCTION CEK LOGIN ---------------
    public class cekLogin extends AsyncTask<String, String, String> {
        String z = "";
        @Override
        protected void onPreExecute() {
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            pDialog.hide();
            if(login){
                SharedPreferences.Editor editor = getSharedPreferences("kopsan", MODE_PRIVATE).edit();
                editor.putString("nama", nama);
                editor.putString("noid", noid);
                editor.putString("no_rek", no_rek);
                editor.putString("pedagang_code", pedagang_code);
                editor.putBoolean("session_status", true);
                editor.apply();
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }else{
                Toast.makeText(getApplicationContext(),"Silahkan Coba Lagi",Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(ScanResultActivity.this, z, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            return z;
        }
    }
    /// -----------------
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
