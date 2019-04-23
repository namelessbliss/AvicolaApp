package namelessbliss.tunquisolutions.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import namelessbliss.tunquisolutions.Adaptadores.AdaptadorListView;
import namelessbliss.tunquisolutions.Modelo.Cliente;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class IngresoClientes extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    String idCliente, nombreCliente, saldo;

    // User Session Manager Class
    UserSessionManager session;

    private List<Cliente> listClientes = new ArrayList<>();
    RequestQueue queue;
    Bundle bundle;
    ListView listView;

    // get user data from session
    HashMap<String, String> user;

    AdaptadorListView listadapter;

    // id
    String idUsuario;

    // nombre
    String nombre;

    Button btnAgregarCliente;
    EditText etNombre, etSaldo;


    public IngresoClientes() {
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
        View view = inflater.inflate(R.layout.fragment_ingreso_clientes, container, false);
        // Session class instance
        session = new UserSessionManager(getContext());

        // get user data from session
        user = session.getUserDetails();

        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        // get nombre
        nombre = user.get(UserSessionManager.KEY_NAME);

        listClientes.removeAll(listClientes);

        listadapter = new AdaptadorListView(getContext(), R.layout.list_view_clientes, listClientes);

        btnAgregarCliente = view.findViewById(R.id.btnAgregarCliente);
        etNombre = view.findViewById(R.id.nombreClienteIngreso);
        etSaldo = view.findViewById(R.id.saldoClienteIngreso);

        listView = (ListView) view.findViewById(R.id.listViewClientes);
        // Adjuntamos el método click para la vista de list view
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        queue = Volley.newRequestQueue(getContext());
        getClientes();

        btnAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAgregarCliente.setEnabled(false);
                validarCampos();
                ocultarTecladoDe(view);
                if (!nombreCliente.isEmpty()) {
                    registrarCliente(nombreCliente, saldo);
                    Toast.makeText(getContext(), "Registrando Cliente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ingrese un nombre valido", Toast.LENGTH_SHORT).show();
                    btnAgregarCliente.setEnabled(true);
                }
            }
        });

        return view;
    }

    private void validarCampos() {
        // Operador ternario ---- variable = (CONDICION) ? valorTRUE : valorFalse
        nombreCliente = (etNombre.getText().toString().isEmpty()) ? "" : etNombre.getText().toString();
        saldo = (etSaldo.getText().toString().isEmpty()) ? "0" : etSaldo.getText().toString();
    }

    private void registrarCliente(final String nombreC, final String saldoC) {

        String server_url = "http://avicolas.skapir.com/registrar_cliente.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("RESPUESTA").equalsIgnoreCase("COMPLETADO")) {
                                Toast.makeText(getContext(), "Cliente registrado", Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnAgregarCliente.setEnabled(true);
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                        Toast.makeText(getContext(), "No se pudo agregar cliente. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("idUsuario", idUsuario);
                param.put("nombreCliente", nombreC);
                param.put("saldoCliente", saldoC);
                param.put("estadoCliente", "1");

                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }

    private void limpiarCampos() {
        btnAgregarCliente.setEnabled(true);
        etNombre.setText("");
        etSaldo.setText("");
        listClientes.removeAll(listClientes);
        listadapter.notifyDataSetChanged();
        getClientes();
    }

    private void getClientes() {

        String server_url = "http://avicolas.skapir.com/obtener_nombres_clientes.php"; // url of server check this 100 times it must be working
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        final String result = response.toString();
                        System.out.println(result);
                        Log.d("response", "result : " + result); //when response come i will log it
                        JSONArray jsonAry = null;
                        try {
                            jsonAry = new JSONArray(response);
                            for (int i = 0; i < jsonAry.length(); i++) {
                                JSONObject jSONObject = jsonAry.getJSONObject(i);
                                idCliente = jSONObject.getString("ID_CLIENTE");
                                nombreCliente = jSONObject.getString("NOMBRE_CLIENTE");
                                listClientes.add(new Cliente(idCliente, nombreCliente, R.mipmap.ic_pollo));
                            }
                            listadapter = new AdaptadorListView(getContext(), R.layout.list_view_clientes, listClientes);

                            listView.setAdapter(listadapter);
                        } catch (JSONException e) {
                            listadapter = new AdaptadorListView(getContext(), R.layout.list_view_clientes, listClientes);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listadapter = new AdaptadorListView(getContext(), R.layout.list_view_clientes, listClientes);
                        Toast.makeText(getContext(), "No se pudo obtener lista de clientes", Toast.LENGTH_SHORT).show();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(getContext(), listClientes.get(position).getNombre(), Toast.LENGTH_SHORT).show();
        //clickCliente(listClientes.get(position));
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        idCliente = listClientes.get(position).getIdCliente();
        String nom = listClientes.get(position).getNombre();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alertDialog);

        builder.setMessage("¿Estás seguro de que quieres eliminar a " + nom)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listClientes.remove(position);
                        eliminarCliente();
                        listadapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancelar", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return true;
    }

    private void eliminarCliente() {
        String server_url = "http://avicolas.skapir.com/eliminar_cliente.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("COMPLETADO")) {
                            Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        error.getMessage(); // when error come i will log it
                        Toast.makeText(getContext(), "No se pudo eliminar", Toast.LENGTH_SHORT).show();
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

    public static void ocultarTecladoDe(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
