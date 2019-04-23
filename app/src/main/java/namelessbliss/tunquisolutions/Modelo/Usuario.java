package namelessbliss.tunquisolutions.Modelo;

public class Usuario {
    private int idusuario;
    private int idperfil;
    private String nombre;

    public Usuario(int idusuario, int idperfil, String nombre) {
        this.idusuario = idusuario;
        this.idperfil = idperfil;
        this.nombre = nombre;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public int getIdperfil() {
        return idperfil;
    }

    public void setIdperfil(int idperfil) {
        this.idperfil = idperfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
