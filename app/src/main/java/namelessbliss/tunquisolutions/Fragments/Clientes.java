package namelessbliss.tunquisolutions.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import namelessbliss.tunquisolutions.Adaptadores.AdaptadorListView;
import namelessbliss.tunquisolutions.Modelo.Cliente;
import namelessbliss.tunquisolutions.Modelo.Servidor;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;


public class Clientes extends Fragment implements AdapterView.OnItemClickListener {

    String idCliente, nombreCliente;

    String url = new Servidor().getSERVIDOR_URL();

    // User Session Manager Class
    UserSessionManager session;

    private List<Cliente> listClientes = new ArrayList<>();
    RequestQueue queue;
    Bundle bundle;
    ListView listView;

    AdaptadorListView listadapter;

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

        // Session class instance
        session = new UserSessionManager(getContext());

        // get user data from session
        user = session.getUserDetails();

        // get id
        idUsuario = user.get(UserSessionManager.KEY_ID);

        // get nombre
        nombre = user.get(UserSessionManager.KEY_NAME);

        listClientes.removeAll(listClientes);
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);


        listView = (ListView) view.findViewById(R.id.listView);
        // Adjuntamos el método click para la vista de list view
        listView.setOnItemClickListener(this);
        /*
        listClientes = new ArrayList<>();
        listClientes.add(new Cliente("Adolfo", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Pedro", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Joan", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Gabriela", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Susi", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Juan", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Alex", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Bety", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Susana", R.mipmap.ic_pollo));
        listClientes.add(new Cliente("Marta", R.mipmap.ic_pollo));
        */

        //AdaptadorListView listadapter = new AdaptadorListView(getActivity(), R.layout.list_view_clientes, listClientes);

        /*ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItems
        );*/

        //listView.setAdapter(listadapter);
        queue = Volley.newRequestQueue(getContext());
        getClientes();

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Compara y ordena los nombres de la lista de clientes
     */
    private void sortArrayList() {
        Collections.sort(listClientes, new Comparator<Cliente>() {
            @Override
            public int compare(Cliente c1, Cliente c2) {
                return c1.getNombre().compareToIgnoreCase(c2.getNombre());
            }
        });

        listadapter.notifyDataSetChanged();
    }

    private void getClientes() {

        String server_url = url + "obtener_nombres_clientes.php"; // url of server check this 100 times it must be working

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        final String result = response.toString();
                        //System.out.println(result);
                        //Log.d("response", "result : " + result); //when response come i will log it
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

                            sortArrayList();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "No se pudo obtener lista de clientes", Toast.LENGTH_LONG).show();
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

            listadapter.notifyDataSetChanged();

            DetalleCliente detalleCliente = new DetalleCliente();

            establecerBundle(cliente, detalleCliente);

            FragmentManager manager = getFragmentManager();
            manager.popBackStack();
            manager.beginTransaction()
                    .add(new Clientes(), "Cliente")
                    .addToBackStack("Cliente")
                    .replace(R.id.Contenedor, detalleCliente)
                    .commit();

        }

    }

    private void establecerBundle(Cliente cliente, DetalleCliente detalleCliente) {
        Bundle bundle = new Bundle();
        bundle.putString("ID_CLIENTE", cliente.getIdCliente());
        bundle.putString("NOMBRE_CLIENTE", cliente.getNombre());
        bundle.putBoolean("POLLO", true);
        bundle.putBoolean("DOBLE", false);
        bundle.putBoolean("ROJA", false);
        bundle.putBoolean("NEGRA", false);
        bundle.putBoolean("PATO", false);
        bundle.putBoolean("PAVO", false);
        bundle.putBoolean("PECHOE", false);
        bundle.putBoolean("PIERNAE", false);
        bundle.putBoolean("ESPINAZO", false);
        bundle.putBoolean("MENUDENCIA", false);
        detalleCliente.setArguments(bundle);
    }

}
