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
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class ReporteFinanciero extends Fragment {

    RequestQueue queue;

    EditText etFecha, etGanancia, etPago, etUtilidad;
    ImageButton addCampoPago;
    Button btnRegistrarCalcular, btnObtenerReporte;

    String idUsuario, fecha;

    float gananciaDia = 0, pagoTotal = 0;

    //Declaracion de los contenedores
    LinearLayout linearCampos;


    // User Session Manager Class
    UserSessionManager session;
    // get user data from session
    HashMap<String, String> user;

    // Declaracion de listado de tipos de campos de texto

    ArrayList<EditText> listaCamposPagos;

    ArrayList<Float> listaPagos;

    public ReporteFinanciero() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reporte_financiero, container, false);

        queue = Volley.newRequestQueue(getContext());

        // Session class instance
        session = new UserSessionManager(getContext());
        // get user data from session
        user = session.getUserDetails();
        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        linearCampos = view.findViewById(R.id.campoPagosTrabajadores);
        etFecha = view.findViewById(R.id.editTextFecha);
        etGanancia = view.findViewById(R.id.etGananciaDia);
        etUtilidad = view.findViewById(R.id.etGananciaFinal);
        etPago = view.findViewById(R.id.etPago);
        addCampoPago = view.findViewById(R.id.addCampoPago);
        btnRegistrarCalcular = view.findViewById(R.id.btnRegistrarCalcular);
        btnObtenerReporte = view.findViewById(R.id.obtenerReporte);

        configCalendar();

        listaCamposPagos = new ArrayList<>();
        listaPagos = new ArrayList<>();
        establecerBoton(addCampoPago, listaCamposPagos, linearCampos);

        establecerBtnObtenerReporte(view);
        establecerBtnRegistrarDatos(view);

        obtenerDatos();


        //TODO


        return view;
    }


    public void configCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        etFecha.setText(day + "-" + month + "-" + year);

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTecladoDe(view);
                DialogFragment selecDialogFragment = new SelectDateFragment();
                ((SelectDateFragment) selecDialogFragment).setEtFecha(etFecha);

                btnRegistrarCalcular.setEnabled(false);
                btnRegistrarCalcular.setVisibility(View.GONE);
                linearCampos.removeAllViews();
                listaPagos.removeAll(listaPagos);
                listaCamposPagos.removeAll(listaCamposPagos);
                establecerBoton(addCampoPago, listaCamposPagos, linearCampos);
                selecDialogFragment.show(getFragmentManager(), "DatePicker");
            }
        });


    }


    private void establecerBoton(ImageButton Boton, final List<EditText> lista, final LinearLayout linear) {
        //Añade campo inicial
        listaCamposPagos.add(etPago);
        Boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTecladoDe(view);
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
                ocultarTecladoDe(view);
                fecha = etFecha.getText().toString();
                obtenerReporte();
            }
        });
    }

    private void establecerBtnRegistrarDatos(View view) {
        btnRegistrarCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTecladoDe(view);
                btnRegistrarCalcular.setEnabled(false);
                btnRegistrarCalcular.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Registrando Datos", Toast.LENGTH_SHORT).show();
                validarCampos();
                validarLista(listaCamposPagos);
                sumarPagos(listaCamposPagos);
                fecha = etFecha.getText().toString();
                registrarDatos();
            }
        });
    }

    public void obtenerReporte() {

        String server_url = "http://avicolas.skapir.com/obtener_reporte_financiero.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.length() > 0) {
                                Toast.makeText(getActivity(), "Datos Obtenidos", Toast.LENGTH_SHORT).show();
                                gananciaDia = Float.parseFloat(jsonObject.getString("ganancia"));
                                etGanancia.setText(String.valueOf(gananciaDia));
                                pagoTotal = Float.parseFloat(jsonObject.getString("pago"));
                                etPago.setText(String.valueOf(pagoTotal));
                                etUtilidad.setText(String.valueOf(gananciaDia - pagoTotal));

                                btnRegistrarCalcular.setEnabled(false);
                                btnRegistrarCalcular.setVisibility(View.GONE);
                                addCampoPago.setEnabled(false);
                                addCampoPago.setVisibility(View.GONE);
                                linearCampos.removeAllViews();
                                listaPagos.removeAll(listaPagos);
                                listaCamposPagos.removeAll(listaCamposPagos);
                                establecerBoton(addCampoPago, listaCamposPagos, linearCampos);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "El reporte no existe, registre los datos", Toast.LENGTH_LONG).show();
                            etGanancia.setText("");
                            etPago.setText("");
                            etUtilidad.setText("");
                            btnRegistrarCalcular.setEnabled(true);
                            btnRegistrarCalcular.setVisibility(View.VISIBLE);
                            addCampoPago.setEnabled(true);
                            addCampoPago.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "¡No se pudo obtener el reporte!", Toast.LENGTH_LONG).show();
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
        String server_url = "http://avicolas.skapir.com/registrar_pago_utilidades.php";

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
                                String valor = String.valueOf(gananciaDia - pagoTotal);
                                etUtilidad.setText(valor);
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
                        btnRegistrarCalcular.setEnabled(true);
                        btnRegistrarCalcular.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("pagoTotal", String.valueOf(pagoTotal));
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


    private void obtenerDatos() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            fecha = bundle.getString("fecha");
            etFecha.setText(fecha);
            gananciaDia = bundle.getFloat("gananciaDia");
            etGanancia.setText(String.valueOf(gananciaDia));
            btnObtenerReporte.setEnabled(false);
            btnObtenerReporte.setVisibility(View.GONE);
            etFecha.setEnabled(false);
        } else {
            btnRegistrarCalcular.setEnabled(false);
            btnRegistrarCalcular.setVisibility(View.GONE);
        }
    }

    private void validarCampos() {
        if (!etGanancia.getText().toString().isEmpty())
            gananciaDia = Float.parseFloat(etGanancia.getText().toString());
        else
            etGanancia.setText("0");
    }

    private void sumarPagos(ArrayList<EditText> lista) {

        for (EditText et : lista) {
            listaPagos.add(Float.parseFloat(et.getText().toString()));
        }
        for (Float val : listaPagos) {
            pagoTotal += val;
        }
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
