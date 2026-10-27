package Logica;

//---------------------Centro de Opereciones---------------------//


import Datos.Clientes;
import Grafica.Login;
import Grafica.Grafica;
import javax.swing.JOptionPane;




public class Cerebro {
    
    public void login(String usuario, String clave, Login ventanaLogin){
        final String Usuario="J.Manuel";
        final String Contraseña="JuanMa01";
        
        if(usuario.equals(Usuario)){
           if(clave.equals(Contraseña)){
               Grafica v2 = new Grafica();
               v2.setLocationRelativeTo(null);
               v2.setVisible(true);
              
               ventanaLogin.dispose();
           }else{JOptionPane.showMessageDialog(null, "Contraseña Incorrecta");}
        }else{JOptionPane.showMessageDialog(null,"Usuiario Incorrecto");}
      
    }
    
    private Clientes[] clientes=new Clientes[1000];
    private int cantidadClientes = 0;
    
    public void registrarCliente(String nombre, String telefono, String email){
        if(cantidadClientes==clientes.length){
            Clientes[]nuevosClientes= new Clientes[clientes.length+100];
        }
        
    }
    
    
    
    
    
    
}
