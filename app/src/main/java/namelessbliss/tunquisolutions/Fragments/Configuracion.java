package namelessbliss.tunquisolutions.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import namelessbliss.tunquisolutions.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Configuracion extends Fragment{

    Button datosEmpresa, ingresoClientes;


    public Configuracion() {
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
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        datosEmpresa = view.findViewById(R.id.datosEmpresa);
        ingresoClientes = view.findViewById(R.id.ingresoClientes);


        datosEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.Contenedor, new DatosEmpresa()).addToBackStack(null).commit();
            }
        });

        ingresoClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.Contenedor, new IngresoClientes()).addToBackStack(null).commit();
            }
        });


        return view;
    }
}
