package Datos;


public class Clientes {
    private int Codigo;
    private String Nombre;
    private int Telefono;
    private String email;
    
    
    public Clientes(int Codigo, String Nombre, int Telefono, String email){
        this.Codigo=Codigo;
        this.Nombre=Nombre;
        this.Telefono=Telefono;
        this.email=email;
    }

    public void setCodigo(int Codigo) {
        this.Codigo = Codigo;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public void setTelefono(int Telefono) {
        this.Telefono = Telefono;
    }

    public void setCorreo(String email) {
        this.email = email;
    }

    public int getCodigo() {
        return Codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public int getTelefono() {
        return Telefono;
    }

    public String getemail() {
        return email;
    }
    
    public void registrar(){
            
    
    
    
    }
      
    
}