package com.inc.vr.corp.app.jualbeli;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Launch extends AppCompatActivity {
    int login = 0;
    ProgressDialog pDialog;
    final Handler handler = new Handler();
    SharedPreferences sharedpreferences;
    String no_id, no_rek, kode_login_update, no_token, db, kode_login, no_anggota;
    Boolean session=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        sharedpreferences = getSharedPreferences("kopsan", Context.MODE_PRIVATE);
        no_rek = sharedpreferences.getString("no_rek", "0");
        no_id = sharedpreferences.getString("no_id",null);

        session = sharedpreferences.getBoolean("session_status", false);
        pDialog = new ProgressDialog(Launch.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memvalidasi data, mungkin membutuhkan waktu yang sedikit lebih lama.");
        pDialog.show();

        if(session){
            Intent sudahLogin = new Intent(Launch.this, MainActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, message);
            Toast.makeText(Launch.this, "Selamat Datang Kembali",
                    Toast.LENGTH_LONG).show();
            pDialog.hide();
            finish();
            startActivity(sudahLogin);
        }else{
            Intent sudahLogin = new Intent(Launch.this, LoginActivity.class);
            sudahLogin.putExtra("CEK_LOGIN", "baru");
            finish();
            Toast.makeText(Launch.this, "Silahkan Login",
                    Toast.LENGTH_LONG).show();
            pDialog.hide();
            startActivity(sudahLogin);
        }
    }
    private void cekdata(){
        final String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/cek_login.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota+"&&no_token="+no_token;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(Launch.class.getSimpleName(), "Auth Response: " +urll+ response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("data").equals("no data")){
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                        //intent.putExtra(EXTRA_MESSAGE, message);
                        belumLogin.putExtra("CEK_LOGIN", "tidak");
                        finish();
                        Toast.makeText(Launch.this, "Harap Login Dahulu!",
                                Toast.LENGTH_LONG).show();
                        startActivity(belumLogin);
                    }else{
                        kode_login_update = jsonObject.getString("kode_login");
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        if(session){
                            if(kode_login.equals(kode_login_update)){
                                Intent sudahLogin = new Intent(Launch.this, MainActivity.class);
                                //intent.putExtra(EXTRA_MESSAGE, message);
                                finish();
                                Toast.makeText(Launch.this, "Selamat Datang Kembali",
                                        Toast.LENGTH_LONG).show();
                                startActivity(sudahLogin);
                            }else{
                                Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                                belumLogin.putExtra("CEK_LOGIN", "tidak");
                                Toast.makeText(Launch.this, "Harap Login Dahulu!",
                                        Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putBoolean("session_status", true);
                                editor.putString("no_anggota", "");
                                editor.putString("nama_lengkap", "");
                                editor.putString("kode_login", "");
                                finish();
                                startActivity(belumLogin);
                            }
                        }else{
                            Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                            //intent.putExtra(EXTRA_MESSAGE, message);
                            belumLogin.putExtra("CEK_LOGIN", "tidak");
                            Toast.makeText(Launch.this, "Harap Login Dahulu!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(belumLogin);
                        }
                    }

                }catch (JSONException e){e.printStackTrace(); }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
                Toast.makeText(Launch.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

}
