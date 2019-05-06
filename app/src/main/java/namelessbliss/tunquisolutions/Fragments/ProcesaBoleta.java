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
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import namelessbliss.tunquisolutions.DatosEmpresaManager.Empresa;
import namelessbliss.tunquisolutions.DatosMenuManager.DatosMenu;
import namelessbliss.tunquisolutions.Modelo.Boleta;
import namelessbliss.tunquisolutions.Modelo.DatosMenuCliente;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;
import namelessbliss.tunquisolutions.TemplatePDF;

/**
 * Vista de boleta, permite registrar la compra
 */
public class ProcesaBoleta extends Fragment {
    RequestQueue queue;

    //Listas de pesos pasados del fragment detalle cliente
    List<EditText> listaPesosGDobles, listaPesosPollos, listaPesosGNegras, listaPesosGRojas,
            listaPesosPatos, listaPesosPavos, listaPesosPechoE, listaPesosPiernaE,
            listaPesosEspinazo, listaPesosMenudencia, listaPesosAla, listaPesosOtros;
    //tamaño maximo de los pesos que pueden ingresar
    float[] pesos = new float[100];
    // tamaño maximo de las cantidades que pueden ingresar
    int[] cantidades = new int[100];
    // peso por default para la tabla
    float precioPollo = 0, precioGdoble = 0, precioGnegra = 0, precioGroja = 0, precioPato = 0,
            precioPavo = 0, precioPechoE = 0, precioPiernaE = 0, precioEspinazo = 0, precioMenudencia = 0,
            precioAla = 0, precioOtros = 0;
    // cantidad por default para la tabla
    int cantiPollo = 0, cantiGdoble = 0, cantiGnegra = 0, cantidGroja = 0, cantiPato = 0, cantiPavo = 0,
            cantiPechoE = 0, cantiPiernaE = 0, cantiEspinazo = 0, cantiMenudencia = 0, cantiAla = 0,
            cantiOtros = 0;
    // variable identificador de campos de precios
    int id = 0;
    // estado de fila subtotal
    boolean filaSubGenerada = false;
    // estado de la venta
    boolean ventaRegistrada = false;
    // valor de pago
    String pago;


    // array de totales, usada para conseguir el subtotal
    ArrayList<TextView> subtotal = new ArrayList<>();
    // clase constructora de pdf
    TemplatePDF pdf;
    // lista de datos para generar el pdf
    ArrayList<Boleta> listaDatosPDF = new ArrayList<>();

    String fecha = "";
    TableLayout tableLayout;
    TextView tituloSubtotal;
    TextView valorSubtotal;
    TextView nombreEmpresa, direccionEmpresa, telefonoEmpresa, fechaBoleta, nombreClienteEmpresa;
    ImageView logoEmpresa;
    Button btnRegistrarVenta, btnRegistrarPago;

    float montoSubtotal = 0;

    LinearLayout linearPago;
    //cabecera de tabla de pdf
    String[] header = {"Poducto", "Cantidad", "Peso", "Precio", "Total"};

    // valores cliente
    String idCliente, nombreCliente;
    int idUsuario;
    ArrayList<Integer> listaIdProductos;
    // get user data from session
    HashMap<String, String> user;
    // User Session Manager Class
    UserSessionManager session;

    private final int PERMISSION_ALL = 1;

    private String[] persimissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // clase con data de empresa
    Empresa empresa;
    // hasmap de los datos de la empresa
    HashMap<String, String> datosEmpresa;
    // variables de datos de empresa
    String nombreEmp, direccion, telefono, logoEmp;

    Bundle bundle;

    DatosMenu datosMenu;

    ArrayList<DatosMenuCliente> datosMenuClientes;

    DatosMenuCliente cliente;

    public ProcesaBoleta() {
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
        menu.findItem(R.id.verBoleta).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_procesa_boleta, container, false);

        datosMenu = new DatosMenu(getContext());
        datosMenuClientes = new ArrayList<>();

        obtenerDatos();

        //fecha
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int month = mMonth + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        fecha = mDay + "-" + month + "-" + mYear + " " + hour + ":" + minute + ":" + second;

        empresa = new Empresa(getContext());
        datosEmpresa = empresa.getDatosEmpresa();

        logoEmpresa = view.findViewById(R.id.imgViewLogo);
        nombreEmpresa = view.findViewById(R.id.nombreEmpresa);
        direccionEmpresa = view.findViewById(R.id.direccionEmpresa);
        telefonoEmpresa = view.findViewById(R.id.telefonoEmpresa);
        fechaBoleta = view.findViewById(R.id.fechaBoleta);
        nombreClienteEmpresa = view.findViewById(R.id.nombreCliente);

