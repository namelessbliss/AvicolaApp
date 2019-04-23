package namelessbliss.tunquisolutions.Modelo.Procesos;

import java.util.Random;

public class Consulta {

    public String getNombreBoleta(){
        final int min = 0;
        final int max = 100;
        final int random = new Random().nextInt((max - min) + 1) + min;
        String nombre = "Boleta";
        return nombre+random;
    }
}
