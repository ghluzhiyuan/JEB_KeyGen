import keygen.MachineID;

import javax.swing.*;
import java.awt.*;

public class KeyGen {
    private JTextField textField1;
    private JPanel panel1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("JEB KeyGen for v2.3.6");
        KeyGen keyGen = new KeyGen();
        frame.setContentPane(keyGen.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(new Dimension(350,150));
        keyGen.textField1.setText(keygen.KeyGen.getKey(MachineID.get()[0], System.currentTimeMillis() / 1000));
    }
}
