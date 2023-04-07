

import javax.swing.*;

public class inscription extends JFrame {


    private JPanel panel1;

    public inscription (){

    add(panel1);
    setTitle("Inscription Udem");
    setSize(600, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);

}

    public static void main(String[] args) {
         inscription inscription= new inscription();
        inscription.setVisible(true);
    }

}






