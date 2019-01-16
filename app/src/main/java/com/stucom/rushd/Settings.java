package com.stucom.rushd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.rushd.api.APIResponse;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Settings extends Activity {

    static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    EditText nomText;

    SharedPreferences prefs;
    ImageView fotoUser;

    String mCurrentPhotoPath;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nomText = findViewById(R.id.editTextNombre);

        fotoUser = findViewById(R.id.fotoUser);


        fotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                builder.setItems(R.array.options_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                startActivityForResult(gallery, PICK_IMAGE);
                                break;
                            case 1:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {

                                    }
                                    if (photoFile != null) {
                                        imageUri = FileProvider.getUriForFile(Settings.this, "com.stucom.rushd.fileprovider", photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
        }
        if (resultCode == RESULT_OK) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);

            SharedPreferences.Editor ed = preferences.edit();

            ed.putString("UserFoto", imageUri.toString());

            ed.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        nomText.setText(preferences.getString("nom", ""));
        fotoUser.setImageURI(Uri.parse(preferences.getString("UserFoto", "")));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor ed = preferences.edit();

        ed.putString("nom", nomText.getText().toString());
        updateApiValues(nomText.getText().toString(),
                (preferences.getString("UserFoto", "")));

        ed.apply();
    }

    protected void updateApiValues(String nombre, String image) {
        String URL = "https://api.flx.cat/dam2game/user";
        StringRequest request = new StringRequest
                (Request.Method.PUT, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String json = response;
                                Gson gson = new Gson();
                                Type typeToken = new TypeToken<APIResponse<String>>() {
                                }.getType();
                                APIResponse<String> apiResponse = gson.fromJson(json, typeToken);
                                String userToken = apiResponse.getData();
                                //Guardar el token el el SharedPreferences
                                prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);

                                Context context = getApplicationContext();
                                CharSequence text = "Datos Cambiados";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                //Si el token no esta vacio nos va a llevar al menu principal
                                //Sino se va a quedar en la activity actual para pedir el registro
                                if (!userToken.isEmpty()) {
                                    Intent intent = new Intent(Settings.this, FirstView.class);
                                    //startActivity(intent);
                                    Toast.makeText(Settings.this, userToken, Toast.LENGTH_LONG).show();
                                    final SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("token", userToken);
                                    editor.apply();
                                } else {
                                    Toast.makeText(Settings.this, "Token:" + userToken, Toast.LENGTH_LONG).show();
                                }

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
                    }
                }) {

            @Override
            protected Map<String, String> getParams () {

                Map<String, String> params = new HashMap<>();
                params.put("token", prefs.getString("token", ""));
                params.put("name", nomText.toString());
                params.put("image", imageToBase64(fotoUser.toString()));
                return params;
            }
        };
        MyVolley.getInstance(Settings.this).add(request);
    }

    protected void getUser() {
        String URL = "https://api.flx.cat/dam2game/user";
        StringRequest request = new StringRequest
                (Request.Method.PUT, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String json = response;
                                Gson gson = new Gson();
                                Type typeToken = new TypeToken<APIResponse<String>>() {
                                }.getType();
                                APIResponse<String> apiResponse = gson.fromJson(json, typeToken);
                                String userToken = apiResponse.getData();
                                //Guardar el token el el SharedPreferences
                                prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);


                                Context context = getApplicationContext();
                                CharSequence text = "Datos Cambiados";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                //Si el token no esta vacio nos va a llevar al menu principal
                                //Sino se va a quedar en la activity actual para pedir el registro
                                if (!userToken.isEmpty()) {
                                    Intent intent = new Intent(Settings.this, FirstView.class);
                                    //startActivity(intent);
                                    Toast.makeText(Settings.this, userToken, Toast.LENGTH_LONG).show();
                                    final SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("token", userToken);
                                    editor.apply();
                                } else {
                                    Toast.makeText(Settings.this, "Token:" + userToken, Toast.LENGTH_LONG).show();
                                }

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
                    }
                }) {

            @Override
            protected Map<String, String> getParams () {

                Map<String, String> params = new HashMap<>();
                params.put("token", prefs.getString("token", ""));
                return params;
            }
        };
        MyVolley.getInstance(Settings.this).add(request);
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String imageToBase64(String image) {
        byte[] data;
        String base64 = null;
        try {
            data = image.getBytes("UTf-8");
            base64 = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return base64;
    }
}
