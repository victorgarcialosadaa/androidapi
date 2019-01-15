package com.stucom.rushd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings extends Activity {

    static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    EditText nomText;


    ImageView fotoUser;

    String mCurrentPhotoPath;

    Uri imageUri;


    /**
     * Busca tots els elements per id i el guarda a les variables, també crea un picker perque al clicar a la fotografia apareixi un desplegable
     * que permeti a l'usuari escollir la fotografia de la galeria o directament prendre la fotografia
     * @param savedInstanceState
     */
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
                        switch(which){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
        }
        if(resultCode == RESULT_OK){
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);

            SharedPreferences.Editor ed = preferences.edit();

            ed.putString("UserFoto",imageUri.toString());

            ed.apply();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        nomText.setText(preferences.getString("nom",""));
        fotoUser.setImageURI(Uri.parse(preferences.getString("UserFoto", "")));
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor ed = preferences.edit();

        ed.putString("nom",nomText.getText().toString());
        updateApiValues(nomText.getText().toString(),
                (preferences.getString("UserFoto", "")));

        ed.apply();
    }

protected void updateApiValues(String nombre,String image) {


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
}
