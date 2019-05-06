package namelessbliss.tunquisolutions.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import namelessbliss.tunquisolutions.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrarPago extends Fragment {

    String idBoleta, idUsuario, idCliente, nombreCliente, montoSubtotal, saldo, total, fecha;

    // Pago que se registra
    float pago;

    RequestQueue queue;

    TextView txtnombreC;
    EditText saldoAnterior, montoPagar, subtotal, etTotal, saldoActual;
    Button btnRegistrarPago;


    public RegistrarPago() {
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
        View view = inflater.inflate(R.layout.fragment_registrar_pago, container, false);
        queue = Volley.newRequestQueue(getContext());

        //fecha
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int month = mMonth + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        fecha = mDay + "-" + month + "-" + mYear;

        txtnombreC = view.findViewById(R.id.nombreCliente);
        saldoAnterior = view.findViewById(R.id.saldoAnterior);
        saldoActual = view.findViewById(R.id.saldoActual);
        montoPagar = view.findViewById(R.id.montoPagar);
        subtotal = view.findViewById(R.id.subtotal);
        etTotal = view.findViewById(R.id.total);

        btnRegistrarPago = view.findViewById(R.id.btnRegistrarPago);

        obtenerDatos();

        establecerDatos();

        // Establece la accion para cuando se escribe
        montoPagar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent keyEvent) {
                String valor;
                float tot;
                if (keyEvent != null) {
                    EditText editText = (EditText) v;
                    if (!editText.getText().toString().isEmpty()) {
                        valor = editText.getText().toString();
                        pago = Float.parseFloat(valor);
                        tot = Float.parseFloat(total);
                        saldoActual.setText(String.valueOf(tot - pago));
                    } else {
                        pago = 0;
                        tot = Float.parseFloat(total);
                        saldoActual.setText(String.valueOf(tot - pago));
                    }
                }
                return false;
            }
        });

        btnRegistrarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegistrarPago.setEnabled(false);
                btnRegistrarPago.setVisibility(View.GONE);
                ocultarTecladoDe(view);
                registrarSaldo();
            }
        });
        return view;
    }

    private void establecerDatos() {
        txtnombreC.setText(nombreCliente);
        subtotal.setText(montoSubtotal);
        obtenerSaldo();
    }

    private void obtenerSaldo() {
        saldoAnterior();
    }

    private void saldoAnterior() {
        String server_url = "http://avicolas.skapir.com/obtener_saldo_cliente.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty())
                            saldo = response;
                        else
                            saldo = "0";
                        saldoAnterior.setText(saldo);
                        total = String.valueOf(Float.parseFloat(montoSubtotal) + Float.parseFloat(saldo));
                        etTotal.setText(total);
                        Toast.makeText(getActivity(), "¡Saldo obtenido!", Toast.LENGTH_LONG).show();
                        btnRegistrarPago.setEnabled(true);
                        btnRegistrarPago.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "¡Error al obtener saldo!", Toast.LENGTH_LONG).show();
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

                return param;
            }
        };
        queue.add(stringRequest);
    }

    private void registrarSaldo() {

        String server_url = "http://avicolas.skapir.com/registrar_saldo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String respuesta = jsonObject.getString("RESPUESTA");
                            if (respuesta.equalsIgnoreCase("COMPLETADO")) {
                                dialogoAlerta();
                                btnRegistrarPago.setEnabled(true);
                                btnRegistrarPago.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnRegistrarPago.setEnabled(true);
                        btnRegistrarPago.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "¡Error al registrar pago!", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idBoleta", idBoleta);
                param.put("idUsuario", idUsuario);
                param.put("idCliente", idCliente);
                param.put("saldoAnterior", saldoAnterior.getText().toString());
                param.put("saldoActual", saldoActual.getText().toString());
                param.put("pago", String.valueOf(pago));
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
        idBoleta = bundle.getString("ID_BOLETA");
        idUsuario = bundle.getString("ID_USUARIO");
        idCliente = bundle.getString("ID_CLIENTE");
        nombreCliente = bundle.getString("NOMBRE_CLIENTE");
        montoSubtotal = bundle.getString("MONTO");
        bundle.clear();
    }

    public static void ocultarTecladoDe(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void dialogoAlerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alertDialog2);

        builder.setMessage("Se registro el pago satisfactoriamente").setTitle("Información")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        regresarVistaBoletasCliente();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Limpia las vista de edita boleta y regresa a las boletas del cliente
     */
    private void regresarVistaBoletasCliente() {
        BoletasCliente boletasCliente = new BoletasCliente();
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("ID_CLIENTE", idCliente);
        boletasCliente.setArguments(bundle);
        assert fragmentManager != null;
        fragmentManager.popBackStack("Boleta", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.Contenedor, boletasCliente).addToBackStack("Boleta").commit();
    }

}
