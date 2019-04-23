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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import namelessbliss.tunquisolutions.DatosMenuManager.DatosMenu;
import namelessbliss.tunquisolutions.Modelo.DatosMenuCliente;
import namelessbliss.tunquisolutions.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClienteMenu extends Fragment {

    DatosMenu datosMenu;

    ArrayList<DatosMenuCliente> datosMenuClientes;

    DatosMenuCliente cliente;

    String idCliente, nombre;
    Button guardarCambios;
    Switch gdoble, pollo, gnegra, groja, pato, pavo, pechoe, piernae, espinazo, menudencia;
    boolean estadoDoble = false, estadoPollo = false, estadoNegra = false, estadoRoja = false, estadoPato = false,
            estadoPavo = false, estadoPechoe = false, estadoPiernae = false, estadoEspinazo = false, estadoMenudencia = false;

    double mermaPollo = 0, mermaGdoble = 0, mermaGnegra = 0, mermaGroja = 0,
            mermaPato = 0, mermaPavo = 0, mermaPiernae = 0, mermaPechoe = 0,
            mermaEspinazo = 0, mermaMenudencia = 0;

    EditText etmermaPollo, etmermaGdoble, etmermaGnegra, etmermaGroja, etmermaPato, etmermaPavo, etmermaPechoE,
            etmermaPiernaE, etmermaEspinazo, etmermaMenudencia;

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


    public ClienteMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cliente_menu, container, false);

        datosMenu = new DatosMenu(getContext());
        datosMenuClientes = new ArrayList<>();

        //Captura los elementos de la vista
        capturarElementos(view);

        obtenerDatos();

        establecerSwitch(pollo);
        establecerSwitch(gdoble);
        establecerSwitch(gnegra);
        establecerSwitch(groja);
        establecerSwitch(pato);
        establecerSwitch(pavo);
        establecerSwitch(piernae);
        establecerSwitch(pechoe);
        establecerSwitch(espinazo);
        establecerSwitch(menudencia);


        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DetalleCliente detalleCliente = new DetalleCliente();
                Bundle bundle = new Bundle();
                bundle.putString("ID_CLIENTE", idCliente);
                bundle.putString("NOMBRE_CLIENTE", nombre);
                detalleCliente.setArguments(bundle);
                datosMenu.eliminarDatosMenu();
                capturarMermas();
                if (cliente != null) {
                    datosMenuClientes.remove(cliente);
                }
                if (datosMenuClientes != null) {
                    datosMenuClientes.add(new DatosMenuCliente(nombre, estadoPollo, estadoDoble, estadoNegra, estadoRoja, estadoPato,
                            estadoPavo, estadoPiernae, estadoPechoe, estadoEspinazo, estadoMenudencia, mermaPollo, mermaGdoble, mermaGnegra,
                            mermaGroja, mermaPato, mermaPavo, mermaPiernae, mermaPechoe, mermaEspinazo, mermaMenudencia));
                    datosMenu.setDatosMenu(datosMenuClientes);
                } else {
                    datosMenuClientes = new ArrayList<>();
                    datosMenuClientes.add(new DatosMenuCliente(nombre, estadoPollo, estadoDoble, estadoNegra, estadoRoja, estadoPato,
                            estadoPavo, estadoPiernae, estadoPechoe, estadoEspinazo, estadoMenudencia, mermaPollo, mermaGdoble, mermaGnegra,
                            mermaGroja, mermaPato, mermaPavo, mermaPiernae, mermaPechoe, mermaEspinazo, mermaMenudencia));
                    datosMenu.setDatosMenu(datosMenuClientes);
                }
                fragmentManager.beginTransaction()
                        .add(new DetalleCliente(), "DetalleCliente")
                        .addToBackStack("DetalleCliente")
                        .replace(R.id.Contenedor, detalleCliente)
                        .commit();
            }
        });

        return view;
    }

    private void capturarMermas() {
        if (estadoPollo)
            if (!etmermaPollo.getText().toString().isEmpty())
                mermaPollo = Double.parseDouble(etmermaPollo.getText().toString());
        if (estadoDoble)
            if (!etmermaGdoble.getText().toString().isEmpty())
                mermaGdoble = Double.parseDouble(etmermaGdoble.getText().toString());
        if (estadoNegra)
            if (!etmermaGnegra.getText().toString().isEmpty())
                mermaGnegra = Double.parseDouble(etmermaGnegra.getText().toString());
        if (estadoRoja)
            if (!etmermaGroja.getText().toString().isEmpty())
                mermaGroja = Double.parseDouble(etmermaGroja.getText().toString());
        if (estadoPato)
            if (!etmermaPato.getText().toString().isEmpty())
                mermaPato = Double.parseDouble(etmermaPato.getText().toString());
        if (estadoPavo)
            if (!etmermaPavo.getText().toString().isEmpty())
                mermaPavo = Double.parseDouble(etmermaPavo.getText().toString());
        if (estadoPechoe)
            if (!etmermaPechoE.getText().toString().isEmpty())
                mermaPechoe = Double.parseDouble(etmermaPechoE.getText().toString());
        if (estadoPiernae)
            if (!etmermaPiernaE.getText().toString().isEmpty())
                mermaPiernae = Double.parseDouble(etmermaPiernaE.getText().toString());
        if (estadoEspinazo)
            if (!etmermaEspinazo.getText().toString().isEmpty())
                mermaEspinazo = Double.parseDouble(etmermaEspinazo.getText().toString());
        if (estadoMenudencia)
            if (!etmermaMenudencia.getText().toString().isEmpty())
                mermaMenudencia = Double.parseDouble(etmermaMenudencia.getText().toString());
    }

    private void capturarElementos(View view) {
        guardarCambios = view.findViewById(R.id.guardarCambios);
        pollo = view.findViewById(R.id.pollo);
        gdoble = view.findViewById(R.id.doble);
        gnegra = view.findViewById(R.id.negra);
        groja = view.findViewById(R.id.roja);
        pato = view.findViewById(R.id.pato);
        pavo = view.findViewById(R.id.pavo);
        pechoe = view.findViewById(R.id.pechoe);
        piernae = view.findViewById(R.id.piernae);
        espinazo = view.findViewById(R.id.espinazo);
        menudencia = view.findViewById(R.id.menudencia);

        etmermaPollo = view.findViewById(R.id.mermaPollo);
        etmermaGdoble = view.findViewById(R.id.mermaGDoble);
        etmermaGnegra = view.findViewById(R.id.mermaGNegra);
        etmermaGroja = view.findViewById(R.id.mermaGRoja);
        etmermaPato = view.findViewById(R.id.mermaPato);
        etmermaPavo = view.findViewById(R.id.mermaPavo);
        etmermaPechoE = view.findViewById(R.id.mermaPechoE);
        etmermaPiernaE = view.findViewById(R.id.mermaPiernaE);
        etmermaEspinazo = view.findViewById(R.id.mermaEspinazo);
        etmermaMenudencia = view.findViewById(R.id.mermaMenudencia);
    }

    private void establecerSwitch(final Switch aSwitch) {
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (aSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Activado", Toast.LENGTH_SHORT).show();
                    switch (aSwitch.getId()) {
                        case R.id.pollo:
                            etmermaPollo.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaPollo.setText(String.valueOf(cliente.getMermaPollo()));
                            estadoPollo = true;
                            break;
                        case R.id.doble:
                            etmermaGdoble.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaGdoble.setText(String.valueOf(cliente.getMermaGdoble()));
                            estadoDoble = true;
                            break;
                        case R.id.roja:
                            etmermaGroja.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaGroja.setText(String.valueOf(cliente.getMermaGroja()));
                            estadoRoja = true;
                            break;
                        case R.id.negra:
                            etmermaGnegra.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaGnegra.setText(String.valueOf(cliente.getMermaGnegra()));
                            estadoNegra = true;
                            break;
                        case R.id.pato:
                            etmermaPato.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaPato.setText(String.valueOf(cliente.getMermaPato()));
                            estadoPato = true;
                            break;
                        case R.id.pavo:
                            etmermaPavo.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaPavo.setText(String.valueOf(cliente.getMermaPavo()));
                            estadoPavo = true;
                            break;
                        case R.id.piernae:
                            etmermaPiernaE.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaPiernaE.setText(String.valueOf(cliente.getMermaPiernae()));
                            estadoPiernae = true;
                            break;
                        case R.id.pechoe:
                            etmermaPechoE.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaPechoE.setText(String.valueOf(cliente.getMermaPechoe()));
                            estadoPechoe = true;
                            break;
                        case R.id.espinazo:
                            etmermaEspinazo.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaEspinazo.setText(String.valueOf(cliente.getMermaEspinazo()));
                            estadoEspinazo = true;
                            break;
                        case R.id.menudencia:
                            etmermaMenudencia.setVisibility(View.VISIBLE);
                            if (cliente != null)
                                etmermaMenudencia.setText(String.valueOf(cliente.getMermaMenudencia()));
                            estadoMenudencia = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getContext(), "Desactivado", Toast.LENGTH_SHORT).show();
                    switch (aSwitch.getId()) {
                        case R.id.pollo:
                            etmermaPollo.setVisibility(View.INVISIBLE);
                            mermaPollo = 0;
                            estadoPollo = false;
                            break;
                        case R.id.doble:
                            etmermaGdoble.setVisibility(View.INVISIBLE);
                            mermaGdoble = 0;
                            estadoDoble = false;
                            break;
                        case R.id.roja:
                            etmermaGroja.setVisibility(View.INVISIBLE);
                            mermaGroja = 0;
                            estadoRoja = false;
                            break;
                        case R.id.negra:
                            etmermaGnegra.setVisibility(View.INVISIBLE);
                            mermaGnegra = 0;
                            estadoNegra = false;
                            break;
                        case R.id.pato:
                            etmermaPato.setVisibility(View.INVISIBLE);
                            mermaPato = 0;
                            estadoPato = false;
                            break;
                        case R.id.pavo:
                            etmermaPavo.setVisibility(View.INVISIBLE);
                            mermaPavo = 0;
                            estadoPavo = false;
                            break;
                        case R.id.piernae:
                            etmermaPiernaE.setVisibility(View.INVISIBLE);
                            mermaPiernae = 0;
                            estadoPiernae = false;
                            break;
                        case R.id.pechoe:
                            etmermaPechoE.setVisibility(View.INVISIBLE);
                            mermaPechoe = 0;
                            estadoPechoe = false;
                            break;
                        case R.id.espinazo:
                            etmermaEspinazo.setVisibility(View.INVISIBLE);
                            mermaEspinazo = 0;
                            estadoEspinazo = false;
                            break;
                        case R.id.menudencia:
                            etmermaMenudencia.setVisibility(View.INVISIBLE);
                            mermaMenudencia = 0;
                            estadoMenudencia = false;
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private void obtenerDatos() {
        Bundle bundle = getArguments();
        //Establece el estado de los switch
        if (bundle != null) {
            idCliente = bundle.getString("ID_CLIENTE");
            nombre = bundle.getString("NOMBRE_CLIENTE");
            /*
            estadoPollo = bundle.getBoolean("POLLO");
                    estadoDoble = bundle.getBoolean("DOBLE");
                    estadoRoja = bundle.getBoolean("ROJA");
                    estadoNegra = bundle.getBoolean("NEGRA");
                    estadoPato = bundle.getBoolean("PATO");
                    estadoPavo = bundle.getBoolean("PAVO");
                    estadoPiernae = bundle.getBoolean("PIERNAE");
                    estadoPechoe = bundle.getBoolean("PECHOE");
                    estadoEspinazo = bundle.getBoolean("ESPINAZO");
                    estadoMenudencia = bundle.getBoolean("MENUDENCIA");
                    establecerDatos();
             */
            bundle.clear();
        }
        datosMenuClientes = datosMenu.getDatosMenu();
        if (datosMenuClientes != null && !datosMenuClientes.isEmpty()) {
            for (DatosMenuCliente menu : datosMenuClientes) {
                if (menu.getNombre().equalsIgnoreCase(nombre))
                    cliente = menu;
            }
        }
        if (cliente != null) {
            estadoPollo = cliente.isPollo();
            estadoDoble = cliente.isGdoble();
            estadoRoja = cliente.isGroja();
            estadoNegra = cliente.isGnegra();
            estadoPato = cliente.isPato();
            estadoPavo = cliente.isPavo();
            estadoPiernae = cliente.isPiernae();
            estadoPechoe = cliente.isPechoe();
            estadoEspinazo = cliente.isEspinazo();
            estadoMenudencia = cliente.isMenudencia();
            establecerDatos();
        }
    }

    private void establecerDatos() {
        pollo.setChecked(estadoPollo);
        gdoble.setChecked(estadoDoble);
        groja.setChecked(estadoRoja);
        gnegra.setChecked(estadoNegra);
        pato.setChecked(estadoPato);
        pavo.setChecked(estadoPavo);
        piernae.setChecked(estadoPiernae);
        pechoe.setChecked(estadoPechoe);
        espinazo.setChecked(estadoEspinazo);
        menudencia.setChecked(estadoMenudencia);
        etablecerMermas();
    }

    private void etablecerMermas() {
        if (estadoPollo)
            etmermaPollo.setVisibility(View.VISIBLE);
        etmermaPollo.setText(String.valueOf(cliente.getMermaPollo()));
        if (estadoDoble)
            etmermaGdoble.setVisibility(View.VISIBLE);
        etmermaGdoble.setText(String.valueOf(cliente.getMermaGdoble()));
        if (estadoRoja)
            etmermaGroja.setVisibility(View.VISIBLE);
        etmermaGroja.setText(String.valueOf(cliente.getMermaGroja()));
        if (estadoNegra)
            etmermaGnegra.setVisibility(View.VISIBLE);
        etmermaGnegra.setText(String.valueOf(cliente.getMermaGnegra()));
        if (estadoPato)
            etmermaPato.setVisibility(View.VISIBLE);
        etmermaPato.setText(String.valueOf(cliente.getMermaPato()));
        if (estadoPavo)
            etmermaPavo.setVisibility(View.VISIBLE);
        etmermaPavo.setText(String.valueOf(cliente.getMermaPavo()));
        if (estadoPiernae)
            etmermaPiernaE.setVisibility(View.VISIBLE);
        etmermaPiernaE.setText(String.valueOf(cliente.getMermaPiernae()));
        if (estadoPechoe)
            etmermaPechoE.setVisibility(View.VISIBLE);
        etmermaPechoE.setText(String.valueOf(cliente.getMermaPechoe()));
        if (estadoEspinazo)
            etmermaEspinazo.setVisibility(View.VISIBLE);
        etmermaEspinazo.setText(String.valueOf(cliente.getMermaEspinazo()));
        if (estadoMenudencia)
            etmermaMenudencia.setVisibility(View.VISIBLE);
        etmermaMenudencia.setText(String.valueOf(cliente.getMermaMenudencia()));
    }
}
