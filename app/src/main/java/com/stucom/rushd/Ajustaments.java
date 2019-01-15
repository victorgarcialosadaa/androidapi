package com.stucom.rushd;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import android.widget.ImageView;




public class Ajustaments extends AppCompatActivity {
    SharedPreferences prefs;
    EditText nombreContenido;
    String imagenContenido;
    ImageView imagen;
    Bitmap bitmapImage;
    Uri uriImagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustaments);
        imagen = findViewById(R.id.ProfilePicture);
        prefs = getPreferences(MODE_PRIVATE);
        /**
         * Obtener el valor del BoxText
         */
        String nombre = prefs.getString("nombre","");
        String rutaImagen = prefs.getString("ProfilePicture","");

        /**
         * Buscamos los id de cada BoxText y le asignamos el contenido
         */
        nombreContenido = findViewById(R.id.boxName);
        nombreContenido.setText(nombre);
        imagenContenido = prefs.getString("Imagen", null);

        /**
         * Bitmap es la imagen en codigo binario
         */

        //save();
    }


    /**
     * Cuando vuelve atrás, al volver a Ajustes la información se mantiene como la ultima vez
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        prefs.edit().putString("nombre",nombreContenido.getText().toString()).commit();
    }

    /**
     * Cuando la imagen a sido clicada entonces va a ir al metodo reloadImage
     * @param view
     */
    public void cuandoClica(View view) {
        reloadImage();
    }

    /**
     * Una vez picada la foto hace un intent para obtener la imagen
     */
    private void reloadImage(){
        //Crearemos un intentt para obtener la URI de la memoria
        Intent intent= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);    //Que pasa cuando clica en el imagen
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecciona la app para poner foto de perfil"),10);
    }

    /**
     * El resultado nos mostrará la imagen que hemos elegido
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode==RESULT_OK){
            uriImagen = data.getData();
            imagen.setImageURI(uriImagen);
        }
    }



    /**
     * Este metodo save lo que hace es guarddar la imagen URI para no perder su contenido
     */
    public void save(){
        if (uriImagen != null){
            SharedPreferences pre = getSharedPreferences(getPackageName(),MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();
            editor.putString("ProfilePicture",uriImagen.toString());
        }
    }
}
