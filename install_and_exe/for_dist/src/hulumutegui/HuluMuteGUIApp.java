/*
 * HuluMuteGUIApp.java
 */

package hulumutegui;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class HuluMuteGUIApp extends SingleFrameApplication {
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        hide(new HuluMuteGUIView(this));
        /*
        HuluMuteGUIView gui = new HuluMuteGUIView(this);
        show(gui);
        hide(gui);
        gui.initFocus();
        View view = getMainView();  //hides the program window on load
        hide(view);
        */
     }
   
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of HuluMuteGUIApp
     */
    public static HuluMuteGUIApp getApplication() {
        return Application.getInstance(HuluMuteGUIApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(HuluMuteGUIApp.class, args);
    }
}
