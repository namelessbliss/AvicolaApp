package namelessbliss.tunquisolutions.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import namelessbliss.tunquisolutions.DatosMenuManager.DatosMenu;
import namelessbliss.tunquisolutions.Modelo.DatosMenuCliente;
import namelessbliss.tunquisolutions.R;

public class DetalleCliente extends Fragment {

    DatosMenu datosMenu;

    ArrayList<DatosMenuCliente> datosMenuClientes;

    DatosMenuCliente cliente;

    String idCliente, nombre;

    //Declaracion de los contenedores
    LinearLayout linearPollos, linearGDoble, linearGRoja, linearGNegra,
            linearPato, linearPavo, linearPechoE, linearPiernaE, linearEspinazo, linearMenudencia,
            linearAla, linearOtros;

    // Declaracion de los elementos

    TextView nombreCliente, txtTextViewPollos, textViewGDoble, textViewGRoja, textViewGNegra,
            textViewPato, textViewPavo, textViewPechoE, textViewPiernaE, textViewEspinazo,
            textViewMenudencia, textViewAla, textViewOtros;

    EditText etPesoPollo, etPesoGdoble, etPesoGroja, etPesoGnegra,
            etPesoPato, etPesoPavo, etPesoPechoE, etPesoPiernaE, etPesoEspinazo, etPesoMenudencia,
            etPesoAla, etPesoOtros,
            etCantiPollo, etCantiGdoble, etCantiGroja, etCantiGnegra, etCantiPato, etCantiPavo, etCantiPechoE,
            etCantiPiernaE, etCantiEspinazo, etCantiMenudencia, etCantiAla, etCantiOtros,
            etPrePollo, etPreGdoble, etPreGroja, etPreGnegra,
            etPrePato, etPrePavo, etPrePechoE, etPrePiernaE, etPreEspinazo, etPreMenudencia, etPreAla,
            etPreOtros;

    Button atras, boletas;

    ImageButton addCampoPollo, addCampoGDoble, addCampoGRoja, addCampoGNegra,
            addCampoPato, addCampoPavo, addCampoPechoE, addCampoPiernaE, addCampoEspinazo,
            addCampoMenudencia, addCampoAla, addCampoOtros;

    // Declaracion de listado de tipos de campos de texto

    List<EditText> listaPesosGDobles, listaPesosPollos, listaPesosGNegras, listaPesosGRojas,
            listaPesosPatos, listaPesosPavos, listaPesosPechoE, listaPesosPiernaE, listaPesosEspinazo,
            listaPesosMenudencia, listaPesosAla, listaPesosOtros;

    // Estado de las opciones

    boolean pollo = false, gdoble = false, groja = false, gnegra = false, pato = false, pavo = false,
            pechoe = false, piernae = false, espinazo = false, menudencia = false, ala = false, otros = false;

    // peso por default para la tabla
    float peso = 0, precioPollo = 0, precioGdoble = 0, precioGnegra = 0, precioGroja = 0, precioPato = 0,
            precioPavo = 0, precioPechoE = 0, precioPiernaE = 0, precioEspinazo = 0, precioMenudencia = 0,
            precioAla = 0, precioOtros = 0;
    // cantidad por default para la tabla
    int cantiPollo = 0, cantiGdoble = 0, cantiGnegra = 0, cantidGroja = 0, cantiPato = 0, cantiPavo = 0,
            cantiPechoE = 0, cantiPiernaE = 0, cantiEspinazo = 0, cantiMenudencia = 0, cantidadAla = 0,
            cantidadOtros;

    ArrayList<Integer> listaIdProductos;

