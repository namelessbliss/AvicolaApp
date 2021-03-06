package namelessbliss.tunquisolutions.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class CuadroUtilidades extends Fragment {

    TableLayout tablaUtilidad;

    String nombreCliente, fechaActual;
    float ventaDia = 0, utilidadCliente = 0, precioCompraDia = 0, peso = 0, utilidadDia = 0;
    int cantidad = 0;

    final float MERMA_FIJA = 0.350f;

    EditText etFecha, etUtilidadDia;

    // User Session Manager Class
    UserSessionManager session;

    RequestQueue queue;

    // get user data from session
    HashMap<String, String> user;

    // id
    String idUsuario;

    public CuadroUtilidades() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cuadro_utilidades, container, false);

        //fecha
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int month = mMonth + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        fechaActual = mDay + "-" + month + "-" + mYear;

        tablaUtilidad = view.findViewById(R.id.tablaUtilidades);
        etFecha = view.findViewById(R.id.editTextFecha);
        etUtilidadDia = view.findViewById(R.id.etUtilidadDia);

        // Session class instance
        session = new UserSessionManager(getContext());

        // get user data from session
        user = session.getUserDetails();

        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);
        queue = Volley.newRequestQueue(getContext());

        obtenerPrecioCompraDia();

        return view;
    }


    private void obtenerCuadroUtilidades() {
        etFecha.setText(fechaActual);
        String server_url = "http://avicolas.skapir.com/cuadro_utilidades_cliente.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonAry = null;
                        try {
                            jsonAry = new JSONArray(response);
                            for (int i = 0; i < jsonAry.length(); i++) {
                                JSONObject jSONObject = (JSONObject) jsonAry.getJSONObject(i);
                                String cliente = jSONObject.getString("cliente");
                                ventaDia = Float.parseFloat(jSONObject.getString("ventaDia"));
                                peso = Float.parseFloat(jSONObject.getString("peso"));
                                cantidad = Integer.parseInt(jSONObject.getString("cantidad"));

                                //calculo de la utilidad
                                float subt = ventaDia - ((cantidad * MERMA_FIJA) + peso) * precioCompraDia;
                                subt = (int) (subt * 100);
                                utilidadCliente = subt / 100.0f;
                                utilidadDia += utilidadCliente;
                                llenarTabla(tablaUtilidad, cliente, String.valueOf(utilidadCliente));
                            }
                            etUtilidadDia.setText(String.valueOf(utilidadDia));

                            Toast.makeText(getContext(), "Datos obtenidos", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "No hay datos registrados el día de hoy", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "No se pudo obtener los datos", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("fecha", fechaActual);

                return param;
            }
        };
        queue.add(stringRequest);
    }

    private void obtenerPrecioCompraDia() {
        String server_url = "http://avicolas.skapir.com/precio_compra_dia.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            precioCompraDia = Float.parseFloat(jsonObject.getString("precioCompra"));
                            obtenerCuadroUtilidades();
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "No hay datos registrados el día de hoy", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "No se pudo obtener los datos", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("fecha", fechaActual);

                return param;
            }
        };
        queue.add(stringRequest);

    }


    private void llenarTabla(TableLayout tableLayout, String nombreCliente, String ventaDia) {
        //if (fecha.equalsIgnoreCase(fechaActual)) {

        llenarDatosTabla(tableLayout, nombreCliente, ventaDia);

        //} else {
        //  llenarDatosTabla(tableLayout, nombreCliente, "0");
        //}

    }

    private void llenarDatosTabla(TableLayout tableLayout, String nombreCliente, String ventaDia) {
        //Instancia de nueva fila
        TableRow row = new TableRow(getContext());
        //Instancias de objetos de la ui
        TextView txtVentaDia = new TextView(getContext());
        TextView txtNombre = new TextView(getContext());

        txtNombre.setText(nombreCliente);
        txtNombre.setPadding(2, 1, 2, 1);
        txtNombre.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto));
        txtNombre.setMaxLines(1);
        txtNombre.setGravity(Gravity.CENTER);

        txtVentaDia.setText(ventaDia);
        txtVentaDia.setPadding(2, 1, 2, 1);
        txtVentaDia.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto));
        txtVentaDia.setMaxLines(1);
        txtVentaDia.setGravity(Gravity.CENTER);


        row.addView(txtNombre);
        row.addView(txtVentaDia);
        tableLayout.addView(row);
    }
}
