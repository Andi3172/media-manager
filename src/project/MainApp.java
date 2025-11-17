package project;

import javax.swing.SwingUtilities;
import project.gui.MainFrame;

public class MainApp {
    
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try{
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e){
                e.printStackTrace();
            }
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

}
