//---------------------Centro de Opereciones---------------------//
package Logica;

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
    
    
    
    
    
    
    
}
