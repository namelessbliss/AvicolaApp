package namelessbliss.tunquisolutions.DatosEmpresaManager;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

public class Empresa {

    //Shared Preferences reference
    SharedPreferences sharedPreferences;

    //Editor reference for shared preference
    SharedPreferences.Editor editor;

    //Contexto
    Context context;

    // Shared preferences file name
    private static final String NOMBRE_ARCHIVO = "DatosEmpresa";

    // All Shared Preferences  Keys
    public static final String KEY_NOMBRE = "nombreEmpresa";
    public static final String KEY_DIRECCION = "direccionEmpresa";
    public static final String KEY_CELULAR = "celularEmpresa";
    public static final String KEY_LOGO = "logoEmpresa";

    // constructor
    public Empresa(Context _context) {
        this.context = _context;
        sharedPreferences = context.getSharedPreferences(NOMBRE_ARCHIVO, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //Create login session
    public void crearDatosEmpresa(String nombre, String direccion, String celular, String logo) {

        // Storing
        editor.putString(KEY_NOMBRE, nombre);

        editor.putString(KEY_DIRECCION, direccion);

        editor.putString(KEY_CELULAR, celular);

        editor.putString(KEY_LOGO, logo);

        //commit changes
        editor.commit();
    }

    /**
     * Get stored data
     */
    public HashMap<String, String> getDatosEmpresa() {

        // use hashmap to store user credentials
        HashMap<String, String> emp = new HashMap<>();

        emp.put(KEY_NOMBRE, sharedPreferences.getString(KEY_NOMBRE, null));

        emp.put(KEY_DIRECCION, sharedPreferences.getString(KEY_DIRECCION, null));

        emp.put(KEY_CELULAR, sharedPreferences.getString(KEY_CELULAR, null));

        emp.put(KEY_LOGO, sharedPreferences.getString(KEY_LOGO, null));

        return emp;
    }

    /**
     * Clear  details
     */

    public void eliminarDatosEmpresa() {
        //Clearing all user data from shared preferences
        editor.clear();
        editor.commit();
    }

}
