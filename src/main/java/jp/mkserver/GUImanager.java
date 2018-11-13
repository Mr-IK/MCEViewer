package jp.mkserver;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class GUImanager extends JFrame {
    public GUImanager (){
        System.out.println("DEBUG: create gui frame start");
        setTitle("MCEViewer");
        setSize(400,100);
        setLocationRelativeTo(null);
        System.out.println("DEBUG: create gui frame 50%");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowClosing());
        URL url=getClass().getClassLoader().getResource("Icon.png");
        ImageIcon icon=new ImageIcon(url);
        setIconImage(icon.getImage());
        JPanel maingui = new MainPanel(this);
        getContentPane().add(maingui);
        setVisible(true);
        System.out.println("DEBUG: create gui frame end");
    }



    class WindowClosing extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            int ans = JOptionPane.showConfirmDialog(GUImanager.this, "本当に終了しますか?","終了確認",JOptionPane.YES_NO_OPTION);
            if(ans == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}
