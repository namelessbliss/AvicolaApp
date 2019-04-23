package namelessbliss.tunquisolutions.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import namelessbliss.tunquisolutions.Adaptadores.AdaptadorListView;
import namelessbliss.tunquisolutions.Modelo.Boleta;
import namelessbliss.tunquisolutions.Modelo.Cliente;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;


public class Boletas extends Fragment implements AdapterView.OnItemClickListener {

    String idCliente, nombreCliente;

    // User Session Manager Class
    UserSessionManager session;

    private List<Cliente> listClientes = new ArrayList<>();
    RequestQueue queue;
    Bundle bundle;
    ListView listView;

    // get user data from session
    HashMap<String, String> user;

    // id
    String idUsuario;

    // nombre
    String nombre;

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
        View view = inflater.inflate(R.layout.fragment_boletas, container, false);

        // Session class instance
        session = new UserSessionManager(getContext());

        // get user data from session
        user = session.getUserDetails();

        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        // get nombre
        nombre = user.get(UserSessionManager.KEY_NAME);

        listClientes.removeAll(listClientes);

        listView = (ListView) view.findViewById(R.id.listViewBoleta);
        // Adjuntamos el método click para la vista de list view
        listView.setOnItemClickListener(this);

        ListView listView = (ListView) view.findViewById(R.id.listViewBoleta);
        // Adjuntamos el método click para la vista de list view
        listView.setOnItemClickListener(this);

        //AdaptadorListView listadapter = new AdaptadorListView(getActivity(), R.layout.list_view_clientes, listBoletas);

        /*ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItems
        );*/

        //listView.setAdapter(listadapter);
        queue = Volley.newRequestQueue(getContext());
        getClientes();
        return view;
    }

    private void getClientes() {
        String server_url = "http://avicolas.skapir.com/obtener_nombres_clientes.php";
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
                            AdaptadorListView listadapter = new AdaptadorListView(getContext(), R.layout.list_view_clientes, listClientes);

                            listView.setAdapter(listadapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        clickCliente(listClientes.get(position));
    }

    public void clickCliente(Cliente cliente) {

        if (cliente != null) {

            BoletasCliente bolCliente = new BoletasCliente();

            establecerBundle(cliente, bolCliente);

            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.Contenedor, bolCliente).addToBackStack(null).commit();

        }

    }

    private void establecerBundle(Cliente cliente, BoletasCliente bolCliente) {
        Bundle bundle = new Bundle();
        bundle.putString("ID_CLIENTE", cliente.getIdCliente());
        bundle.putString("NOMBRE_CLIENTE", cliente.getNombre());
        bolCliente.setArguments(bundle);
    }
}
