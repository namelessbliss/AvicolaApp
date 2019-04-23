package namelessbliss.tunquisolutions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import namelessbliss.tunquisolutions.SessionManager.UserSessionManager;

public class MainActivity extends AppCompatActivity {
    // Objetos de la vista capturadas con la lib butterknife
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.iniciarSesion)
    Button iniciarSesion;
    @BindView(R.id.mensaje)
    TextView mensaje;
    // cola para el http request
    RequestQueue queue;

    // User Session Manager Class
    UserSessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        // user session manager
        session = new UserSessionManager(getApplicationContext());

        // Check user login
        // If User is not logged in , This will redirect user to LoginActivity.
        if (session.isLogin()) {
            Entrar();
        }

        queue = Volley.newRequestQueue(this);
    }

    private void Entrar() {
        Intent intent = new Intent(getApplicationContext(), NavigationDrawer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.iniciarSesion)
    public void onViewClicked() {
        String user = String.valueOf(username.getText());
        String pass = String.valueOf(password.getText());

        iniciarSesion(user, pass);
    }

    public void iniciarSesion(final String user, String pass) {
        String url = "http://avicolas.skapir.com/usuario_existe.php/?username=" + user + "&password=" + pass;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String respuesta = jsonObject.getString("RESPUESTA");
                            if (respuesta.equalsIgnoreCase("si")) {
                                String idUsuario = jsonObject.getString("IDUSUARIO");
                                session.createUserLoginSession(idUsuario, user);
                                mensaje.setText("");
                                mensaje.setText("Iniciando sesion");
                                Intent intent = new Intent(getApplicationContext(), NavigationDrawer.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {
                                mensaje.setText("");
                                mensaje.setText("Usuario o contraseña invalido");
                            }
                        } catch (JSONException error) {
                            mensaje.setText("La respuesta no fue la esperada");
                            error.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensaje.setText("No hay conexion a internet");
                error.printStackTrace();
            }
        });
        queue.add(request);
    }


}
