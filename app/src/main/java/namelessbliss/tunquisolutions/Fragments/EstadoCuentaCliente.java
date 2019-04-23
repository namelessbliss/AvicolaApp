package namelessbliss.tunquisolutions.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class EstadoCuentaCliente extends Fragment {

    TableLayout tablaSaldo;

    String saldoActual, nombreCliente, idCliente, pago, fecha, fechaActual;

    // User Session Manager Class
    UserSessionManager session;

    RequestQueue queue;
    Bundle bundle;
    //ListView listView;

    // get user data from session
    HashMap<String, String> user;

    // id
    String idUsuario;

    // nombre
    String nombre;


    public EstadoCuentaCliente() {
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
        View view = inflater.inflate(R.layout.fragment_estado_cuenta_cliente, container, false);

        //fecha
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int month = mMonth + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        fechaActual = mDay + "-" + month + "-" + mYear;

        tablaSaldo = view.findViewById(R.id.tablaSaldo);

        // Session class instance
        session = new UserSessionManager(getContext());

        // get user data from session
        user = session.getUserDetails();

        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        // get nombre
        nombre = user.get(UserSessionManager.KEY_NAME);

        queue = Volley.newRequestQueue(getContext());

        //obtenerDatosCliente();
        estadoDeCuenta();

        return view;
    }

    private void obtenerDatosCliente() {
        Bundle bundle = getArguments();
        idCliente = bundle.getString("ID_CLIENTE");
        nombreCliente = bundle.getString("NOMBRE_CLIENTE");
    }

    private void llenarTabla(TableLayout tableLayout, String nombreCliente, String pago, String saldo, String fecha) {
        if (fecha.equalsIgnoreCase(fechaActual)) {

            llenarDatosTabla(tableLayout, nombreCliente, pago, saldo);

        } else {
            llenarDatosTabla(tableLayout, nombreCliente, "0", saldo);
        }

    }

    private void llenarDatosTabla(TableLayout tableLayout, String nombreCliente, String pago, String saldo) {
        //Instancia de nueva fila
        TableRow row = new TableRow(getContext());
        //Instancias de objetos de la ui
        TextView txtPago = new TextView(getContext());
        TextView txtSaldo = new TextView(getContext());
        TextView txtNombre = new TextView(getContext());

        txtNombre.setText(nombreCliente);
        txtNombre.setPadding(2, 1, 2, 1);
        txtNombre.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto));
        txtNombre.setMaxLines(1);
        txtNombre.setGravity(Gravity.CENTER);

        txtPago.setText(pago);
        txtPago.setPadding(2, 1, 2, 1);
        txtPago.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto));
        txtPago.setMaxLines(1);
        txtPago.setGravity(Gravity.CENTER);

        txtSaldo.setText(saldo);
        txtSaldo.setPadding(2, 1, 2, 1);
        txtSaldo.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto));
        txtSaldo.setMaxLines(1);
        txtSaldo.setGravity(Gravity.CENTER);

        row.addView(txtNombre);
        row.addView(txtPago);
        row.addView(txtSaldo);
        tableLayout.addView(row);
    }

    private void estadoDeCuenta() {
        String server_url = "http://avicolas.skapir.com/estado_de_cuenta.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonAry = null;
                        try {
                            jsonAry = new JSONArray(response);
                            for (int i = 0; i < jsonAry.length(); i++) {
                                JSONObject jSONObject = jsonAry.getJSONObject(i);
                                nombreCliente = jSONObject.getString("NOMBRE_CLIENTE");
                                pago = jSONObject.getString("pago");
                                saldoActual = jSONObject.getString("saldo");
                                fecha = jSONObject.getString("fecha");
                                llenarTabla(tablaSaldo, nombreCliente, pago, saldoActual, fecha);
                            }
                            Toast.makeText(getContext(),"Datos obtenidos",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String result = response.toString();
                        Log.d("response", "result : " + result); //when response come i will log it

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"No se pudo obtener los datos",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);

                return param;
            }
        };
        queue.add(stringRequest);
    }

}
