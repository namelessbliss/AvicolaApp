package namelessbliss.tunquisolutions.Modelo;

public class DatosMenuCliente {
    /**
     * Datos de estado de las opciones de venta de los clientes
     */

    private String nombre;
    private boolean pollo;
    private boolean gdoble;
    private boolean gnegra;
    private boolean groja;
    private boolean pato;
    private boolean pavo;
    private boolean piernae;
    private boolean pechoe;
    private boolean espinazo;
    private boolean menudencia;
    private boolean ala;
    private boolean otros;
    private double mermaPollo;
    private double mermaGdoble;
    private double mermaGnegra;
    private double mermaGroja;
    private double mermaPato;
    private double mermaPavo;
    private double mermaPiernae;
    private double mermaPechoe;
    private double mermaEspinazo;
    private double mermaMenudencia;
    private double mermaAla;
    private double mermaOtros;

    public DatosMenuCliente(String nombre, boolean pollo, boolean gdoble, boolean gnegra,
                            boolean groja, boolean pato, boolean pavo, boolean piernae,
                            boolean pechoe, boolean espinazo, boolean menudencia,
                            boolean ala, boolean otros,
                            double mermaPollo, double mermaGdoble, double mermaGnegra,
                            double mermaGroja, double mermaPato, double mermaPavo,
                            double mermaPiernae, double mermaPechoe, double mermaEspinazo,
                            double mermaMenudencia, double mermaAla, double mermaOtros) {
        this.nombre = nombre;
        this.pollo = pollo;
        this.gdoble = gdoble;
        this.gnegra = gnegra;
        this.groja = groja;
        this.pato = pato;
        this.pavo = pavo;
        this.piernae = piernae;
        this.pechoe = pechoe;
        this.espinazo = espinazo;
        this.menudencia = menudencia;
        this.ala = ala;
        this.otros = otros;
        this.mermaPollo = mermaPollo;
        this.mermaGdoble = mermaGdoble;
        this.mermaGnegra = mermaGnegra;
        this.mermaGroja = mermaGroja;
        this.mermaPato = mermaPato;
        this.mermaPavo = mermaPavo;
        this.mermaPiernae = mermaPiernae;
        this.mermaPechoe = mermaPechoe;
        this.mermaEspinazo = mermaEspinazo;
        this.mermaMenudencia = mermaMenudencia;
        this.mermaAla = mermaAla;
        this.mermaOtros = mermaOtros;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPollo() {
        return pollo;
    }

    public void setPollo(boolean pollo) {
        this.pollo = pollo;
    }

    public boolean isGdoble() {
        return gdoble;
    }

    public void setGdoble(boolean gdoble) {
        this.gdoble = gdoble;
    }

    public boolean isGnegra() {
        return gnegra;
    }

    public void setGnegra(boolean gnegra) {
        this.gnegra = gnegra;
    }

    public boolean isGroja() {
        return groja;
    }

    public void setGroja(boolean groja) {
        this.groja = groja;
    }

    public boolean isPato() {
        return pato;
    }

    public void setPato(boolean pato) {
        this.pato = pato;
    }

    public boolean isPavo() {
        return pavo;
    }

    public void setPavo(boolean pavo) {
        this.pavo = pavo;
    }

    public boolean isPiernae() {
        return piernae;
    }

    public void setPiernae(boolean piernae) {
        this.piernae = piernae;
    }

    public boolean isPechoe() {
        return pechoe;
    }

    public void setPechoe(boolean pechoe) {
        this.pechoe = pechoe;
    }

    public boolean isEspinazo() {
        return espinazo;
    }

    public void setEspinazo(boolean espinazo) {
        this.espinazo = espinazo;
    }

    public boolean isMenudencia() {
        return menudencia;
    }

    public void setMenudencia(boolean menudencia) {
        this.menudencia = menudencia;
    }

    public double getMermaPollo() {
        return mermaPollo;
    }

    public void setMermaPollo(double mermaPollo) {
        this.mermaPollo = mermaPollo;
    }

    public double getMermaGdoble() {
        return mermaGdoble;
    }

    public void setMermaGdoble(double mermaGdoble) {
        this.mermaGdoble = mermaGdoble;
    }

    public double getMermaGnegra() {
        return mermaGnegra;
    }

    public void setMermaGnegra(double mermaGnegra) {
        this.mermaGnegra = mermaGnegra;
    }

    public double getMermaGroja() {
        return mermaGroja;
    }

    public void setMermaGroja(double mermaGroja) {
        this.mermaGroja = mermaGroja;
    }

    public double getMermaPato() {
        return mermaPato;
    }

    public void setMermaPato(double mermaPato) {
        this.mermaPato = mermaPato;
    }

    public double getMermaPavo() {
        return mermaPavo;
    }

    public void setMermaPavo(double mermaPavo) {
        this.mermaPavo = mermaPavo;
    }

    public double getMermaPiernae() {
        return mermaPiernae;
    }

    public void setMermaPiernae(double mermaPiernae) {
        this.mermaPiernae = mermaPiernae;
    }

    public double getMermaPechoe() {
        return mermaPechoe;
    }

    public void setMermaPechoe(double mermaPechoe) {
        this.mermaPechoe = mermaPechoe;
    }

    public double getMermaEspinazo() {
        return mermaEspinazo;
    }

    public void setMermaEspinazo(double mermaEspinazo) {
        this.mermaEspinazo = mermaEspinazo;
    }

    public double getMermaMenudencia() {
        return mermaMenudencia;
    }

    public void setMermaMenudencia(double mermaMenudencia) {
        this.mermaMenudencia = mermaMenudencia;
    }

    public boolean isAla() {
        return ala;
    }

    public void setAla(boolean ala) {
        this.ala = ala;
    }

    public boolean isOtros() {
        return otros;
    }

    public void setOtros(boolean otros) {
        this.otros = otros;
    }

    public double getMermaAla() {
        return mermaAla;
    }

    public void setMermaAla(double mermaAla) {
        this.mermaAla = mermaAla;
    }

    public double getMermaOtros() {
        return mermaOtros;
    }

    public void setMermaOtros(double mermaOtros) {
        this.mermaOtros = mermaOtros;
    }
}
