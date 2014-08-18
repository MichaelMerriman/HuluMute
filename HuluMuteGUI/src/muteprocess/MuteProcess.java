package muteprocess;

import hulumutegui.HuluMuteGUIView;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class MuteProcess {

    private Robot robot = new Robot();
    private static JWindow screenSaverWindow, screenSaverWindowTop;
    private static JFrame screenSaverFrame, screenSaverFrameTop;
    private static String name="hulumute";
    private static double dVersion =0.90;
    private static boolean escapePressed, log, debug, blockVisuals, checkUpdate, desktopApp;
    private boolean active;
    private static int delay;
    private static int origResX, origResY;
    private static String muteCommand, unmuteCommand;
    //private static String selectWindow = "cmdow.exe \"Blocking Ads\" /ACT";
    private final static String[][] aDefaults = {
        {"desktopApp","log","debug","checkUpdate","delay","muteCommand","unmuteCommand","blockVisuals"},
        {"false","true","false","true","150","nircmdc.exe mutesysvolume 1","nircmdc.exe mutesysvolume 0","true"}
    };
    
    public MuteProcess() throws Exception {
        System.out.println(name+".exe");
        System.out.println("This program can be configured within "+name+".conf");
        System.out.println();
        active = false;
        log = false;
        if (!isConfig(name)) {//check for config file
            newConfig(name,aDefaults[1]);
        }
        log = Boolean.parseBoolean(readConfig(name,"log"));
        resetLog(name);
        desktopApp = Boolean.parseBoolean(readConfig(name,"desktopApp"));
        origResX = Toolkit.getDefaultToolkit().getScreenSize().width;
        origResY = Toolkit.getDefaultToolkit().getScreenSize().height;
        writeLog(name,"Screen resolution is "+origResX+"x"+origResY);
        try {
            if (desktopApp && !supportedRes()) {
                HuluMuteGUIView.showResError();
            }
        } catch (IllegalStateException ise) {
            HuluMuteGUIView.showResError();
        }
        checkUpdate = Boolean.parseBoolean(readConfig(name,"checkUpdate"));
        try {
            if (checkUpdate && isNewVersion(dVersion)) {
                HuluMuteGUIView.promptUpdate();
            }  
        } catch (IOException ioe) {
            writeLog(name,ioe.getMessage());
        }             
        //<editor-fold defaultstate="collapsed" desc="Already running?">
        if (totalRunning(name+".exe")>1) {
            final JPanel panel = new JPanel();      //display warning for already running process
            
            JOptionPane.showMessageDialog(panel,
                    "Hulumute is already running.  This process will now exit.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            writeLog(name,"Process already running: "+name);
            System.exit(0);
        }
        //</editor-fold>
        debug = Boolean.parseBoolean(readConfig(name,"debug"));
        muteCommand = readConfig(name,"muteCommand");
        unmuteCommand = readConfig(name,"unmuteCommand");
        blockVisuals = Boolean.parseBoolean(readConfig(name,"blockVisuals"));
        try {
           delay = Integer.parseInt(readConfig(name,"delay")); 
        } catch (Exception ex) {
            writeLog(name,"Could not parse delay var from config ("+ex.getMessage()+"): setting to default");
            delay = 100;
        }
    }

    public void doMuteProcess() {
        Thread thread = new Thread() {
            @Override
            public void run () {
                try {
                    escapePressed = false;
                    boolean firstPass = true;
                    boolean doWait = true;
                    //resChanged(screenWidth, screenHeight);
                    if (blockVisuals) {
                        createScreensaver();
                    }
                    writeLog(name,"Mute process is active.");
                    boolean isMuted = false;
                    boolean isCommercial = false;
                    Runtime.getRuntime().exec(getUnmuteCommand());
                    while (active) {
                        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
                        if (escapePressed) {
                            active = false;
                        }
                        if (resChanged(screenWidth, screenHeight)){
                            createScreensaver();
                            origResX = screenWidth;
                            origResY = screenHeight;
                        }
                        if (desktopApp) {
                            isCommercial = isDesktopAppCommercial();
                        } else {
                            isCommercial = isCommercial();
                        }
                        if (isCommercial) {
                            firstPass = true;
                        }
                        if (!isMuted && isCommercial) {
                            if (isDebug()) {writeLog(name, "Muted. ");}
                            doMuteCMD();
                            if (isBlockVisuals()) {
                                //robot.mouseMove(Toolkit.getDefaultToolkit().getScreenSize().width, 0); //not needed with blank cursor
                                setScreensaverVisible(true);
                            }
                            escapePressed = false;
                            isMuted = true;
                        } else if (isMuted && (!isCommercial||escapePressed)) {  //if no commercial or esc key pressed,
                            if (desktopApp && firstPass && !escapePressed) {                  //unmute and/or hide visual block
                                //if (desktopApp) {
                                    sleep(3000); //used to make sure there isn't another commercial coming up
                                  //} else {       //controls fade outs as well
                                    //sleep(900);
                               // }
                                firstPass = false;
                                doWait = false;
                            } else {
                                if (isDebug()) {writeLog(name, "Unmuted. ");}
                                doWait = false;
                                doUnmuteCMD();
                                if (isBlockVisuals()) {
                                    setScreensaverVisible(false);
                                }
                                isMuted = false;
                                firstPass = true;
                                if (escapePressed) {
                                    escapePressed = false;
                                    active = false;
                                }
                            }
                        }
                        if (doWait) {  //skips wait on first pass
                            sleep(getDelay());
                        } else {
                            doWait = true;
                        }
                    }
                    writeLog(name, "Mute process has stopped");
                } catch (IllegalStateException ex) {
                    writeLog(name,"The screen resolution is unsupported for hulu desktop muting");
                    HuluMuteGUIView.showResError();
                    active = false;
                    System.out.println(ex.getMessage());
                    writeLog(name,"Error occured during mute process: "+ex.getMessage());
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    writeLog(name,"Error occured during mute process: "+ex.getMessage());
                }
            }
	};
	if (active) {            
            thread.setDaemon(true);
            thread.start();    
        }
    }
    private boolean resChanged(int x, int y) {
        boolean b = (x == origResX && y == origResY);
        if (!b) {
            writeLog(name,"The screen resolution has changed from "
                    +origResX+"x"+origResY+" to "+x+"x"+y);
        }
        return !b;
    }
    private Point[] getPoints()  throws IllegalStateException {
        Point[] p1366x768 = //<editor-fold defaultstate="collapsed" desc="comment">
        {
            new Point(32,45),
            new Point(32,46),
            new Point(33,48),
            new Point(70,42),
            new Point(70,43),
            new Point(70,44),
            new Point(70,45),
            new Point(70,46),
            new Point(70,47),
            new Point(70,48),
            new Point(86,45),
            new Point(86,46),
            new Point(86,47),
            new Point(95,44),
            new Point(95,45),
            new Point(95,46),
            new Point(95,47),
            new Point(95,48),
            new Point(100,45),
            new Point(100,46),
            new Point(100,47),
            new Point(100,48),
            new Point(105,45),
            new Point(105,46),
            new Point(105,47),
            new Point(105,48),
            new Point(109,45),
            new Point(109,46),
            new Point(109,47),
        };
        //</editor-fold>
        Point[] p = null;
        if (desktopApp) {
            switch (origResX) //<editor-fold defaultstate="collapsed" desc="comment">
        {   case 800:
                if (origResY == 600) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                            new Point(44, 25),
                            new Point(44, 26),
                            new Point(44, 27),
                            new Point(44, 28),
                            new Point(44, 29)};
                    //</editor-fold>
                }
                break;
            case 1024:
                if (origResY == 768) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[] {//new Point(25, 35),
                        new Point(32, 33),
                        new Point(32, 36),
                        new Point(96, 32)}; //need more points
                    //</editor-fold>
                }
                break;
            case 1152:
                if (origResY==648||origResY==864){
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(31,37),
                        new Point(31,39),
                        new Point(39,36),
                        new Point(59,35),
                        new Point(66,40),
                        //new Point(68,38),
                        //new Point(77,37),
                        //new Point(104,36),
                        //new Point(108,35),
                    };
                    //</editor-fold>
                }
                break;
            case 1280:
                if (origResY==720||origResY==800||origResY==960||origResY==1024) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(24, 44),
                        new Point(25, 43),
                        new Point(26,39),
                        new Point(26,43),
                        new Point(27,43),
                        //new Point(28,37),
                        //new Point(28,43)
                    };
                    //</editor-fold>
                } else if (origResY == 768) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(51,39),
                        new Point(54,37),
                        new Point(56,42),
                        new Point(64,39),
                        new Point(69,42),
                        //new Point(80,40),
                        //new Point(89,38)
                    };
                    //</editor-fold>
                } else if (origResY == 600) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(140, 34),
                        new Point(140, 37),
                        new Point(185, 34)};//need more;
                    //</editor-fold>
                }
                break;
            case 1360:
                if (origResY == 768) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(26,47),
                        new Point(29,47),
                        new Point(30,41),
                        new Point(30,47),
                        new Point(31,44),
                        //new Point(31,47),
                        //new Point(32,46),
                    };
                    //</editor-fold>
                }
                break;
            case 1366:
                if (origResY ==768){
                    p = p1366x768;
                }
                break;
            case 1400:
                if (origResY == 1050) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(26,50),
                        new Point(27,47),
                        new Point(28,45),
                        new Point(29,42),
                        new Point(31,42),
                        //new Point(32,44),
                        //new Point(33,47),
                        //new Point(34,50),
                        //new Point(38,45),
                        //new Point(38,50),
                        //new Point(43,45)
                    };
                    //</editor-fold>
                }
                break;
            case 1440:
                if (origResY == 900) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(35,50),
                        new Point(39,49),
                        new Point(45,47),
                        new Point(79,47),
                        new Point(79,48),
                        //new Point(79,49),
                        //new Point(79,50),
                        //new Point(79,51),
                    };
                    //</editor-fold>
                }
                break;
            case 1600:
                if (origResY == 900) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(32,51),
                        new Point(33,49),
                        new Point(35,47),
                        new Point(36,49),
                        new Point(36,50),
                        //new Point(37,52),
                        //new Point(38,54),
                        //new Point(38,55),
                        //new Point(39,57),
                        //new Point(43,52)
                    };
                    //</editor-fold>
                }
                break;
            case 1680:
                if (origResY == 1050) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(31,60),
                        new Point(32,58),
                        new Point(33,55),
                        new Point(33,56),
                        new Point(33,57),
                        //new Point(34,53),
                        //new Point(34,57),
                        //new Point(35,50),
                        //new Point(35,51),
                        //new Point(35,57),
                        //new Point(36,57),
                        //new Point(37,49),
                        //new Point(37,57)
                    };
                    //</editor-fold>
                }
                break;
            case 1776:
                if (origResY==1000){
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[]{
                        new Point(33,63),
                        new Point(34,60),
                        new Point(34,61),
                        new Point(35,58),
                        new Point(35,59),
                        //new Point(35,60),
                        //new Point(36,55),
                        //new Point(36,56),
                        //new Point(36,60),
                        //new Point(37,53),
                        //new Point(37,60),
                    };
                    //</editor-fold>
                }
                break;
            case 1920:
                if (origResY == 1080) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[] {
                        new Point(35,69),
                        new Point(36,66),
                        new Point(36,67),
                        new Point(37,64),
                        new Point(37,65),
                        //new Point(38,61),
                        //new Point(38,62)
                    };/*
                     * new Point(39,59),
                     * new Point(39,60),
                     * new Point(40,56),
                     * new Point(40,57),
                     * };*/
                     //</editor-fold>
                }
                break;
            case 2048:
                if (origResY == 1152) {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    p = new Point[] {
                        new Point(37,74),
                        new Point(38,72),
                        new Point(38,73),
                        new Point(39,69),
                        new Point(39,70),
                        //new Point(39,71),
                        //new Point(40,67)
                    };
                    //new Point(40,68),
                    //new Point(40,70),
                    //new Point(41,64),
                    //new Point(41,65)};
                }
                //</editor-fold>
                break;
            default:
                p = null;
                throw new IllegalStateException("Unsupported resolution");
        }
        //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="comment">
            p = new Point[]{
                new Point(24, 23),
                new Point(24, 22),
                new Point(25, 22),
                new Point(26, 22),
                new Point(27, 22),
                new Point(35, 17),
                new Point(35, 18),
                new Point(32, 19)};
            //</editor-fold>
        }    
        return p;
    }
    private Point getLowestPoint(Point[] p) {
        Point result = new Point(0,0);
        for (int i = 0; i < p.length; i++) {
            if (p[i].x > result.x) {
                result.setLocation(p[i].x,result.y);
            }
            if (p[i].y > result.y) {
                result.setLocation(result.x,p[i].y);
            }
        }
        return result;
    }
    private Color[] getColors(){
        Color[] c = null;
        if (desktopApp){
            //<editor-fold defaultstate="collapsed" desc="comment">
            c = new Color[]{
                new Color(0xA9A9A9),
                new Color(0xA9A9AA),
                new Color(0x9A9A9A),
                new Color(0xAAA9A9),
                new Color(0xA9AAA9),
                new Color(0xAAA9AA),
                new Color(0xA9AAAA),
                new Color(0xAAAAAA),
                new Color(0xAAAAA9),
                new Color(0xA6A6A6),
                new Color(0xA5A5A5),
                new Color(0x929292),
                new Color(0xAAA9A9),
                new Color(0xA9A9AA),
                new Color(0x969696)};
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="comment">
            c = new Color[]{
                new Color(0xEDEDED),
                new Color(0xEEEEEE),
                new Color(0xEDEEED),
                new Color(0xEDEEEE),
                new Color(0xEEEEED),
                new Color(0xEEEDED),
                new Color(0xEEEDEE),
                new Color(0xEDEDEE)};
            //</editor-fold>
        }
        return c;
    }
    private boolean supportedRes() {
        return !(getPoints()==null);
    }     
    private boolean isDesktopAppCommercial (){
        final Color[] rColor = getColors();
        final Point[] p = getPoints();
        
        if (supportedRes() && !(p==null)) {
            for (int i = 0; i < p.length; i++) {
                Color pixel = robot.getPixelColor(p[i].x, p[i].y); //memory leak here!
                if (pixel.equals(rColor[0])||pixel.equals(rColor[1])||pixel.equals(rColor[2])
                        ||pixel.equals(rColor[3])||pixel.equals(rColor[4])||pixel.equals(rColor[5])
                        ||pixel.equals(rColor[6])||pixel.equals(rColor[7])||pixel.equals(rColor[8])
                        ||pixel.equals(rColor[9])||pixel.equals(rColor[10])) {
                    if (isDebug()) {
                        writeLog(name,"HIT!!" + pixel+ " ("+p[i].x+","+p[i].y+")");
                    }
                } else {
                if (isDebug()) {
                    writeLog(name,"Miss: " + pixel+ " new Point("+p[i].x+","+p[i].y+"),");
                }
                return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }        
    public static void doMuteCMD() throws IOException {
        Runtime.getRuntime().exec(getMuteCommand());
    }
    public static void doUnmuteCMD() throws IOException {
        Runtime.getRuntime().exec(getUnmuteCommand());
    }
    private boolean isCommercial () {
        final Point[] pixels = getPoints();
        final Color[] requiredColor = getColors();
        
        for (int i = 0; i < pixels.length; i++) {
            Color pixel = robot.getPixelColor(pixels[i].x, pixels[i].y); //memory leak likely here!
            if (pixel.equals(requiredColor[0])||pixel.equals(requiredColor[1])
                    ||pixel.equals(requiredColor[2])||pixel.equals(requiredColor[3])
                    ||pixel.equals(requiredColor[4])||pixel.equals(requiredColor[5])
                    ||pixel.equals(requiredColor[6])||pixel.equals(requiredColor[7])) {
                    if (isDebug()) {writeLog(name, "HIT!!");}
            } else {
                if (isDebug()) {writeLog(name,"miss: " + pixel+ " x="+pixels[i].x+" y="+pixels[i].y);}
                return false;
            }
        }
        return true;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Logging methods">
    public static void resetLog(final String filename) {
        if (isLog()) {
            try {
                PrintWriter out = new PrintWriter(new FileWriter(filename+"_log.txt"));
                out.write("");
                out.println("To disable this log, change the \'log=true\' value in hulumute.config to \'log=false\'");
                out.println(new Date()+" hulumute process begins");
                out.close();
            } catch (Exception ex) {
                System.out.println("Error clearing log: "+ex.getMessage());
            }
        }
    }
    
    public static void writeLog(final String filename, String msg) {
        if (isLog()) {
            try {
                msg = new Date() +" "+ msg;
                PrintWriter out = new PrintWriter(new FileWriter(filename+"_log.txt", true));
                out.println(msg);
                out.close();
            } catch (Exception ex) {
                System.out.println("Write error ("+ex.getMessage()+"on log.");
                System.out.println("Attempted line: "+msg);
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Screensaver Methods">
    public void createScreensaver() throws Exception {
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        
        try {
            screenSaverWindow.dispose();
            screenSaverWindowTop.dispose();
        } catch (NullPointerException npe) {
            if (debug) {writeLog(name,"No window to dispose");}
        }
        try {
            screenSaverFrame.dispose();
            screenSaverWindowTop.dispose();
        } catch (NullPointerException npe) {
            if (debug) {writeLog(name,"No frame to dispose");}
        }
        // Create a new blank cursor
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
        cursorImg, new Point(0, 0), "blank cursor");
        
        if (desktopApp){  //if using desktop app, we can make a JFrame and use key listeners
            screenSaverFrame = new JFrame(); //*
            screenSaverFrame.getContentPane().addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    if(code == KeyEvent.VK_ESCAPE){
                        escapePressed = true;
                    }
                }
                public void keyReleased(KeyEvent e) {}
                public void keyTyped(KeyEvent e) {}
            });
            screenSaverFrame.setSize(0, 0);
            screenSaverFrame.setLocation(0,getLowestPoint(getPoints()).y+1);
            //screenSaverFrame.setUndecorated(true);
            //screenSaverFrame.setFocusableWindowState(true);
            //screenSaverFrame.setFocusable(true);
            screenSaverFrame.setTitle("Blocking Ads");
            screenSaverFrame.getContentPane().setCursor(blankCursor); //blank cursor when mouse enters frame/window
            screenSaverFrame.validate();
        }
        screenSaverWindow = new JWindow();  //window required for browser, or flash player will lose focus
        screenSaverWindow.setSize(screenWidth, (screenHeight-getLowestPoint(getPoints()).y+1));
        screenSaverWindow.setLocation(0,getLowestPoint(getPoints()).y+1);
        screenSaverWindow.setAlwaysOnTop(true);
        screenSaverWindow.getContentPane().setForeground(Color.BLACK);
        screenSaverWindow.getContentPane().setBackground(Color.BLACK);
        screenSaverWindow.getContentPane().setCursor(blankCursor); //blank cursor when mouse enters frame/window
        screenSaverWindow.validate();
        
        Font font = new Font("Serif", Font.BOLD, 14);
        try {
            InputStream fin = this.getClass().getResourceAsStream("/hulumutegui/resources/font.ttf");
            font = Font.createFont (Font.PLAIN, fin).deriveFont(14f);
        } catch (Exception e){
            writeLog(name,"Error loading font");
        }
        JLabel jlbl = new JLabel("Blocked by HuluMute");
        jlbl.setForeground(new Color(0x78B631));
        jlbl.setBorder(BorderFactory.createEmptyBorder(5,10,5,5));
        jlbl.setFont(font);
        screenSaverWindowTop = new JWindow();
        screenSaverWindowTop.setSize(screenWidth-getLowestPoint(getPoints()).x+1, getLowestPoint(getPoints()).y+1);
        screenSaverWindowTop.setLocation(getLowestPoint(getPoints()).x+1,0);
        screenSaverWindowTop.setAlwaysOnTop(true);
        screenSaverWindowTop.getContentPane().setForeground(Color.BLACK);
        screenSaverWindowTop.getContentPane().setBackground(Color.BLACK);
        screenSaverWindowTop.getContentPane().add(jlbl);
        screenSaverWindowTop.getContentPane().setCursor(blankCursor); //blank cursor when mouse enters frame/window
        screenSaverWindowTop.validate();
    }
    private void setScreensaverVisible(boolean b) {
        if (desktopApp) {
            screenSaverFrame.setVisible(b);
            if (b) {              //necessary for escape key listener
                try {
                    Runtime.getRuntime().exec("cmdow.exe \"Blocking Ads\" /ACT");
                } catch (Exception e) {
                    writeLog(name,"Could not select screen saver frame window: "+ e.getMessage());
                }
                screenSaverFrame.getContentPane().requestFocusInWindow();
            } else {
                try {
                    Runtime.getRuntime().exec("cmdow.exe \"Hulu Desktop\" /ACT");
                } catch (Exception e) {
                    writeLog(name,"Could not select screen saver frame window: "+ e.getMessage());
                }
            }
        }
        screenSaverWindow.setVisible(b);
        screenSaverWindowTop.setVisible(b);
    }
    //</editor-fold>
    private static int totalRunning(String process) throws Exception {
        Process p = Runtime.getRuntime().exec("TASKLIST /FI \"imagename eq "+process+"\"");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
        p.getInputStream()));
        int running = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            writeLog(name, line);
            if (line.contains(process)) {
                running++;
            }
        }
        return running;
    }
       
    //<editor-fold defaultstate="collapsed" desc="Config File methods">
    public static String readConfig(String filename, String var) {
        String sValue = "";
        if (isConfig(filename)) {
            filename = filename + ".conf";
            try {
                BufferedReader fin = new BufferedReader(new FileReader(filename));
                String s = fin.readLine();
                while ((s != null)) {
                    
                    if (!s.startsWith("#")&&s.startsWith(var)) {
                        sValue = s.substring(s.indexOf("=")+1);
                        writeLog(name,"Pulled value "+sValue+" for variable "
                                +var+" from "+filename);
                        return sValue;
                    } else {
                        s = fin.readLine(); //next line
                    }
                }
            } catch (Exception e) {
                writeLog(name,"Error reading config file: " + e.getMessage());
            }
        } else {
            writeLog(name,"Config file does not exist!");
        }
        if (sValue.equals("")) {
            writeLog(name,"hulumute.conf "+var+" not found, setting to default");
            sValue = getDefaultValue(var);
        }
        return sValue;
    }
    public static boolean isConfig(String filename){
        return (new File(filename+".conf").isFile());
    }
    
    public static void newConfig(final String filename, Object[] aValues)
            throws IOException{
        try {
            System.out.println("Creating/updating config file.");
            writeLog(name,"Creating/updating config file.");
            PrintWriter out = new PrintWriter(new FileWriter(filename+".conf"));
            out.println("#  hulumute config file  #");
            out.println("# MODIFY AT YOUR OWN RISK#");
            out.println("##########################");
            out.println("");
            out.println("#using the Hulu Labs desktop application? (true or false)");
            out.println("#(only certain screen resolutions are supported)");
            out.println("desktopApp="+aValues[0].toString());
            out.println("");
            out.println("#create a log file in the executable's directory?");
            out.println("#useful for testing/diagnostics (true or false)");
            out.println("log="+aValues[1].toString());
            out.println("");
            out.println("#advanced debug logging (true or false)");
            out.println("debug="+aValues[2].toString());
            out.println("");
            out.println("#check for updates (true or false)");
            out.println("checkUpdate="+aValues[3].toString());
            out.println("");
            out.println("#delay (in milliseconds) between pixel checks");
            out.println("delay="+aValues[4].toString());
            out.println("");
            out.println("#mute and unmute commands");
            out.println("muteCommand="+aValues[5].toString());
            out.println("unmuteCommand="+aValues[6].toString());
            out.println("");
            out.println("#block ad visuals with black screen? (true or false)");
            out.println("blockVisuals="+aValues[7].toString());
            
            out.close();
        } catch (Exception ex) {
            System.out.println("Write error ("+ex.getMessage()+"on config.");
            writeLog(name,"Write error ("+ex.getMessage()+"on config.");
            throw new IOException("Could not complete creation of new config file.");
        }
    }
    
    public static boolean configChanged(final String filename) {
        //String [] aValues = { "log","debug","checkupdate","delay","mutecommand","unmutecommand","blockvisuals"};
        Object [] aCurrentVars = getCurrentVars();
        for (int i = 0; i < aDefaults[0].length; i++){
            //System.out.println(aCurrentVars[i].toString());\
            try {
                boolean temp = log;
                log = false;
                if (!readConfig(filename,aDefaults[0][i]).equals(aCurrentVars[i].toString()) ){
                    return true;
                }
                log = temp;
            } catch (Exception e) {
                writeLog(name,"An error occured while determining if configuration values have changed. "+e.getMessage());
            }
        }
        return false;
    }
    //</editor-fold>
    public static String getDefaultValue(String var) {
        String result = null;
        //System.out.println("\nVariable =" +var);
        for (int i = 0; i < aDefaults[0].length; i++) {
            //System.out.print(aDefaults[0][i]+" ");
            if (aDefaults[0][i].toLowerCase().contains(var.toLowerCase())) {
                result = aDefaults[1][i];
                return result;
            }                
        }
        writeLog(name,"Could not find default value for variable "+var);
        return result;
    }
    //<editor-fold defaultstate="collapsed" desc="URL Related Methods">
    public static boolean isNewVersion(double dProgVersion) throws IOException {
        String sUpdateURL = "http://hulumute.wordpress.com/downloads/";
        String var = "<!--Current_Version";
        String sLatestVersion = "";
        boolean bNewVersion = false;
        try {
            URL version = new URL(sUpdateURL);
            URLConnection uc = version.openConnection();
            BufferedReader fin = new BufferedReader(new InputStreamReader(uc
                    .getInputStream()));
            String s = fin.readLine();
            while ((s != null)) {
                if (s.contains(var)) {
                    sLatestVersion = s.substring(s.indexOf("=")+1,s.lastIndexOf("--"));
                    writeLog(name,"Latest version determined to be "+sLatestVersion+" "+"from "+sUpdateURL);
                    s = fin.readLine();
                } else {
                    s = fin.readLine(); //next line
                }
            }
            fin.close();
        } catch (Exception e) {
            writeLog(name,"Error reading latest version: " + e.getMessage());
            throw new IOException("Could not connect to website "+sUpdateURL);
        }
        try {
            double dLatestVersion = Double.parseDouble(sLatestVersion);
            if (dProgVersion >= dLatestVersion) {
                bNewVersion = false;
                writeLog(name,"Program version "+dProgVersion+" "+" is the latest version "+dLatestVersion);
            } else {
                bNewVersion = true;
                writeLog(name,"Program version "+dProgVersion+" "+" is not up to date. Latest version is "+dLatestVersion);
            }
        } catch (Exception e) {
            writeLog(name,"Error parsing latest version to double: " + e.getMessage());
        }
        return bNewVersion;
    }
    
    public static void getNewVersion() {
        try {
            writeLog(name,"Opening downloads site.");
            String url = "http://hulumute.wordpress.com/downloads/";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        }
        catch (Exception e) {
            writeLog(name,"An error occured opening downloads site "+e.getMessage());
        }
    }
    public static void visitFAQ() {
        try {
            writeLog(name,"Opening FAQ webpage.");
            String url = "http://hulumute.wordpress.com/FAQ/";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        }
        catch (Exception e) {
            writeLog(name,"An error occured opening FAQ page "+e.getMessage());
        }
    }
    
    public static void visitWebsite() {
        try {
            writeLog(name,"Opening HuluMute site.");
            String url = "http://hulumute.wordpress.com/";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        }
        catch (Exception e) {
            writeLog(name,"An error occured opening HuluMute site "+e.getMessage());
        }
    }
    //</editor-fold>
    
    public static void checkOS() {
        String[] aOS = {"Windows 8","Windows 7", "Windows Vista","Windows XP"};
        String sOS = System.getProperty("os.name");
        
        if (!sOS.equals(aOS[0])&&!sOS.equals(aOS[1])&&!sOS.equals(aOS[2])&&!sOS.equals(aOS[3])) {
            final JPanel panel = new JPanel();      //display warning for unsupported operating system
            JOptionPane.showMessageDialog(panel, 
                    "It appears you are running an unsupported operating system.\n "
                    + "             This program may not function as expected.", "Warning",
            JOptionPane.WARNING_MESSAGE);
            writeLog(name,"Unsupported OS detected: "+sOS);
        } else {
            writeLog(name,"Supported OS detected: "+sOS);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="public sets and gets">
    public void setActive(boolean b) {
        this.active = b;
    }
    public boolean getActive() {
        return active;
    }
    public static Object[] getCurrentVars() {
        Object[] aCurrentVars = new Object[]{desktopApp,log,debug,checkUpdate,delay,muteCommand,unmuteCommand,blockVisuals};
        if (debug) {
            writeLog(name,"Current values stand as follows:");
            writeLog(name,aCurrentVars[0].toString());
            writeLog(name,aCurrentVars[1].toString());
            writeLog(name,aCurrentVars[2].toString());
            writeLog(name,aCurrentVars[3].toString());
            writeLog(name,aCurrentVars[4].toString());
            writeLog(name,aCurrentVars[5].toString());
            writeLog(name,aCurrentVars[6].toString());
            writeLog(name,aCurrentVars[7].toString());
            //writeLog(name,aCurrentVars[8].toString());
        }
        return aCurrentVars;
    }
    public static String getName() {
        return name;
    }
    public static double getVersion() {
        return dVersion;
    }
    public static int getDelay() {
        return delay;
    }
    public static String getMuteCommand() {
        return muteCommand;
    }
    public static String getUnmuteCommand() {
        return unmuteCommand;
    }
    public static boolean isDesktopApp() {
        return desktopApp;
    }
    public static boolean isLog() {
        return log;
    }
    public static boolean isDebug() {
        return debug;
    }
    public static boolean isBlockVisuals() {
        return blockVisuals;
    }
    public static boolean isCheckUpdate() {
        return checkUpdate;
    }
    public void setDesktopApp(boolean b) {
        writeLog(name,"Variable desktopApp changed to "+b+" by GUI");
        desktopApp = b;
    }
    public void setLog(boolean b) {
        writeLog(name,"Variable log changed to "+b+" by GUI");
        log = b;
    }
    public void setDebug(boolean b) {
        debug = b;
        writeLog(name,"Variable debug changed to "+b+" by GUI");
    }
    public void setBlockVisuals(boolean b) {
        blockVisuals = b;
        writeLog(name,"Variable blockVisuals changed to "+b+" by GUI");
    }
    public void setCheckUpdate(boolean b) {
        checkUpdate = b;
        writeLog(name,"Variable checkUpdate changed to "+b+" by GUI");
    }
    public void setDelay(int i) {
        delay = i;
        writeLog(name,"Variable delay changed to "+i+" by GUI");
    }
    public void setMuteCommand(String s) {
        muteCommand = s;
        writeLog(name,"Variable muteCommand changed to "+s+" by GUI");
    }
    public void setUnmuteCommand(String s) {
        unmuteCommand = s;
        writeLog(name,"Variable unmuteCommand changed to "+s+" by GUI");
    }
    //</editor-fold>
}