        llenarDatosEmpresa(datosEmpresa);

        tableLayout = view.findViewById(R.id.tablaBoleta);
        tituloSubtotal = new TextView(getContext());
        valorSubtotal = new TextView(getContext());

        btnRegistrarVenta = view.findViewById(R.id.registrarVenta);
        btnRegistrarPago = view.findViewById(R.id.registrarPago);

        // Session class instance
        session = new UserSessionManager(getContext());
        // get user data from session
        user = session.getUserDetails();
        // get id
        idUsuario = Integer.parseInt(user.get(UserSessionManager.KEY_ID));

        btnRegistrarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegistrarVenta.setVisibility(View.GONE);
                btnRegistrarVenta.setEnabled(false);
                procesarVenta();

            }
        });

        /*btnRegistrarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegistrarPago.setEnabled(false);
                registrarPago();
            }
        });*/
        /*sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTecladoDe(view);
                validarListaSubTotal(subtotal);
                validarListaPDF(listaDatosPDF);
                llenarFilaSubtotal(tableLayout, tituloSubtotal, valorSubtotal);
                procesarPago.setEnabled(true);
            }
        });*/

        /*procesarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (procesarPago.isEnabled()) {
                    linearPago.setVisibility(View.VISIBLE);

                } else {
                    System.out.println("PAGO NO HABILITADO");
                    Toast.makeText(getContext(), "Calcule el subtotal primero", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        /*btnRegistrarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pago = campoPago.getText().toString();
                if (pago != null && !pago.isEmpty()) {
                    if (pagoRegistrado == false) {
                        ocultarTecladoDe(view);
                        procesarVenta();
                        pagoRegistrado = true;
                    } else {
                        Toast.makeText(getContext(), "Acción repetida no aceptada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Llene el campo de pago para procesar la compra", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        /*generarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });*/

        recorrerDatos();

        llenarFilaSubtotal(tableLayout, tituloSubtotal, valorSubtotal);

        queue = Volley.newRequestQueue(getContext());
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
        fragmentManager.beginTransaction().replace(R.id.Contenedor, registrarPago).addToBackStack("Inicio").commit();
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
            nombreClienteEmpresa.setText(nombreCliente);
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

    private void obtenerDatos() {
        bundle = getArguments();
        //Obtiene el nombre del cliente seleccionado
        if (bundle != null) {
            idCliente = bundle.getString("ID_CLIENTE");
            nombreCliente = bundle.getString("NOMBRE_CLIENTE");
            listaIdProductos = bundle.getIntegerArrayList("LISTA_ID_PRODUCTO");
        }
        datosMenuClientes = datosMenu.getDatosMenu();
        if (datosMenuClientes != null && !datosMenuClientes.isEmpty()) {
            for (DatosMenuCliente menu : datosMenuClientes) {
                if (menu.getNombre().equalsIgnoreCase(nombreCliente))
                    cliente = menu;
            }
        }
    }

    public static void ocultarTecladoDe(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Envia datos al webservice
     */
    private void procesarVenta() {
        Gson gson = new Gson();
        final String newDataArray = gson.toJson(listaDatosPDF); // dataarray is list aaray

        // now we have json string lets send it to server using volly

        String server_url = "http://avicolas.skapir.com/procesar_venta.php"; // url of server check this 100 times it must be working

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
                                Toast.makeText(getActivity(), "¡Venta Registrada!", Toast.LENGTH_LONG).show();
                                //btnRegistrarPago.setVisibility(View.VISIBLE);
                                ventaRegistrada = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "¡Error al registrar venta!", Toast.LENGTH_LONG).show();
                        //btnRegistrarPago.setVisibility(View.GONE);
                        btnRegistrarVenta.setVisibility(View.VISIBLE);
                        btnRegistrarVenta.setEnabled(true);
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                System.out.println(newDataArray.toString());
                param.put("arrayVenta", newDataArray); // array is key which we will use on server side

                return param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
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

    private void recorrerDatos() {
        if (listaPesosPollos != null && !listaPesosPollos.isEmpty()) {
            float peso = 0;
            float merma = cantiPollo * (float) (cliente.getMermaPollo() / 1000);
            //cantidad = listaPesosPollos.size();
            for (int i = 0; i < listaPesosPollos.size(); i++) {
                peso += Float.parseFloat(listaPesosPollos.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Pollo", cantiPollo, peso + merma, precioPollo);
        }
        if (listaPesosGDobles != null && !listaPesosGDobles.isEmpty()) {
            float peso = 0;
            float merma = cantiGdoble * (float) (cliente.getMermaGdoble() / 1000);
            for (int i = 0; i < listaPesosGDobles.size(); i++) {
                peso += Float.parseFloat(listaPesosGDobles.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "G. Doble", cantiGdoble, peso + merma, precioGdoble);
        }
        if (listaPesosGNegras != null && !listaPesosGNegras.isEmpty()) {
            float peso = 0;
            float merma = cantiGnegra * (float) (cliente.getMermaGnegra() / 1000);
            for (int i = 0; i < listaPesosGNegras.size(); i++) {
                peso += Float.parseFloat(listaPesosGNegras.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "G. Negra", cantiGnegra, peso + merma, precioGnegra);
        }
        if (listaPesosGRojas != null && !listaPesosGRojas.isEmpty()) {
            float peso = 0;
            float merma = cantidGroja * (float) (cliente.getMermaGroja() / 1000);
            for (int i = 0; i < listaPesosGRojas.size(); i++) {
                peso += Float.parseFloat(listaPesosGRojas.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "G. Roja", cantidGroja, peso + merma, precioGroja);
        }
        if (listaPesosPatos != null && !listaPesosPatos.isEmpty()) {
            float peso = 0;
            float merma = cantiPato * (float) (cliente.getMermaPato() / 1000);
            for (int i = 0; i < listaPesosPatos.size(); i++) {
                peso += Float.parseFloat(listaPesosPatos.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Pato", cantiPato, peso + merma, precioPato);
        }
        if (listaPesosPavos != null && !listaPesosPavos.isEmpty()) {
            float peso = 0;
            float merma = cantiPavo * (float) (cliente.getMermaPavo() / 1000);
            for (int i = 0; i < listaPesosPavos.size(); i++) {
                peso += Float.parseFloat(listaPesosPavos.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Pavo", cantiPavo, peso + merma, precioPavo);
        }
        if (listaPesosPechoE != null && !listaPesosPechoE.isEmpty()) {
            float peso = 0;
            float merma = cantiPechoE * (float) (cliente.getMermaPechoe() / 1000);
            for (int i = 0; i < listaPesosPechoE.size(); i++) {
                peso += Float.parseFloat(listaPesosPechoE.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Pecho E", cantiPechoE, peso + merma, precioPechoE);
        }
        if (listaPesosPiernaE != null && !listaPesosPiernaE.isEmpty()) {
            float peso = 0;
            float merma = cantiPiernaE * (float) (cliente.getMermaPiernae() / 1000);
            for (int i = 0; i < listaPesosPiernaE.size(); i++) {
                peso += Float.parseFloat(listaPesosPiernaE.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Pierna E", cantiPiernaE, peso + merma, precioPiernaE);
        }
        if (listaPesosEspinazo != null && !listaPesosEspinazo.isEmpty()) {
            float peso = 0;
            float merma = cantiEspinazo * (float) (cliente.getMermaEspinazo() / 1000);
            for (int i = 0; i < listaPesosEspinazo.size(); i++) {
                peso += Float.parseFloat(listaPesosEspinazo.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Espinazo", cantiEspinazo, peso + merma, precioEspinazo);
        }
        if (listaPesosMenudencia != null && !listaPesosMenudencia.isEmpty()) {
            float peso = 0;
            float merma = cantiMenudencia * (float) (cliente.getMermaMenudencia() / 1000);
            for (int i = 0; i < listaPesosMenudencia.size(); i++) {
                peso += Float.parseFloat(listaPesosMenudencia.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Menudencia", cantiMenudencia, peso + merma, precioMenudencia);
        }
        if (listaPesosAla != null && !listaPesosAla.isEmpty()) {
            float peso = 0;
            float merma = cantiAla * (float) (cliente.getMermaAla() / 1000);
            for (int i = 0; i < listaPesosAla.size(); i++) {
                peso += Float.parseFloat(listaPesosAla.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Ala", cantiAla, peso + merma, precioAla);
        }
        if (listaPesosOtros != null && !listaPesosOtros.isEmpty()) {
            float peso = 0;
            float merma = cantiOtros * (float) (cliente.getMermaOtros() / 1000);
            for (int i = 0; i < listaPesosOtros.size(); i++) {
                peso += Float.parseFloat(listaPesosOtros.get(i).getText().toString());
            }
            llenarTabla(tableLayout, "Otros", cantiOtros, peso + merma, precioOtros);
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
        for (int i = 0; i < subtotal.size(); i++) {
            subt += Float.parseFloat(subtotal.get(i).getText().toString());
        }
        subt = (int) (subt * 100);
        montoSubtotal = subt / 100.0f;
        //montoSubtotal = Float.parseFloat(String.valueOf(Math.floor(subt * 100) / 100));
        //montoSubtotal = new BigDecimal(subt).setScale(2, RoundingMode.HALF_UP).floatValue();
       /* DecimalFormat df = new DecimalFormat("#.##");
        String twoDigitNum = df.format(subt);*/
        //montoSubtotal = (Float) Float.parseFloat(twoDigitNum);
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
            valor.setText(String.valueOf(montoSubtotal));
        }
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

        /*// Establece la accion para el cambio de focus de los campos de texto
        precio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    int index = editText.getId();
                    float pres;
                    float suma;
                    if (!editText.getText().toString().isEmpty()) {
                        pres = Float.parseFloat(editText.getText().toString());
                        suma = cantidades[index] * pesos[index] * pres;
                        // actualiza la lista para el pdf
                        listaDatosPDF.get(index).setPrecio(pres);
                        listaDatosPDF.get(index).setTotal(suma);
                    } else {
                        pres = 0;
                        suma = cantidades[index] * pesos[index] * pres;
                    }
                    total.setText(String.valueOf(suma));
                    validarListaSubTotal(subtotal);
                    validarListaPDF(listaDatosPDF);
                    llenarFilaSubtotal(tabLayout, tituloSubtotal, valorSubtotal);
                }
            }
        });*/
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
                        listaDatosPDF.get(index).setPrecio(pres);
                        listaDatosPDF.get(index).setTotal(suma);
                    } else {
                        pres = 0;
                        suma = cantidades[index] * pesos[index] * pres;
                    }
                    total.setText(String.valueOf(suma));
                    validarListaSubTotal(subtotal);
                    validarListaPDF(listaDatosPDF);
                    llenarFilaSubtotal(tabLayout, tituloSubtotal, valorSubtotal);
                }
                return false;
            }
        });

        /*// Establece la accion para cuando se termina de escribir
        precio.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                EditText editText = (EditText) v;
                                int index = editText.getId();
                                float pres;
                                float suma;
                                if (!editText.getText().toString().isEmpty()) {
                                    pres = Float.parseFloat(editText.getText().toString());
                                    suma = cantidades[index] * pesos[index] * pres;
                                    // actualiza la lista para el pdf
                                    listaDatosPDF.get(index).setPrecio(pres);
                                    listaDatosPDF.get(index).setTotal(suma);
                                } else {
                                    pres = 0;
                                    suma = cantidades[index] * pesos[index] * pres;
                                }
                                total.setText(String.valueOf(suma));
                                validarListaSubTotal(subtotal);
                                validarListaPDF(listaDatosPDF);
                                llenarFilaSubtotal(tabLayout, tituloSubtotal, valorSubtotal);
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );*/

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
        listaDatosPDF.add(new Boleta(idUsuario, Integer.parseInt(idCliente), listaIdProductos.get(id), fecha, nomprod, canti, pesoKG, precioUnitario, (pesoKG * precioUnitario)));
        id++;
    }

    public boolean hasPermission(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String permission = permissions[0];
        int result = grantResults[0];
        switch (requestCode) {
            case PERMISSION_ALL:
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //Comprobar si ha sido aceptado o denegado el permiso
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        //concedio permiso
                    } else {
                        //no concedio
                        Toast.makeText(getContext(), "No concediste permiso", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }*/

    //Valida el permiso de escritura y lectura externa
    private void versionesAnteriores() {
        if (CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                && CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            llenarPDF(fecha);
            verPDF();
            Toast.makeText(getContext(), "Los PDF se guardan en la carpeta TunquiSolutionsPDF", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Llena los datos basicos del pdf con datos por default
     *
     * @param nombre
     */
    private void llenarPDF(String nombre) {

        pdf = new TemplatePDF(getContext());
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
        pdf.createTable(header, getProductos(listaDatosPDF));
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

    private boolean CheckPermission(String permission) {
        int result = getContext().checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.verBoleta) {
            if (ventaRegistrada) {
                ocultarTecladoDe(getView());
                validarPermisos();
            } else
                Toast.makeText(getContext(), "Primero registre la venta", Toast.LENGTH_SHORT).show();
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

    /*
    Setters de las diferentes listas
     */

    public void setListaPesosGDobles(List<EditText> listaPesosGDobles) {
        this.listaPesosGDobles = listaPesosGDobles;
    }

    public void setListaPesosPollos(List<EditText> listaPesosPollos) {
        this.listaPesosPollos = listaPesosPollos;
    }

    public void setListaPesosGNegras(List<EditText> listaPesosGNegras) {
        this.listaPesosGNegras = listaPesosGNegras;
    }

    public void setListaPesosGRojas(List<EditText> listaPesosGRojas) {
        this.listaPesosGRojas = listaPesosGRojas;
    }

    public void setListaPesosPatos(List<EditText> listaPesosPatos) {
        this.listaPesosPatos = listaPesosPatos;
    }

    public void setListaPesosPavos(List<EditText> listaPesosPavos) {
        this.listaPesosPavos = listaPesosPavos;
    }

    public void setListaPesosPechoE(List<EditText> listaPesosPechoE) {
        this.listaPesosPechoE = listaPesosPechoE;
    }

    public void setListaPesosPiernaE(List<EditText> listaPesosPiernaE) {
        this.listaPesosPiernaE = listaPesosPiernaE;
    }

    public void setListaPesosEspinazo(List<EditText> listaPesosEspinazo) {
        this.listaPesosEspinazo = listaPesosEspinazo;
    }

    public void setListaPesosMenudencia(List<EditText> listaPesosMenudencia) {
        this.listaPesosMenudencia = listaPesosMenudencia;
    }

    public void setListaPesosAla(List<EditText> listaPesosAla) {
        this.listaPesosAla = listaPesosAla;
    }

    public void setListaPesosOtros(List<EditText> listaPesosOtros) {
        this.listaPesosOtros = listaPesosOtros;
    }

    public void setPrecioPollo(float precioPollo) {
        this.precioPollo = precioPollo;
    }

    public void setPrecioGdoble(float precioGdoble) {
        this.precioGdoble = precioGdoble;
    }

    public void setPrecioGnegra(float precioGnegra) {
        this.precioGnegra = precioGnegra;
    }

    public void setPrecioGroja(float precioGroja) {
        this.precioGroja = precioGroja;
    }

    public void setPrecioPato(float precioPato) {
        this.precioPato = precioPato;
    }

    public void setPrecioPavo(float precioPavo) {
        this.precioPavo = precioPavo;
    }

    public void setPrecioPechoE(float precioPechoE) {
        this.precioPechoE = precioPechoE;
    }

    public void setPrecioPiernaE(float precioPiernaE) {
        this.precioPiernaE = precioPiernaE;
    }

    public void setPrecioEspinazo(float precioEspinazo) {
        this.precioEspinazo = precioEspinazo;
    }

    public void setPrecioMenudencia(float precioMenudencia) {
        this.precioMenudencia = precioMenudencia;
    }

    public void setPrecioAla(float precioAla) {
        this.precioAla = precioAla;
    }

    public void setPrecioOtros(float precioOtros) {
        this.precioOtros = precioOtros;
    }

    public void setCantiAla(int cantiAla) {
        this.cantiAla = cantiAla;
    }

    public void setCantiOtros(int cantiOtros) {
        this.cantiOtros = cantiOtros;
    }

    public void setCantiPollo(int cantiPollo) {
        this.cantiPollo = cantiPollo;
    }

    public void setCantiGdoble(int cantiGdoble) {
        this.cantiGdoble = cantiGdoble;
    }

    public void setCantiGnegra(int cantiGnegra) {
        this.cantiGnegra = cantiGnegra;
    }

    public void setCantidGroja(int cantidGroja) {
        this.cantidGroja = cantidGroja;
    }

    public void setCantiPato(int cantiPato) {
        this.cantiPato = cantiPato;
    }

    public void setCantiPavo(int cantiPavo) {
        this.cantiPavo = cantiPavo;
    }

    public void setCantiPechoE(int cantiPechoE) {
        this.cantiPechoE = cantiPechoE;
    }

    public void setCantiPiernaE(int cantiPiernaE) {
        this.cantiPiernaE = cantiPiernaE;
    }

    public void setCantiEspinazo(int cantiEspinazo) {
        this.cantiEspinazo = cantiEspinazo;
    }

    public void setCantiMenudencia(int cantiMenudencia) {
        this.cantiMenudencia = cantiMenudencia;
    }
}
