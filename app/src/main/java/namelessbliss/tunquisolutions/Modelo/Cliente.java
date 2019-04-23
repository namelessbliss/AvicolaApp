package namelessbliss.tunquisolutions.Modelo;

public class Cliente {
    private String idCliente;
    private String nombre;
    private String pago;
    private String saldo;
    private String fecha;
    private int icono;

    public Cliente(String idCliente,String nombre, int icono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.icono = icono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcon(int icon) {
        this.icono = icon;
    }
}
