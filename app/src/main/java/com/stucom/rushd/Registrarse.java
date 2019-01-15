package com.stucom.rushd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {
    SharedPreferences prefs;
    EditText emailField;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.activity_registrarse);

        String email = prefs.getString("email", "");
        Button btn_registerEmail = findViewById(R.id.btn_registerEmail);
        textResult = findViewById(R.id.textResult);

        emailField = findViewById(R.id.emailField);
        emailField.setText(email);


        btn_registerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister(v);
                //setContentView(R.layout.activity_registrar_code);

                //Comprobación TOKEN

            }

            public void onRegister(View v) {
                String URL = "https://api.flx.cat/dam2game/register";
                StringRequest request = new StringRequest
                        (Request.Method.POST, URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        //getIntent().putExtra("email", emailField.getText().toString());
                                        textResult.setText("Prueba  " + response);
                                        Context context = getApplicationContext();
                                        CharSequence text = "Verifica tu email " + response;
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                        Intent intent = new Intent(Registrarse.this, RegistrarCode.class);
                                        intent.putExtra("email", emailField.getText().toString());
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String message = error.toString();
                                NetworkResponse response = error.networkResponse;
                                if (response != null) {
                                    Context context = getApplicationContext();
                                    CharSequence text = response.statusCode + " " + message;
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                                textResult.setText("ERROR " + message);

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", emailField.getText().toString());
                        return params;
                    }
                };
                MyVolley.getInstance(Registrarse.this).add(request);
            }
        });
    }
}
