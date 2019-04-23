package namelessbliss.tunquisolutions.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class ReportePesos extends Fragment {

    RequestQueue queue;
    float precioCompra, pesoCompraTotal, capitalInversion, ventaTotal, gananciaDia;

    String fecha;

    String idUsuario;

    // User Session Manager Class
    UserSessionManager session;
    // get user data from session
    HashMap<String, String> user;

    EditText etFecha, etPesoCompra, etPesoVenta, etPerdidaPeso, etCapitalInversion, etGananciaDia,
            etVentaDia;

    Button btnFinanzas;

    public ReportePesos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reporte_pesos, container, false);

        queue = Volley.newRequestQueue(getContext());

        // Session class instance
        session = new UserSessionManager(getContext());
        // get user data from session
        user = session.getUserDetails();
        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        etFecha = view.findViewById(R.id.editTextFecha);
        etPesoCompra = view.findViewById(R.id.etPesosCompra);
        etPesoVenta = view.findViewById(R.id.etPesosVenta);
        etPerdidaPeso = view.findViewById(R.id.etPerdidaPeso);
        etCapitalInversion = view.findViewById(R.id.etCapitalInversion);
        etVentaDia = view.findViewById(R.id.etVentaDia);
        etGananciaDia = view.findViewById(R.id.etGananciaDia);
        btnFinanzas = view.findViewById(R.id.btnFinanzas);

        obtenerDatos();

        obtenerPesoVenta();

        obtenerGanancia();

        btnFinanzas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goReporteFinanzas();

            }
        });

        return view;
    }


    private void obtenerGanancia() {
        String server_url = "http://avicolas.skapir.com/obtener_ganancia_dia.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jSONObject = jsonArray.getJSONObject(i);
                                String venta = jSONObject.getString("SUM(peso*precio)");
                                if (!venta.equalsIgnoreCase("null")) {
                                    ventaTotal = Float.parseFloat(venta);
                                } else {
                                    venta = "0";
                                    ventaTotal = Float.parseFloat(venta);
                                }
                                etVentaDia.setText(venta);
                                gananciaDia = ventaTotal - capitalInversion;
                                etGananciaDia.setText(String.valueOf(gananciaDia));
                                btnFinanzas.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "El reporte no existe", Toast.LENGTH_LONG).show();
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

        queue.add(stringRequest);
    }

    private void obtenerPesoVenta() {
        String server_url = "http://avicolas.skapir.com/obtener_peso_venta.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jSONObject = jsonArray.getJSONObject(i);
                                String peso = jSONObject.getString("sum(peso)");
                                if (!peso.equalsIgnoreCase("null"))
                                    etPesoVenta.setText(peso);
                                else
                                    peso = "0";
                                etPesoVenta.setText(peso);
                                etPerdidaPeso.setText(String.valueOf(pesoCompraTotal - Float.parseFloat(peso)));
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "El reporte no existe", Toast.LENGTH_LONG).show();
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

        queue.add(stringRequest);
    }

    private void goReporteFinanzas() {

        ReporteFinanciero reporteFinanciero = new ReporteFinanciero();
        Bundle bundle = new Bundle();
        bundle.putString("fecha", fecha);
        bundle.putFloat("gananciaDia", gananciaDia);
        reporteFinanciero.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Contenedor, reporteFinanciero).addToBackStack(null).commit();

    }

    private void obtenerDatos() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            fecha = bundle.getString("fecha");
            pesoCompraTotal = bundle.getFloat("pesoCompraTotal");
            precioCompra = bundle.getFloat("precioCompra");
            capitalInversion = bundle.getFloat("capitalInversion");
            etFecha.setText(fecha);
            etPesoCompra.setText(String.valueOf(pesoCompraTotal));
            etCapitalInversion.setText(String.valueOf(capitalInversion));
        }
    }

}
