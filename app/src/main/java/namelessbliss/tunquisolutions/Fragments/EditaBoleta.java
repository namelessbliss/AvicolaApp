package namelessbliss.tunquisolutions.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import namelessbliss.tunquisolutions.DatosEmpresaManager.Empresa;
import namelessbliss.tunquisolutions.Modelo.Boleta;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.TemplatePDF;


public class EditaBoleta extends Fragment {

    // clase con data de empresa
    Empresa empresa;
    // hasmap de los datos de la empresa
    HashMap<String, String> datosEmpresa;
    // variables de datos de empresa
    String nombreEmp = "", direccion = "", telefono = "", logoEmp = "";

    // variables datos boleta
    String idBoleta, idUsuario, idCliente, nombreCliente, fecha;

    TextView nombreEmpresa, direccionEmpresa, telefonoEmpresa, fechaBoleta,
            txtnombreCliente;
    ImageView logoEmpresa;

    Button registrarPago, actualizarVenta;

    TableLayout tableLayout;
    TextView tituloSubtotal;
    TextView valorSubtotal;

    RequestQueue queue;
    private ArrayList<Boleta> listaDetalle;

    int id = 0;
    //tamaño maximo de los pesos que pueden ingresar
    float[] pesos = new float[100];
    // tamaño maximo de las cantidades que pueden ingresar
    int[] cantidades = new int[100];
    // array de totales, usada para conseguir el subtotal
    ArrayList<TextView> subtotal = new ArrayList<>();
    // estado de fila subtotal
    boolean filaSubGenerada = false;


    //cabecera de tabla de pdf
    String[] header = {"Poducto", "Cantidad", "Peso", "Precio", "Total"};
    // clase constructora de pdf
    TemplatePDF pdf;
    boolean ventaActualizada = false;

    private final int PERMISSION_ALL = 1;

