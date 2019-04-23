package namelessbliss.tunquisolutions.Modelo;

public class Boleta {
    private  int icono;
    private int idBoleta;
    private int idUsuario;
    private int idCliente;
    private int idProducto;
    private String fecha;
    private String producto;
    private int cantidad;
    private float pesoNeto;
    private float precio;
    private float total;
    private float subtotal;

    public Boleta() {
    }

    public Boleta(String producto, int cantidad, float pesoNeto, float precio, float total) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.pesoNeto = pesoNeto;
        this.precio = precio;
        this.total = total;
    }

    public Boleta(int idUsuario, int idCliente, int idProducto, String fecha, String producto, int cantidad, float pesoNeto, float precio, float total) {
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.fecha = fecha;
        this.producto = producto;
        this.cantidad = cantidad;
        this.pesoNeto = pesoNeto;
        this.precio = precio;
        this.total = total;
    }

    //constructor de la lista boleta
    public Boleta(int icono,int idBoleta, int idUsuario, String fecha, int idCliente, String producto, int cantidad, float pesoNeto, float precio) {
        this.icono = icono;
        this.idBoleta = idBoleta;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.producto = producto;
        this.cantidad = cantidad;
        this.pesoNeto = pesoNeto;
        this.precio = precio;
    }

    public Boleta(int icono, int idBoleta, int idUsuario, int idCliente, String fecha, float subtotal) {
        this.icono = icono;
        this.idBoleta = idBoleta;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.subtotal = subtotal;
    }



    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public int getIdBoleta() {
        return idBoleta;
    }

    public void setIdBoleta(int idBoleta) {
        this.idBoleta = idBoleta;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPesoNeto() {
        return pesoNeto;
    }

    public void setPesoNeto(float pesoNeto) {
        this.pesoNeto = pesoNeto;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
