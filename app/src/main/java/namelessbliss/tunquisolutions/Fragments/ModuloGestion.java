package namelessbliss.tunquisolutions.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import namelessbliss.tunquisolutions.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModuloGestion extends Fragment {

    Button btnCompra, btnReporteFinanciero, btnCuadroUtilidades;


    public ModuloGestion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modulo_gestion, container, false);

        btnCompra = view.findViewById(R.id.btnGestionCompra);
        btnReporteFinanciero = view.findViewById(R.id.btnReporteFinanciero);
        btnCuadroUtilidades = view.findViewById(R.id.btnCuadroUtilidades);


        btnCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.Contenedor, new Compra())
                        .addToBackStack(null).commit();
            }
        });

        btnReporteFinanciero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.Contenedor, new ReporteFinanciero())
                        .addToBackStack(null).commit();
            }
        });

        btnCuadroUtilidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.Contenedor, new CuadroUtilidades())
                        .addToBackStack(null).commit();
            }
        });

        return view;

    }

}