    private String[] persimissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    float montoSubtotal = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.opcionesCliente).setVisible(false);
        menu.findItem(R.id.verBoleta).setVisible(true);
        super.onPrepareOptionsMenu(menu);

    }

    public EditaBoleta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edita_boleta, container, false);

        listaDetalle = new ArrayList<>();
        empresa = new Empresa(getContext());
        datosEmpresa = empresa.getDatosEmpresa();

        logoEmpresa = view.findViewById(R.id.imgViewLogo);
        nombreEmpresa = view.findViewById(R.id.nombreEmpresa);
        direccionEmpresa = view.findViewById(R.id.direccionEmpresa);
        telefonoEmpresa = view.findViewById(R.id.telefonoEmpresa);
        fechaBoleta = view.findViewById(R.id.fechaBoleta);
        txtnombreCliente = view.findViewById(R.id.nombreCliente);

        actualizarVenta = view.findViewById(R.id.actualizarVenta);
        registrarPago = view.findViewById(R.id.registrarPago);

        obtenerDatos();

        tableLayout = view.findViewById(R.id.tablaBoleta);
        tituloSubtotal = new TextView(getContext());
        valorSubtotal = new TextView(getContext());


        llenarDatosEmpresa(datosEmpresa);

        queue = Volley.newRequestQueue(getContext());

        obtenerDatosBoleta();

        actualizarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarVenta.setEnabled(false);
                actualizarVenta.setVisibility(View.GONE);
                actualizarVenta();
            }
        });

        registrarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPago();
            }
        });

        return view;
    }

    private void registrarPago() {
        RegistrarPago registrarPago = new RegistrarPago();
        Bundle bundle = new Bundle();
        bundle.putString("ID_CLIENTE", idCliente);
        bundle.putString("ID_USUARIO", String.valueOf(idUsuario));
        bundle.putString("NOMBRE_CLIENTE", nombreCliente);
        bundle.putString("MONTO", String.valueOf(montoSubtotal));
        registrarPago.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Contenedor, registrarPago).addToBackStack(null).commit();
    }

    private void obtenerDatosBoleta() {

        String server_url = "http://avicolas.skapir.com/obtener_boleta.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        final String result = response.toString();
                        Log.d("response", "result : " + result); //when response come i will log it
                        JSONArray jsonAry = null;
                        try {
                            jsonAry = new JSONArray(response);
                            for (int i = 0; i < jsonAry.length(); i++) {
                                JSONObject jSONObject = jsonAry.getJSONObject(i);

                                String prod = jSONObject.getString("NOMBRE_PRODUCTO");
                                int canti = jSONObject.getInt("CANTIDAD");
                                float peso = Float.parseFloat(String.valueOf(jSONObject.getDouble("PESO")));
                                float precio = Float.parseFloat(String.valueOf(jSONObject.getDouble("PRECIO")));
                                float total = peso * precio;

                                if (jSONObject.getString("FECHA").equalsIgnoreCase(fecha)) {
                                    listaDetalle.add(new Boleta(prod, canti, peso, precio, total));
                                }
                            }

                            for (Boleta bol : listaDetalle) {
                                llenarTabla(tableLayout, bol.getProducto(), bol.getCantidad(), bol.getPesoNeto(), bol.getPrecio());
                            }
                            llenarFilaSubtotal(tableLayout, tituloSubtotal, valorSubtotal);
                            Toast.makeText(getContext(), "Detalles obtenidos", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "No se pudo obtener los detalles", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("idCliente", idCliente);
                param.put("idBoleta", idBoleta);

                return param;
            }
        };
        queue.add(stringRequest);

    }

    private void actualizarVenta() {
        Gson gson = new Gson();
        final String newDataArray = gson.toJson(listaDetalle); // dataarray is list aaray

        String server_url = "http://avicolas.skapir.com/actualizar_venta.php"; // url of server check this 100 times it must be working

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String result = response;
                        Log.d("response", "result : " + result); //when response come i will log it
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String respuesta = jsonObject.getString("RESPUESTA");
                            if (respuesta.equalsIgnoreCase("COMPLETADO")) {
                                Toast.makeText(getActivity(), "¡Venta Actualizada!", Toast.LENGTH_LONG).show();
                                actualizarVenta.setEnabled(true);
                                actualizarVenta.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "¡Error al actualizar venta!", Toast.LENGTH_LONG).show();
                        registrarPago.setVisibility(View.GONE);
                        actualizarVenta.setVisibility(View.VISIBLE);
                        actualizarVenta.setEnabled(true);
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("arrayVenta", newDataArray);
                param.put("idUsuario", idUsuario);
                param.put("idCliente", idCliente);
                param.put("idBoleta", idBoleta);

                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void llenarTabla(final TableLayout tabLayout, String nomprod, int canti, float pesoKG, float precioUnitario) {
        //Instancia de nueva fila
        TableRow row = new TableRow(getContext());
        //Instancias de objetos de la ui
        TextView producto = new TextView(getContext());
        TextView cantidad = new TextView(getContext());
        TextView peso = new TextView(getContext());
        EditText precio = new EditText(getContext());
        final TextView total = new TextView(getContext());

        //atributos,Lo hace solo decimal
        precio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        precio.setId(id);
        precio.setHint("precio...");
        precio.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        //precio.setPadding(2, 1, 2, 1);
        precio.setMaxLines(1);
        //precio.setPadding(0, 4, 0, 4);
        precio.setGravity(Gravity.CENTER);
        precio.setText(String.valueOf(precioUnitario));

        // Establece la accion para cuando se escribe
        precio.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent keyEvent) {
                if (keyEvent != null) {
                    EditText editText = (EditText) v;
                    int index = editText.getId();
                    float pres;
                    float suma;
                    if (!editText.getText().toString().isEmpty()) {
                        pres = Float.parseFloat(editText.getText().toString());
                        suma = pesos[index] * pres;
                        // actualiza la lista para el pdf
                        listaDetalle.get(index).setPrecio(pres);
                        listaDetalle.get(index).setTotal(suma);
                    } else {
                        pres = 0;
                        suma = cantidades[index] * pesos[index] * pres;
                    }
                    total.setText(String.valueOf(suma));
                    validarListaSubTotal(subtotal);
                    validarListaPDF(listaDetalle);
                    llenarFilaSubtotal(tabLayout, tituloSubtotal, valorSubtotal);
                }
                return false;
            }
        });

        producto.setText(nomprod);
        producto.setPadding(2, 1, 2, 1);
        producto.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        producto.setMaxLines(1);
        producto.setGravity(Gravity.CENTER);
        cantidad.setText(String.valueOf(canti));
        cantidad.setPadding(2, 1, 2, 1);
        cantidad.setMaxLines(1);
        cantidad.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        cantidad.setGravity(Gravity.CENTER);
        peso.setMaxLines(1);
        peso.setPadding(2, 1, 2, 1);
        peso.setText(String.valueOf(pesoKG));
        peso.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        peso.setGravity(Gravity.CENTER);
        total.setMaxLines(1);
        total.setText(String.valueOf(precioUnitario * pesoKG));
        total.setId(id);
        total.setPadding(2, 1, 2, 1);
        total.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        total.setGravity(Gravity.CENTER);
        subtotal.add(total);
        row.addView(producto);
        row.addView(cantidad);
        row.addView(peso);
        row.addView(precio);
        row.addView(total);
        tabLayout.addView(row);
        pesos[id] = Float.parseFloat(peso.getText().toString());
        cantidades[id] = Integer.parseInt(cantidad.getText().toString());
        id++;
    }

    private void validarListaSubTotal(ArrayList<TextView> lista) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getText().toString().isEmpty()) {
                lista.get(i).setText("0");
            }
        }
    }

    private void validarListaPDF(ArrayList<Boleta> lista) {
        for (Boleta bol : lista) {
            if (bol.getPrecio() < 0) {
                bol.setPrecio(0);
            }
            if (bol.getTotal() < 0) {
                bol.setTotal(0);
            }
        }
    }

    private void llenarFilaSubtotal(TableLayout tableLayout, TextView titulo, TextView valor) {
        float subt = 0;
        //Instancia de nueva fila
        TableRow row = new TableRow(getContext());
        //Instancias de objetos de la ui

        titulo.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        titulo.setTextColor(getResources().getColor(R.color.colorAccent));
        titulo.setTypeface(null, Typeface.BOLD);
        valor.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoTabla));
        valor.setTextColor(getResources().getColor(R.color.design_default_color_primary));
        valor.setTypeface(null, Typeface.BOLD);
        titulo.setGravity(Gravity.CENTER);
        valor.setGravity(Gravity.CENTER);
        titulo.setPadding(5, 1, 5, 1);
        valor.setPadding(5, 1, 5, 1);
        titulo.setText("SubTotal: ");
        for (int i = 0; i < subtotal.size(); i++) {
            subt += Float.parseFloat(subtotal.get(i).getText().toString());
        }
        subt = (int) (subt * 100);
        montoSubtotal = subt / 100.0f;
        if (!filaSubGenerada) {
            //System.out.println("fila");
            valor.setText(String.valueOf(montoSubtotal));

            row.addView(new TextView(getContext()));
            row.addView(new TextView(getContext()));
            row.addView(new TextView(getContext()));
            row.addView(titulo);
            row.addView(valor);
            tableLayout.addView(row);
            filaSubGenerada = true;
        } else {
            System.out.println("no fila");
            valor.setText(String.valueOf(montoSubtotal));
        }
    }

    private void obtenerDatos() {
        Bundle bundle = getArguments();
        //Establece el nombre del cliente seleccionado
        if (bundle != null) {
            idBoleta = bundle.getString("ID_BOLETA");
            idUsuario = bundle.getString("ID_USUARIO");
            idCliente = bundle.getString("ID_CLIENTE");
            nombreCliente = bundle.getString("NOMBRE_CLIENTE");
            fecha = bundle.getString("FECHA");

            fechaBoleta.setText(fecha);
            txtnombreCliente.setText(nombreCliente);
        }
    }

    private void llenarDatosEmpresa(HashMap<String, String> datosEmpresa) {
        if (!datosEmpresa.isEmpty() && datosEmpresa != null) {
            nombreEmp = datosEmpresa.get(Empresa.KEY_NOMBRE);
            direccion = datosEmpresa.get(Empresa.KEY_DIRECCION);
            telefono = datosEmpresa.get(Empresa.KEY_CELULAR);
            logoEmp = datosEmpresa.get(Empresa.KEY_LOGO);
            if (logoEmp == null)
                logoEmp = "";

            nombreEmpresa.setText(nombreEmp);
            direccionEmpresa.setText(direccion);
            telefonoEmpresa.setText(telefono);
            txtnombreCliente.setText(nombreCliente);
            fechaBoleta.setText(fecha);

            if (datosEmpresa != null && !logoEmp.isEmpty()) {
                // si esta establecido o es diferente vacio
                // obtiene la imagen de la ruta guardada
                String ubicacion = logoEmp;
                File imgFile = new File(ubicacion);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    logoEmpresa.setImageBitmap(myBitmap);
                }
            } else {
                logoEmpresa.setImageResource(R.drawable.tunqui_logo);
            }
        }
    }

    /**
     * Llena los datos basicos del pdf con datos por default
     *
     * @param nombre
     */
    private void llenarPDF(String nombre) {

        pdf = new TemplatePDF(getContext());
        pdf.createFile(fecha);
        pdf.openDocument(nombre);
        pdf.addMetaData("TunquiSolutions", "Boleta", "TunquiSolutions");
        File imgLogo = new File(logoEmp);
        // load image
        if (!logoEmp.isEmpty() && imgLogo.exists()) {
            try {
                Image image = Image.getInstance(imgLogo.getAbsolutePath());
                pdf.addImgName(image);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Drawable d = getContext().getResources().getDrawable(R.drawable.tunqui_logo);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
            try {
                Image image = Image.getInstance(stream.toByteArray());
                pdf.addImgName(image);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pdf.addTitles(nombreEmp, direccion, telefono, nombreCliente, fecha);
        pdf.createTable(header, getProductos(listaDetalle));
        pdf.addSlogan();
        pdf.closeDocument();
    }

    private ArrayList<String[]> getProductos(ArrayList<Boleta> lista) {
        ArrayList<String[]> rows = new ArrayList<>();
        for (Boleta bol : lista) {
            rows.add(new String[]{bol.getProducto(), String.valueOf(bol.getCantidad()),
                    String.valueOf(bol.getPesoNeto()), String.valueOf(bol.getPrecio()),
                    String.valueOf(bol.getTotal())});
        }
        return rows;
    }

    private void verPDF() {
        pdf.viewPDF();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.verBoleta) {
            ocultarTecladoDe(getView());
            validarPermisos();
        }
        return super.onOptionsItemSelected(item);
    }

    public void validarPermisos() {
        // comprueba version de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // versiones nuevas
            if (!hasPermission(getContext(), persimissions)) { // si no tiene permisos los pide
                //ActivityCompat.requestPermissions(getActivity(), persimissions, PERMISSION_ALL);
                Toast.makeText(getContext(), "Se necesita que acepte los siguientes permisos para generar el pdf", Toast.LENGTH_LONG).show();
                requestPermissions(persimissions, PERMISSION_ALL);
            } else {
                //Toast.makeText(getContext(), "Los PDF se guardan en la carpeta TunquiSolutionsPDF", Toast.LENGTH_SHORT).show();
                llenarPDF(fecha);
                verPDF();
            }

        } else {
            versionesAnteriores();
        }
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
            llenarPDF(fecha);
            verPDF();
            Toast.makeText(getContext(), "Los PDF se guardan en la carpeta TunquiSolutionsPDF", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean CheckPermission(String permission) {
        int result = getContext().checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void ocultarTecladoDe(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
