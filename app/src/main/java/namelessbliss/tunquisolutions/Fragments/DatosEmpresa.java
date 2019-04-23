package namelessbliss.tunquisolutions.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import namelessbliss.tunquisolutions.DatosEmpresaManager.Empresa;
import namelessbliss.tunquisolutions.R;

import static android.app.Activity.RESULT_OK;

public class DatosEmpresa extends Fragment {

    private static final int IMAGE_PICK_CODE = 100;
    private final int PERMISSION_ALL = 1;

    private String[] persimissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // clase con data de empresa
    Empresa empresa;
    // hasmap de los datos de la empresa
    HashMap<String, String> datosEmpresa;
    // variables de datos de empresa
    String nombre, direccion, celular, logo;
    // campos de texto
    EditText etNombre, etDirecciion, etCelular;
    ImageView imgLogo;

    Button guardarCambios;
    ImageButton cambiarLogo;

    public DatosEmpresa() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.opcionesCliente).setVisible(false);
        menu.findItem(R.id.verBoleta).setVisible(false);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_datos_empresa, container, false);

        etNombre = view.findViewById(R.id.nombreEmpresa);
        etDirecciion = view.findViewById(R.id.direccionEmpresa);
        etCelular = view.findViewById(R.id.celularEmpresa);
        imgLogo = view.findViewById(R.id.logoEmpresa);
        guardarCambios = view.findViewById(R.id.guardarCambiosEmpresa);
        cambiarLogo = view.findViewById(R.id.cambiarLogo);

        empresa = new Empresa(getContext());
        datosEmpresa = empresa.getDatosEmpresa();

        llenarDatos(datosEmpresa);

        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre = etNombre.getText().toString();
                direccion = etDirecciion.getText().toString();
                celular = etCelular.getText().toString();
                empresa.eliminarDatosEmpresa();
                empresa.crearDatosEmpresa(nombre, direccion, celular, logo);
                Toast.makeText(getContext(), "Datos de empresa alamacenados", Toast.LENGTH_SHORT).show();
            }
        });

        cambiarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // comprueba version de android
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // versiones nuevas
                    if (!hasPermission(getContext(), persimissions)) { // si no tiene permisos los pide
                        requestPermissions(persimissions, PERMISSION_ALL);
                    } else { // si tiene permisos
                        seleccionarImagen();
                    }

                } else {
                    versionesAnteriores();
                }

            }
        });

        return view;
    }

    private void llenarDatos(HashMap<String, String> datosEmpresa) {
        if (!datosEmpresa.isEmpty() && datosEmpresa != null) {
            nombre = datosEmpresa.get(Empresa.KEY_NOMBRE);
            direccion = datosEmpresa.get(Empresa.KEY_DIRECCION);
            celular = datosEmpresa.get(Empresa.KEY_CELULAR);
            logo = datosEmpresa.get(Empresa.KEY_LOGO);
            if (logo == null)
                logo = "";

            etNombre.setText(nombre);
            etDirecciion.setText(direccion);
            etCelular.setText(celular);

            if (datosEmpresa != null && !logo.isEmpty()) {
                // si esta establecido o es diferente vacio
                // obtiene la imagen de la ruta guardada
                String ubicacion = logo;
                File imgFile = new File(ubicacion);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgLogo.setImageBitmap(myBitmap);
                }
            } else {
                imgLogo.setImageResource(R.drawable.tunqui_logo);
            }
        }
    }

    private void seleccionarImagen() {
        // intent para seleccionar una imagen
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri selectedImageUri = data.getData();
            String picturePath = getPath(getActivity().getApplicationContext(), selectedImageUri);
            logo = picturePath;
            imgLogo.setImageURI(data.getData());
            System.out.println("logo ------- " + picturePath);
        }
    }

    /**
     * Obtiene la direccion del archivo
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public boolean hasPermission(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //Valida el permiso de escritura y lectura externa
    private void versionesAnteriores() {
        if (CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                && CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            seleccionarImagen();
        }
    }

    private boolean CheckPermission(String permission) {
        int result = getContext().checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }


}
