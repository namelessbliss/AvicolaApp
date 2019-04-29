package namelessbliss.tunquisolutions.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import namelessbliss.tunquisolutions.Adaptadores.AdaptadorListViewBoletas;
import namelessbliss.tunquisolutions.Modelo.Boleta;
import namelessbliss.tunquisolutions.R;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;
import namelessbliss.tunquisolutions.TemplatePDF;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoletasCliente extends Fragment implements AdapterView.OnItemClickListener {

    String idCliente, nombreCliente;
    int limiteInf = 0;
    int limiteSup = 10;

    // clase constructora de pdf
    TemplatePDF pdf;
    //cabecera de tabla de pdf
    String[] header = {"Poducto", "Cantidad", "Peso", "Precio", "Total"};

    // User Session Manager Class
    UserSessionManager session;

    private ArrayList<Boleta> listaBoletas = new ArrayList<>();
    private ArrayList<Boleta> listaBoletasOriginal = new ArrayList<>();
    RequestQueue queue;
    Bundle bundle;
    ListView listView;

    // get user data from session
    HashMap<String, String> user;

    // id
    String idUsuario;

    // nombre
    String nombre;

    String fecha = "";

    EditaBoleta editaBoleta;

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

    public BoletasCliente() {
        // Required empty public constructor
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

        listaBoletas.removeAll(listaBoletas);

        obtenerDatosCliente();

        listView = (ListView) view.findViewById(R.id.listViewBoleta);
        // Adjuntamos el método click para la vista de list view
        listView.setOnItemClickListener(this);

        ListView listView = (ListView) view.findViewById(R.id.listViewBoleta);
        // Adjuntamos el método click para la vista de list view
        listView.setOnItemClickListener(this);

        queue = Volley.newRequestQueue(getContext());
        getBoletas();
        return view;
    }

    private void obtenerDatosCliente() {
        Bundle bundle = getArguments();
        idCliente = bundle.getString("ID_CLIENTE");
        nombreCliente = bundle.getString("NOMBRE_CLIENTE");
    }

    private void getBoletas() {
        String server_url = "http://avicolas.skapir.com/obtener_boletas_cliente.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        final String result = response.toString();
                        Log.d("response", "result : " + result); //when response come i will log it
                        JSONArray jsonAry = null;
                        try {
                            jsonAry = new JSONArray(response);
                            for (int i = 0; i < jsonAry.length(); i++) {
                                JSONObject jSONObject = jsonAry.getJSONObject(i);
                                if (!jSONObject.getString("FECHA").equalsIgnoreCase(fecha)) {
                                    fecha = jSONObject.getString("FECHA");
                                    listaBoletas.add(new Boleta(R.mipmap.ic_boleta,
                                            jSONObject.getInt("ID_BOLETA"),jSONObject.getInt("ID_USUARIO"),
                                            jSONObject.getInt("ID_CLIENTE"), jSONObject.getString("FECHA"),
                                            Float.parseFloat(jSONObject.getString("total"))));
                                }
                            }
                            AdaptadorListViewBoletas listadapterBol = new AdaptadorListViewBoletas(getContext(), R.layout.list_view_boletas, listaBoletas);

                            listView.setAdapter(listadapterBol);
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
                param.put("idCliente", idCliente);
/*                param.put("limiteInf", String.valueOf(limiteInf));
                param.put("limiteSup", String.valueOf(limiteSup));*/

                return param;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        clickBoleta(listaBoletas.get(position));
    }

    public void clickBoleta(Boleta boleta) {

        if (boleta != null) {
            editaBoleta = new EditaBoleta();
            FragmentManager manager = getFragmentManager();
            Bundle bundle1 = new Bundle();
            bundle1.putString("ID_BOLETA", String.valueOf(boleta.getIdBoleta()));
            bundle1.putString("ID_USUARIO", idUsuario);
            bundle1.putString("ID_CLIENTE", idCliente);
            bundle1.putString("NOMBRE_CLIENTE", nombreCliente);
            bundle1.putString("FECHA", boleta.getFecha());
            editaBoleta.setArguments(bundle1);
            manager.beginTransaction().replace(R.id.Contenedor, editaBoleta).addToBackStack(null).commit();

            //llenarPDF(boleta);
            //verPDF();
        }

    }

    /*private void establecerBundle(Boleta boleta, BoletasCliente bolCliente) {
        Bundle bundle = new Bundle();
        bundle.putString("ID_CLIENTE", idCliente);
        bundle.putString("NOMBRE_CLIENTE", nombreCliente);
        bolCliente.setArguments(bundle);
    }*/

    /*private void llenarPDF(Boleta bol) {

        pdf = new TemplatePDF(getContext());
        pdf.openDocument(bol.getFecha());
        pdf.addMetaData("TunquiSolutions", "Boleta", "TunquiSolutions");
        // load image
        Drawable d = getContext().getResources().getDrawable(R.drawable.tunqui_logo);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        try {
            Image image = Image.getInstance(stream.toByteArray());
            pdf.addImgName(image);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pdf.addTitles("BOLETA DE VENTA : ", "Cliente : "+nombreCliente, fecha);
        pdf.createTable(header, getProductos(bol,listaBoletasOriginal));
        pdf.closeDocument();
    }*/

    /*private void verPDF() {
        pdf.viewPDF();
    }*/

   /* private ArrayList<String[]> getProductos(Boleta bol, ArrayList<Boleta> lista) {
        int idBol = bol.getIdBoleta();
        ArrayList<String[]> rows = new ArrayList<>();
        for (Boleta bole : lista) {
            if (bole.getIdBoleta() == idBol) {
                rows.add(new String[]{bole.getProducto(), String.valueOf(bole.getCantidad()),
                        String.valueOf(bole.getPesoNeto()), String.valueOf(bole.getPrecio()),
                        String.valueOf(bole.getTotal())});
            }
        }
        return rows;
    }
    */
}