    ProcesaBoleta procesaBoleta;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.opcionesCliente).setVisible(true);
        menu.findItem(R.id.verBoleta).setVisible(false);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalle_cliente, container, false);

        procesaBoleta = new ProcesaBoleta();

        datosMenu = new DatosMenu(getContext());
        datosMenuClientes = new ArrayList<>();

        nombreCliente = view.findViewById(R.id.nombreCliente);
        atras = view.findViewById(R.id.Atras);
        boletas = view.findViewById(R.id.Boleta);

        listaIdProductos = new ArrayList<>();

        obtenerDatos();

        capturarElementos(view);

        if (pollo) {
            //System.out.println("pollo on");
            linearPollos.setVisibility(View.VISIBLE);
            listaPesosPollos = new ArrayList();
            listaPesosPollos.add(etPesoPollo);
            establecerBoton(addCampoPollo, listaPesosPollos, linearPollos);
        }
        if (gdoble) {
            //System.out.println("gdoble on");
            linearGDoble.setVisibility(View.VISIBLE);
            listaPesosGDobles = new ArrayList();
            listaPesosGDobles.add(etPesoGdoble);
            establecerBoton(addCampoGDoble, listaPesosGDobles, linearGDoble);
        }
        if (groja) {
            linearGRoja.setVisibility(View.VISIBLE);
            listaPesosGRojas = new ArrayList();
            listaPesosGRojas.add(etPesoGroja);
            establecerBoton(addCampoGRoja, listaPesosGRojas, linearGRoja);
        }
        if (gnegra) {
            linearGNegra.setVisibility(View.VISIBLE);
            listaPesosGNegras = new ArrayList();
            listaPesosGNegras.add(etPesoGnegra);
            establecerBoton(addCampoGNegra, listaPesosGNegras, linearGNegra);
        }
        if (pato) {
            linearPato.setVisibility(View.VISIBLE);
            listaPesosPatos = new ArrayList();
            listaPesosPatos.add(etPesoPato);
            establecerBoton(addCampoPato, listaPesosPatos, linearPato);
        }
        if (pavo) {
            linearPavo.setVisibility(View.VISIBLE);
            listaPesosPavos = new ArrayList();
            listaPesosPavos.add(etPesoPavo);
            establecerBoton(addCampoPavo, listaPesosPavos, linearPavo);
        }
        if (pechoe) {
            linearPechoE.setVisibility(View.VISIBLE);
            listaPesosPechoE = new ArrayList();
            listaPesosPechoE.add(etPesoPechoE);
            establecerBoton(addCampoPechoE, listaPesosPechoE, linearPechoE);
        }
        if (piernae) {
            linearPiernaE.setVisibility(View.VISIBLE);
            listaPesosPiernaE = new ArrayList();
            listaPesosPiernaE.add(etPesoPiernaE);
            establecerBoton(addCampoPiernaE, listaPesosPiernaE, linearPiernaE);
        }
        if (espinazo) {
            linearEspinazo.setVisibility(View.VISIBLE);
            listaPesosEspinazo = new ArrayList();
            listaPesosEspinazo.add(etPesoEspinazo);
            establecerBoton(addCampoEspinazo, listaPesosEspinazo, linearEspinazo);
        }
        if (menudencia) {
            linearMenudencia.setVisibility(View.VISIBLE);
            listaPesosMenudencia = new ArrayList();
            listaPesosMenudencia.add(etPesoMenudencia);
            establecerBoton(addCampoMenudencia, listaPesosMenudencia, linearMenudencia);
        }
        if (ala) {
            linearAla.setVisibility(View.VISIBLE);
            listaPesosAla = new ArrayList();
            listaPesosAla.add(etPesoAla);
            establecerBoton(addCampoAla, listaPesosAla, linearAla);
        }
        if (otros) {
            linearOtros.setVisibility(View.VISIBLE);
            listaPesosOtros = new ArrayList();
            listaPesosOtros.add(etPesoOtros);
            establecerBoton(addCampoOtros, listaPesosOtros, linearOtros);
        }

        establecerBotonBoletas(view);
        return view;
    }

    private void obtenerDatos() {
        Bundle bundle = getArguments();
        //Establece el nombre del cliente seleccionado
        if (bundle != null) {
            idCliente = bundle.getString("ID_CLIENTE");
            nombre = bundle.getString("NOMBRE_CLIENTE");
/*            pollo = bundle.getBoolean("POLLO");
            gdoble = bundle.getBoolean("DOBLE");
            groja = bundle.getBoolean("ROJA");
            gnegra = bundle.getBoolean("NEGRA");
            pato = bundle.getBoolean("PATO");
            pavo = bundle.getBoolean("PAVO");
            pechoe = bundle.getBoolean("PECHOE");
            piernae = bundle.getBoolean("PIERNAE");
            espinazo = bundle.getBoolean("ESPINAZO");
            menudencia = bundle.getBoolean("MENUDENCIA");*/
            nombreCliente.setText(nombre);
        }
        datosMenuClientes = datosMenu.getDatosMenu();
        if (datosMenuClientes != null && !datosMenuClientes.isEmpty()) {
            for (DatosMenuCliente menu : datosMenuClientes) {
                if (menu.getNombre().equalsIgnoreCase(nombre))
                    cliente = menu;
            }
        }
        if (cliente != null) {
            pollo = cliente.isPollo();
            gdoble = cliente.isGdoble();
            groja = cliente.isGroja();
            gnegra = cliente.isGnegra();
            pato = cliente.isPato();
            pavo = cliente.isPavo();
            piernae = cliente.isPiernae();
            pechoe = cliente.isPechoe();
            espinazo = cliente.isEspinazo();
            menudencia = cliente.isMenudencia();
            ala = cliente.isAla();
            otros= cliente.isOtros();
        }
    }

    private void establecerBotonBoletas(View view) {
        boletas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boletas.setEnabled(false);
                ocultarTecladoDe(view);
                if (pollo) {
                    validarLista(listaPesosPollos);
                    listaIdProductos.add(1);
                    if (!etPrePollo.getText().toString().isEmpty())
                        precioPollo = Float.parseFloat(etPrePollo.getText().toString());
                    if (!etCantiPollo.getText().toString().isEmpty())
                        cantiPollo = Integer.parseInt(etCantiPollo.getText().toString());
                    procesaBoleta.setListaPesosPollos(listaPesosPollos);
                    procesaBoleta.setPrecioPollo(precioPollo);
                    procesaBoleta.setCantiPollo(cantiPollo);
                }
                if (gdoble) {
                    validarLista(listaPesosGDobles);
                    listaIdProductos.add(2);
                    if (!etPreGdoble.getText().toString().isEmpty())
                        precioGdoble = Float.parseFloat(etPreGdoble.getText().toString());
                    if (!etCantiGdoble.getText().toString().isEmpty())
                        cantiGdoble = Integer.parseInt(etCantiGdoble.getText().toString());
                    procesaBoleta.setListaPesosGDobles(listaPesosGDobles);
                    procesaBoleta.setPrecioGdoble(precioGdoble);
                    procesaBoleta.setCantiGdoble(cantiGdoble);
                }
                if (groja) {
                    validarLista(listaPesosGRojas);
                    listaIdProductos.add(3);
                    if (!etPreGroja.getText().toString().isEmpty())
                        precioGroja = Float.parseFloat(etPreGroja.getText().toString());
                    if (!etCantiGroja.getText().toString().isEmpty())
                        cantidGroja = Integer.parseInt(etCantiGroja.getText().toString());
                    procesaBoleta.setListaPesosGRojas(listaPesosGRojas);
                    procesaBoleta.setPrecioGroja(precioGroja);
                    procesaBoleta.setCantidGroja(cantidGroja);
                }
                if (gnegra) {
                    validarLista(listaPesosGNegras);
                    listaIdProductos.add(4);
                    if (!etPreGnegra.getText().toString().isEmpty())
                        precioGnegra = Float.parseFloat(etPreGnegra.getText().toString());
                    if (!etCantiGnegra.getText().toString().isEmpty())
                        cantiGnegra = Integer.parseInt(etCantiGnegra.getText().toString());
                    procesaBoleta.setListaPesosGNegras(listaPesosGNegras);
                    procesaBoleta.setPrecioGnegra(precioGnegra);
                    procesaBoleta.setCantiGnegra(cantiGnegra);
                }
                if (pato) {
                    validarLista(listaPesosPatos);
                    listaIdProductos.add(5);
                    if (!etPrePato.getText().toString().isEmpty())
                        precioPato = Float.parseFloat(etPrePato.getText().toString());
                    if (!etCantiPato.getText().toString().isEmpty())
                        cantiPato = Integer.parseInt(etCantiPato.getText().toString());
                    procesaBoleta.setListaPesosPatos(listaPesosPatos);
                    procesaBoleta.setPrecioPato(precioPato);
                    procesaBoleta.setCantiPato(cantiPato);
                }
                if (pavo) {
                    validarLista(listaPesosPavos);
                    listaIdProductos.add(6);
                    if (!etPrePavo.getText().toString().isEmpty())
                        precioPavo = Float.parseFloat(etPrePavo.getText().toString());
                    if (!etCantiPavo.getText().toString().isEmpty())
                        cantiPavo = Integer.parseInt(etCantiPavo.getText().toString());
                    procesaBoleta.setListaPesosPavos(listaPesosPavos);
                    procesaBoleta.setPrecioPavo(precioPavo);
                    procesaBoleta.setCantiPavo(cantiPavo);
                }
                if (pechoe) {
                    validarLista(listaPesosPechoE);
                    listaIdProductos.add(7);
                    if (!etPrePechoE.getText().toString().isEmpty())
                        precioPechoE = Float.parseFloat(etPrePechoE.getText().toString());
                    if (!etCantiPechoE.getText().toString().isEmpty())
                        cantiPechoE = Integer.parseInt(etCantiPechoE.getText().toString());
                    procesaBoleta.setListaPesosPechoE(listaPesosPechoE);
                    procesaBoleta.setPrecioPechoE(precioPechoE);
                    procesaBoleta.setCantiPechoE(cantiPechoE);
                }
                if (piernae) {
                    validarLista(listaPesosPiernaE);
                    listaIdProductos.add(8);
                    if (!etPrePiernaE.getText().toString().isEmpty())
                        precioPiernaE = Float.parseFloat(etPrePiernaE.getText().toString());
                    if (!etCantiPiernaE.getText().toString().isEmpty())
                        cantiPiernaE = Integer.parseInt(etCantiPiernaE.getText().toString());
                    procesaBoleta.setListaPesosPiernaE(listaPesosPiernaE);
                    procesaBoleta.setPrecioPiernaE(precioPiernaE);
                    procesaBoleta.setCantiPiernaE(cantiPiernaE);
                }
                if (espinazo) {
                    validarLista(listaPesosEspinazo);
                    listaIdProductos.add(9);
                    if (!etPreEspinazo.getText().toString().isEmpty())
                        precioEspinazo = Float.parseFloat(etPreEspinazo.getText().toString());
                    if (!etCantiEspinazo.getText().toString().isEmpty())
                        cantiEspinazo = Integer.parseInt(etCantiEspinazo.getText().toString());
                    procesaBoleta.setListaPesosEspinazo(listaPesosEspinazo);
                    procesaBoleta.setPrecioEspinazo(precioEspinazo);
                    procesaBoleta.setCantiEspinazo(cantiEspinazo);
                }
                if (menudencia) {
                    validarLista(listaPesosMenudencia);
                    listaIdProductos.add(10);
                    if (!etPreMenudencia.getText().toString().isEmpty())
                        precioMenudencia = Float.parseFloat(etPreMenudencia.getText().toString());
                    if (!etCantiMenudencia.getText().toString().isEmpty())
                        cantiMenudencia = Integer.parseInt(etCantiMenudencia.getText().toString());
                    procesaBoleta.setListaPesosMenudencia(listaPesosMenudencia);
                    procesaBoleta.setPrecioMenudencia(precioMenudencia);
                    procesaBoleta.setCantiMenudencia(cantiMenudencia);
                }
                if (ala) {
                    validarLista(listaPesosAla);
                    listaIdProductos.add(11);
                    if (!etPreAla.getText().toString().isEmpty())
                        precioAla = Float.parseFloat(etPreAla.getText().toString());
                    if (!etCantiAla.getText().toString().isEmpty())
                        cantidadAla = Integer.parseInt(etCantiAla.getText().toString());
                    procesaBoleta.setListaPesosAla(listaPesosAla);
                    procesaBoleta.setPrecioAla(precioAla);
                    procesaBoleta.setCantiAla(cantidadAla);
                }
                if (otros) {
                    validarLista(listaPesosOtros);
                    listaIdProductos.add(12);
                    if (!etPreOtros.getText().toString().isEmpty())
                        precioOtros = Float.parseFloat(etPreOtros.getText().toString());
                    if (!etCantiOtros.getText().toString().isEmpty())
                        cantidadOtros = Integer.parseInt(etCantiOtros.getText().toString());
                    procesaBoleta.setListaPesosOtros(listaPesosOtros);
                    procesaBoleta.setPrecioOtros(precioOtros);
                    procesaBoleta.setCantiOtros(cantidadOtros);
                }

                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ID_CLIENTE", idCliente);
                bundle1.putString("NOMBRE_CLIENTE", nombre);
                bundle1.putIntegerArrayList("LISTA_ID_PRODUCTO", listaIdProductos);
                procesaBoleta.setArguments(bundle1);
                fragmentManager.beginTransaction().replace(R.id.Contenedor, procesaBoleta).addToBackStack("Inicio").commit();
            }
        });
    }

    void validarLista(List<EditText> lista) {
        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getText().toString().isEmpty()) {
                    lista.get(i).setText("0");
                }
            }
        }
    }

    public static void ocultarTecladoDe(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void establecerBoton(ImageButton Boton, final List<EditText> lista, final LinearLayout linear) {
        Boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orden = lista.size() + 1;
                EditText t = new EditText(getContext());
                //Establece el largo y alto
                t.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                //Lo hace solo decimal
                t.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                t.setHint("Ingrese peso n° " + orden);
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textoCampo));
                t.setMaxLines(1);
                t.setPadding(0, 10, 0, 10);
                t.setGravity(Gravity.CENTER);

                linear.addView(t);
                lista.add(t);
            }
        });
    }


    private void capturarElementos(View view) {

        if (pollo) {
            // Captura los elementos de las vistas
            txtTextViewPollos = view.findViewById(R.id.textViewPollos);
            linearPollos = view.findViewById(R.id.linearPollos);
            etPesoPollo = view.findViewById(R.id.editTextPollo);
            etPrePollo = view.findViewById(R.id.editTextPolloPrecio);
            etCantiPollo = view.findViewById(R.id.editTextPolloCantidad);
            addCampoPollo = view.findViewById(R.id.addCampoPollo);
        }
        if (gdoble) {
            textViewGDoble = view.findViewById(R.id.textViewGDoble);
            linearGDoble = view.findViewById(R.id.linearGDoble);
            etPesoGdoble = view.findViewById(R.id.editTextGDoble);
            etPreGdoble = view.findViewById(R.id.editTextGdoblePrecio);
            etCantiGdoble = view.findViewById(R.id.editTextGdobleCantidad);
            addCampoGDoble = view.findViewById(R.id.addCampoGDoble);
        }
        if (groja) {
            textViewGRoja = view.findViewById(R.id.textViewGRoja);
            linearGRoja = view.findViewById(R.id.linearGRoja);
            etPesoGroja = view.findViewById(R.id.editTextGRoja);
            etPreGroja = view.findViewById(R.id.editTextGrojaPrecio);
            etCantiGroja = view.findViewById(R.id.editTextGrojaCantidad);
            addCampoGRoja = view.findViewById(R.id.addCampoGRoja);
        }
        if (gnegra) {
            textViewGNegra = view.findViewById(R.id.textViewGNegra);
            linearGNegra = view.findViewById(R.id.linearGNegra);
            etPesoGnegra = view.findViewById(R.id.editTextGNegra);
            etPreGnegra = view.findViewById(R.id.editTextGnegraPrecio);
            etCantiGnegra = view.findViewById(R.id.editTextGnegraCantidad);
            addCampoGNegra = view.findViewById(R.id.addCampoGNegra);
        }
        if (pato) {
            textViewPato = view.findViewById(R.id.textViewPato);
            linearPato = view.findViewById(R.id.linearPato);
            etPesoPato = view.findViewById(R.id.editTextPato);
            etPrePato = view.findViewById(R.id.editTextPatoPrecio);
            etCantiPato = view.findViewById(R.id.editTextPatoCantidad);
            addCampoPato = view.findViewById(R.id.addCampoPato);
        }
        if (pavo) {
            textViewPavo = view.findViewById(R.id.textViewPavo);
            linearPavo = view.findViewById(R.id.linearPavo);
            etPesoPavo = view.findViewById(R.id.editTextPavo);
            etPrePavo = view.findViewById(R.id.editTextPavoPrecio);
            etCantiPavo = view.findViewById(R.id.editTextPavoCantidad);
            addCampoPavo = view.findViewById(R.id.addCampoPavo);
        }
        if (pechoe) {
            textViewPechoE = view.findViewById(R.id.textViewPechoE);
            linearPechoE = view.findViewById(R.id.linearPechoE);
            etPesoPechoE = view.findViewById(R.id.editTextPechoE);
            etPrePechoE = view.findViewById(R.id.editTextPechoEPrecio);
            etCantiPechoE = view.findViewById(R.id.editTextPechoECantidad);
            addCampoPechoE = view.findViewById(R.id.addCampoPechoE);
        }
        if (piernae) {
            textViewPiernaE = view.findViewById(R.id.textViewPiernaE);
            linearPiernaE = view.findViewById(R.id.linearPiernaE);
            etPesoPiernaE = view.findViewById(R.id.editTextPiernaE);
            etPrePiernaE = view.findViewById(R.id.editTextPiernaEPrecio);
            etCantiPiernaE = view.findViewById(R.id.editTextPiernaECantidad);
            addCampoPiernaE = view.findViewById(R.id.addCampoPiernaE);
        }
        if (espinazo) {
            textViewEspinazo = view.findViewById(R.id.textViewEspinazo);
            linearEspinazo = view.findViewById(R.id.linearEspinazo);
            etPesoEspinazo = view.findViewById(R.id.editTextEspinazo);
            etPreEspinazo = view.findViewById(R.id.editTextEspinazoPrecio);
            etCantiEspinazo = view.findViewById(R.id.editTextEspinazoCantidad);
            addCampoEspinazo = view.findViewById(R.id.addCampoEspinazo);
        }
        if (menudencia) {
            textViewMenudencia = view.findViewById(R.id.textViewMenudencia);
            linearMenudencia = view.findViewById(R.id.linearMenudencia);
            etPesoMenudencia = view.findViewById(R.id.editTextMenudencia);
            etPreMenudencia = view.findViewById(R.id.editTextMenudenciaPrecio);
            etCantiMenudencia = view.findViewById(R.id.editTextMenudenciaCantidad);
            addCampoMenudencia = view.findViewById(R.id.addCampoMenudencia);
        }
        if (ala) {
            textViewAla = view.findViewById(R.id.textViewAla);
            linearAla = view.findViewById(R.id.linearAla);
            etPesoAla = view.findViewById(R.id.editTextAla);
            etPreAla = view.findViewById(R.id.editTextAlaPrecio);
            etCantiAla = view.findViewById(R.id.editTextAlaCantidad);
            addCampoAla = view.findViewById(R.id.addCampoAla);
        }
        if (otros) {
            textViewOtros = view.findViewById(R.id.textViewOtros);
            linearOtros = view.findViewById(R.id.linearOtros);
            etPesoOtros = view.findViewById(R.id.editTextOtros);
            etPreOtros = view.findViewById(R.id.editTextOtrosPrecio);
            etCantiOtros = view.findViewById(R.id.editTextOtrosCantidad);
            addCampoOtros = view.findViewById(R.id.addCampoOtros);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.opcionesCliente) {
            ocultarTecladoDe(getView());
            //System.out.println("opcion presionada");
            FragmentManager fragmentManager = getFragmentManager();
            ClienteMenu clienteMenu = new ClienteMenu();
            Bundle bundle = new Bundle();
            bundle.putString("ID_CLIENTE", idCliente);
            bundle.putString("NOMBRE_CLIENTE", nombre);
            clienteMenu.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(new DetalleCliente(), "DetalleCliente")
                    .addToBackStack("DetalleCliente")
                    .replace(R.id.Contenedor, clienteMenu)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }
}
