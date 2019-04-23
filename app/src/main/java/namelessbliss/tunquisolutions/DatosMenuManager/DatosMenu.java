package namelessbliss.tunquisolutions.DatosMenuManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import namelessbliss.tunquisolutions.Modelo.DatosMenuCliente;

public class DatosMenu {

    //Shared Preferences reference
    SharedPreferences sharedPreferences;

    //Editor reference for shared preference
    SharedPreferences.Editor editor;

    //Contexto
    Context context;

    Gson gson;

    ArrayList<DatosMenuCliente> ary;

    // Shared preferences file name
    private static final String NOMBRE_ARCHIVO = "DatosMenu";

    // All Shared Preferences  Keys
    public static final String JSON_DATOS = "datosMenu";

    // constructor
    public DatosMenu(Context _context) {
        this.context = _context;
        sharedPreferences = context.getSharedPreferences(NOMBRE_ARCHIVO, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson= new Gson();
        ary = new ArrayList<>();
    }

    //Create login session
    public void setDatosMenu(ArrayList<DatosMenuCliente> arrayMenu) {

        String json = gson.toJson(arrayMenu);
        // Storing
        editor.putString(JSON_DATOS, json);

        //commit changes
        editor.commit();
    }

    /**
     * Get stored data
     */
    public ArrayList<DatosMenuCliente> getDatosMenu() {
        String json = sharedPreferences.getString(JSON_DATOS,null);
        Type type = new TypeToken<ArrayList<DatosMenuCliente>>(){}.getType();
        ary = gson.fromJson(json,type);
        return ary;
    }

    /**
     * Clear  details
     */

    public void eliminarDatosMenu() {
        //Clearing all user data from shared preferences
        editor.clear();
        editor.commit();
    }
}
