package namelessbliss.tunquisolutions;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import namelessbliss.tunquisolutions.Fragments.Boletas;
import namelessbliss.tunquisolutions.Fragments.Clientes;
import namelessbliss.tunquisolutions.Fragments.Configuracion;
import namelessbliss.tunquisolutions.Fragments.EstadoCuenta;
import namelessbliss.tunquisolutions.Fragments.EstadoCuentaCliente;
import namelessbliss.tunquisolutions.Fragments.ModuloGestion;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Fragment Manager
    FragmentManager fragmentManager = getSupportFragmentManager();

    String idUsuario;
    Clientes clientes = new Clientes();

    // User Session Manager Class
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Session class instance
        session = new UserSessionManager(getApplicationContext());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toast.makeText(getApplicationContext(),
                "Bienvenido",
                Toast.LENGTH_LONG).show();

        // Check user login
        // If User is not logged in , This will redirect user to LoginActivity.
        if (session.isNotLogin())
            finish();

        //Carga el fragmen clientes al abrir
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idUsuario = bundle.getString("idUsuario");
            System.out.println("id ---- " + idUsuario);
        }
        Bundle bundle1 = new Bundle();
        bundle1.putString("idUsuario", idUsuario);
        clientes.setArguments(bundle1);
        fragmentManager.beginTransaction()
                .replace(R.id.Contenedor, clientes)
                .commit();
    }

    @Override
    public void onBackPressed() {
        /*int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.opcionesCliente) {
            System.out.println("click en drawer");
            return false;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // limpia el stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (id) {
            case R.id.nav_config:
                fragmentManager.beginTransaction()
                        .add(new Clientes(), "Inicio")
                        .addToBackStack("Inicio")
                        .replace(R.id.Contenedor, new Configuracion())
                        .commit();
                break;
            case R.id.nav_clientes:
                fragmentManager.beginTransaction()
                        .add(new Clientes(), "Inicio")
                        .addToBackStack("Inicio")
                        .replace(R.id.Contenedor, new Clientes())
                        .commit();
                break;
            case R.id.nav_boletas:
                fragmentManager.beginTransaction()
                        .add(new Clientes(), "Inicio")
                        .addToBackStack("Inicio")
                        .replace(R.id.Contenedor, new Boletas())
                        .commit();
                break;
            case R.id.nav_estadoCuenta:
                fragmentManager.beginTransaction()
                        .add(new Clientes(), "Inicio")
                        .addToBackStack("Inicio")
                        .replace(R.id.Contenedor, new EstadoCuentaCliente())
                        .commit();
                break;
            case R.id.nav_moduloGestion:
                fragmentManager.beginTransaction()
                        .add(new Clientes(), "Inicio")
                        .addToBackStack("Inicio")
                        .replace(R.id.Contenedor, new ModuloGestion())
                        .commit();
                break;
            case R.id.nav_cerrarSersion:
                // Clear the User session data
                // and redirect user to LoginActivity
                session.logoutUser();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
