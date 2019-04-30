package namelessbliss.tunquisolutions.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import namelessbliss.tunquisolutions.Modelo.SelectDateFragment;
import namelessbliss.tunquisolutions.Modelo.Servidor;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class Compra extends Fragment {

    String url = new Servidor().getSERVIDOR_URL();

    RequestQueue queue;

    String idUsuario, fecha;

    // User Session Manager Class
    UserSessionManager session;
    // get user data from session
    HashMap<String, String> user;

    private Calendar calendar;
    //Campos de la vista compra
    EditText etFecha, etPrecioCompra, etNumeroJabas, etTara, etDevolucion, etPeso, etCapital;
    // boton para añadir mas compos de pesos
    ImageButton addCampo;
    // botones de la vista compra
    Button btnGenerarReporte, btnRegistrarDatos, btnObtenerReporte;

    //Declaracion de los contenedores
    LinearLayout linearCampos;

    // Declaracion de listado de tipos de campos de texto
    ArrayList<EditText> listaCamposPesos;

    ArrayList<Float> listaPesos;

    private float precioCompra = 0;
    private int numJabas = 0;
    private float valTara = 0;
    private float valDevolucion = 0;

    private float pesoTotal = 0;
    private float pesoBruto = 0;
    private float pesoNeto = 0;
    private float capitalInversion = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compra, container, false);
        queue = Volley.newRequestQueue(getContext());

        // Session class instance
        session = new UserSessionManager(getContext());
        // get user data from session
        user = session.getUserDetails();
        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        etFecha = view.findViewById(R.id.editTextFechaCompra);
        etPrecioCompra = view.findViewById(R.id.editTextPrecioCompra);
        etNumeroJabas = view.findViewById(R.id.editTextNumeroJabas);
        etTara = view.findViewById(R.id.editTextTara);
        etDevolucion = view.findViewById(R.id.editTextDevolucion);
        etPeso = view.findViewById(R.id.editTextPeso);
        etCapital = view.findViewById(R.id.editTextCapital);
        addCampo = view.findViewById(R.id.addCampo);
        btnGenerarReporte = view.findViewById(R.id.btnGenerarReporte);
        btnRegistrarDatos = view.findViewById(R.id.registrarDatos);
        btnObtenerReporte = view.findViewById(R.id.obtenerReporte);
        configCalendar();

        linearCampos = view.findViewById(R.id.linearPesos);
        listaCamposPesos = new ArrayList<>();
        listaPesos = new ArrayList<>();
        establecerBoton(addCampo, listaCamposPesos, linearCampos);

        establecerBtnObtenerReporte(view);
        establecerBtnRegistrarDatos(view);
        establecerBtnReporte(view);

        btnGenerarReporte.setEnabled(true);

        return view;
    }

    /**
     * Establece la fecha actual del campo calendario y
     * establece la accion de seleccionar fecha
     */
    public void configCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        etFecha.setText(day + "-" + month + "-" + year);

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment selecDialogFragment = new SelectDateFragment();
                ((SelectDateFragment) selecDialogFragment).setEtFecha(etFecha);
                limpiarDatos();
                selecDialogFragment.show(getFragmentManager(), "DatePicker");
            }
        });


    }

    /**
     * Refresca el estado de los componentes de la vista y restablece valor de las variables
     */
    private void limpiarDatos() {
        btnGenerarReporte.setEnabled(false);
        btnGenerarReporte.setVisibility(View.GONE);
        btnRegistrarDatos.setEnabled(false);
        btnRegistrarDatos.setVisibility(View.GONE);
        addCampo.setEnabled(false);
        addCampo.setVisibility(View.GONE);
        linearCampos.removeAllViews();
        listaPesos.removeAll(listaPesos);
        listaCamposPesos.removeAll(listaCamposPesos);
        establecerBoton(addCampo, listaCamposPesos, linearCampos);
        precioCompra = 0;
        pesoBruto = 0;
        pesoNeto = 0;
        pesoTotal = 0;
        numJabas = 0;
        valTara = 0;
        valDevolucion = 0;
    }

    private void establecerBoton(ImageButton Boton, final List<EditText> lista, final LinearLayout linear) {
        //Añade campo inicial
        listaCamposPesos.add(etPeso);
        Boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orden = lista.size() + 1;
                EditText t = new EditText(getContext());
                //Establece el largo y alto
                t.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                //Lo hace solo decimal
                t.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                t.setHint("Ingrese peso n° " + orden);
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoCampo));
                t.setMaxLines(1);
                t.setPadding(0, 10, 0, 10);
                t.setGravity(Gravity.CENTER);

                linear.addView(t);
                lista.add(t);
            }
        });
    }

    private void establecerBtnObtenerReporte(View view) {
        btnObtenerReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fecha = etFecha.getText().toString();
                obtenerReporte();
            }
        });
    }


    private void establecerBtnReporte(View view) {
        btnGenerarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGenerarReporte.setEnabled(false);
                ocultarTecladoDe(view);
                validarLista(listaCamposPesos);
                sumarPesos(listaCamposPesos);
                validarCampos();
                generarReporte();

            }
        });
    }

    private void generarReporte() {
        ReportePesos reportePesos = new ReportePesos();
        Bundle bundle = new Bundle();
        bundle.putString("fecha", fecha);
        bundle.putFloat("pesoCompraTotal", pesoTotal);
        bundle.putFloat("precioCompra", precioCompra);
        bundle.putFloat("capitalInversion", capitalInversion);
        reportePesos.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Contenedor, reportePesos).addToBackStack(null).commit();
    }

    private void establecerBtnRegistrarDatos(View view) {
        btnRegistrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegistrarDatos.setEnabled(false);
                btnRegistrarDatos.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Registrando Datos", Toast.LENGTH_SHORT).show();
                validarLista(listaCamposPesos);
                sumarPesos(listaCamposPesos);
                validarCampos();
                calcularCapitalInversion();
                fecha = etFecha.getText().toString();
                registrarDatos();
            }
        });
    }

    /**
     * Valida los compos de la vista y calcula el capital de inversion
     */
    private void validarCampos() {
        if (!etPrecioCompra.getText().toString().isEmpty())
            precioCompra = Float.parseFloat(etPrecioCompra.getText().toString());
        else
            etPrecioCompra.setText("0");
        if (!etNumeroJabas.getText().toString().isEmpty())
            numJabas = Integer.parseInt(etNumeroJabas.getText().toString());
        else
            etNumeroJabas.setText("0");
        if (!etTara.getText().toString().isEmpty())
            valTara = Float.parseFloat(etTara.getText().toString());
        else
            etTara.setText("0");
        if (!etDevolucion.getText().toString().isEmpty())
            valDevolucion = Float.parseFloat(etDevolucion.getText().toString());
        else
            etDevolucion.setText("0");
        if (!etCapital.getText().toString().isEmpty())
            capitalInversion = Float.parseFloat(etCapital.getText().toString());
        else
            etCapital.setText("0");
    }

    private void calcularCapitalInversion() {
        pesoBruto = pesoTotal - (numJabas * valTara);
        pesoNeto = pesoBruto - valDevolucion;

        //Trunca a dos decimales
        float subt = pesoNeto * precioCompra;
        subt = (int) (subt * 100);

        capitalInversion = subt / 100.0f;

        etCapital.setText(String.valueOf(capitalInversion));
    }

    private void sumarPesos(ArrayList<EditText> lista) {

        for (EditText et : lista) {
            listaPesos.add(Float.parseFloat(et.getText().toString()));
        }
        for (Float val : listaPesos) {
            pesoTotal += val;
        }
    }

    private void obtenerReporte() {

        String server_url = url + "obtener_reporte_existente.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jSONObject = jsonArray.getJSONObject(i);

                                etPrecioCompra.setText(jSONObject.getString("precio_compra"));
                                etNumeroJabas.setText(jSONObject.getString("numero_jaba"));
                                etTara.setText(jSONObject.getString("valor_tara"));
                                etDevolucion.setText(jSONObject.getString("valor_devolucion"));
                                etPeso.setText(jSONObject.getString("peso_compra"));
                                etCapital.setText(jSONObject.getString("capital_inversion"));
                            }
                            btnGenerarReporte.setEnabled(true);
                            btnGenerarReporte.setVisibility(View.VISIBLE);
                            btnRegistrarDatos.setEnabled(false);
                            btnRegistrarDatos.setVisibility(View.GONE);
                            addCampo.setEnabled(false);
                            addCampo.setVisibility(View.GONE);
                            linearCampos.removeAllViews();
                            listaPesos.removeAll(listaPesos);
                            listaCamposPesos.removeAll(listaCamposPesos);
                            establecerBoton(addCampo, listaCamposPesos, linearCampos);

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "El reporte no existe, registre los datos", Toast.LENGTH_LONG).show();
                            etCapital.setText("");
                            btnGenerarReporte.setEnabled(false);
                            btnGenerarReporte.setVisibility(View.GONE);
                            addCampo.setEnabled(true);
                            addCampo.setVisibility(View.VISIBLE);
                            btnRegistrarDatos.setEnabled(true);
                            btnRegistrarDatos.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "¡No se pudo obtener el reporte!", Toast.LENGTH_LONG).show();
                        btnRegistrarDatos.setEnabled(true);
                        btnRegistrarDatos.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("fecha", fecha);

                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    private void registrarDatos() {
        String server_url = url + "registrar_datos_utilidades.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String respuesta = jsonObject.getString("RESPUESTA");
                            if (respuesta.equalsIgnoreCase("COMPLETADO")) {
                                Toast.makeText(getActivity(), "Datos Registrados", Toast.LENGTH_LONG).show();
                                btnGenerarReporte.setEnabled(true);
                                btnGenerarReporte.setVisibility(View.VISIBLE);
                                pesoTotal = 0;
                                precioCompra = 0;
                                capitalInversion = 0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "¡Error al registrar datos. Intentelo de nuevo!", Toast.LENGTH_LONG).show();
                        btnRegistrarDatos.setEnabled(true);
                        btnRegistrarDatos.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("pesoCompra", String.valueOf(pesoTotal));
                param.put("numJabas", String.valueOf(numJabas));
                param.put("valTara", String.valueOf(valTara));
                param.put("valDevolucion", String.valueOf(valDevolucion));
                param.put("precioCompra", String.valueOf(precioCompra));
                param.put("capitalInversion", String.valueOf(capitalInversion));
                param.put("fecha", fecha);

                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }


    private void validarLista(List<EditText> lista) {
        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getText().toString().isEmpty()) {
                    lista.get(i).setText("0");
                }
            }
        }
    }

    public static void ocultarTecladoDe(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
