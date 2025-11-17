package project;

import javax.swing.SwingUtilities;
import project.gui.MainFrame;

/**
 * Application entry point for the Media Manager.
 * <p>
 * This class contains the {@code main} method which initializes the Swing
 * look-and-feel and launches the primary {@link project.gui.MainFrame} GUI.
 */
public class MainApp {
    
    /**
     * Main entry point.
     * <p>
     * This method starts the Swing event dispatch thread and shows the
     * application's main window.
     *
     * @param args runtime arguments (ignored)
     */
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
