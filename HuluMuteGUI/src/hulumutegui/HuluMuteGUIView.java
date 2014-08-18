/*
 * HuluMuteGUIView.java
 */

package hulumutegui;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import muteprocess.MuteProcess;
import org.jdesktop.application.Application.ExitListener;

/**
 * The application's main frame.
 */
public class HuluMuteGUIView extends FrameView {
    private static boolean bInitial;
    MuteProcess mp;
    private TrayIcon trayIcon;
    private SystemTray tray;
    Image imageRunning, imageStopped;
    MenuItem miOpen, miQuit, miToggle;
    
    public HuluMuteGUIView(SingleFrameApplication app) {
        super(app);

        initComponents();
        try {
            mp = new MuteProcess();
            createTray();
            jbtnMuteToggle.doClick();
        } catch (Exception e) {
            MuteProcess.writeLog(MuteProcess.getName(), "An error occured creating the tray icon: "+e.getMessage());
        }        
        HuluMuteGUIApp.getApplication().addExitListener(new ExitListener()  //closes to tray instead of exiting program
        {
            public boolean canExit(EventObject event) {
                hideWindow();
                statusMessageLabel.setText("");
                return false;
            }
            public void willExit(EventObject event) {
              System.exit(0);
            }
        });

        loadValues();
        
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        jbtnMuteToggle.requestFocusInWindow();
        //use timer to check for mute process status and change program window & tray icon accordingly
        ActionListener taskPerformer = new ActionListener() {
    	public void actionPerformed(ActionEvent evt) {
  	    updateChanges();
    	}
        };
        Timer timer = new Timer( 250 , taskPerformer);
        timer.setRepeats(true);
        timer.start();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = HuluMuteGUIApp.getApplication().getMainFrame();
            aboutBox = new HuluMuteGUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        HuluMuteGUIApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jbtnMuteToggle = new javax.swing.JButton();
        jbtnExit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jchckLog = new javax.swing.JCheckBox();
        jchckDebug = new javax.swing.JCheckBox();
        jchckVisuals = new javax.swing.JCheckBox();
        jchckUpdate = new javax.swing.JCheckBox();
        jtxtDelay = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtxtMute = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtUnmute = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jchckAdvanced = new javax.swing.JCheckBox();
        jbtnSaveConfig = new javax.swing.JButton();
        jbtnLoadDefaults = new javax.swing.JButton();
        jbtnMute = new javax.swing.JButton();
        jbtnDelay = new javax.swing.JButton();
        jbtnUnmute = new javax.swing.JButton();
        jradBrowser = new javax.swing.JRadioButton();
        jradDesktop = new javax.swing.JRadioButton();
        jbtnCheckUpdate = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem jmnuMinimize = new javax.swing.JMenuItem();
        jmnuQuit = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jmnuFAQ = new javax.swing.JMenuItem();
        jmnuUpdate = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        buttonGroup1 = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        jbtnMuteToggle.setFocusPainted(false);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(hulumutegui.HuluMuteGUIApp.class).getContext().getResourceMap(HuluMuteGUIView.class);
        jbtnMuteToggle.setLabel(resourceMap.getString("jbtnMuteToggle.label")); // NOI18N
        jbtnMuteToggle.setName("jbtnMuteToggle"); // NOI18N
        jbtnMuteToggle.setNextFocusableComponent(jbtnExit);
        jbtnMuteToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMuteToggleActionPerformed(evt);
            }
        });

        jbtnExit.setText(resourceMap.getString("jbtnExit.text")); // NOI18N
        jbtnExit.setName("jbtnExit"); // NOI18N
        jbtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitActionPerformed(evt);
            }
        });

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel1MouseEntered(evt);
            }
        });

        jchckLog.setText(resourceMap.getString("jchckLog.text")); // NOI18N
        jchckLog.setToolTipText(resourceMap.getString("jchckLog.toolTipText")); // NOI18N
        jchckLog.setName("jchckLog"); // NOI18N
        jchckLog.setNextFocusableComponent(jchckDebug);
        jchckLog.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchckLogItemStateChanged(evt);
            }
        });

        jchckDebug.setText(resourceMap.getString("jchckDebug.text")); // NOI18N
        jchckDebug.setToolTipText(resourceMap.getString("jchckDebug.toolTipText")); // NOI18N
        jchckDebug.setEnabled(false);
        jchckDebug.setName("jchckDebug"); // NOI18N
        jchckDebug.setNextFocusableComponent(jchckVisuals);
        jchckDebug.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchckDebugItemStateChanged(evt);
            }
        });

        jchckVisuals.setText(resourceMap.getString("jchckVisuals.text")); // NOI18N
        jchckVisuals.setToolTipText(resourceMap.getString("jchckVisuals.toolTipText")); // NOI18N
        jchckVisuals.setName("jchckVisuals"); // NOI18N
        jchckVisuals.setNextFocusableComponent(jchckUpdate);
        jchckVisuals.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchckVisualsItemStateChanged(evt);
            }
        });

        jchckUpdate.setText(resourceMap.getString("jchckUpdate.text")); // NOI18N
        jchckUpdate.setToolTipText(resourceMap.getString("jchckUpdate.toolTipText")); // NOI18N
        jchckUpdate.setName("jchckUpdate"); // NOI18N
        jchckUpdate.setNextFocusableComponent(jbtnCheckUpdate);
        jchckUpdate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchckUpdateItemStateChanged(evt);
            }
        });

        jtxtDelay.setText(resourceMap.getString("jtxtDelay.text")); // NOI18N
        jtxtDelay.setToolTipText(resourceMap.getString("jtxtDelay.toolTipText")); // NOI18N
        jtxtDelay.setEnabled(false);
        jtxtDelay.setName("jtxtDelay"); // NOI18N
        jtxtDelay.setNextFocusableComponent(jbtnDelay);
        jtxtDelay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDelayActionPerformed(evt);
            }
        });
        jtxtDelay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDelayKeyPressed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(resourceMap.getString("jLabel1.toolTipText")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jtxtMute.setToolTipText(resourceMap.getString("jtxtMute.toolTipText")); // NOI18N
        jtxtMute.setEnabled(false);
        jtxtMute.setName("jtxtMute"); // NOI18N
        jtxtMute.setNextFocusableComponent(jbtnMute);
        jtxtMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtMuteActionPerformed(evt);
            }
        });
        jtxtMute.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMuteKeyPressed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(resourceMap.getString("jLabel2.toolTipText")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jtxtUnmute.setToolTipText(resourceMap.getString("jtxtUnmute.toolTipText")); // NOI18N
        jtxtUnmute.setEnabled(false);
        jtxtUnmute.setName("jtxtUnmute"); // NOI18N
        jtxtUnmute.setNextFocusableComponent(jbtnUnmute);
        jtxtUnmute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtUnmuteActionPerformed(evt);
            }
        });
        jtxtUnmute.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtUnmuteKeyPressed(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(resourceMap.getString("jLabel4.toolTipText")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jchckAdvanced.setText(resourceMap.getString("jchckAdvanced.text")); // NOI18N
        jchckAdvanced.setName("jchckAdvanced"); // NOI18N
        jchckAdvanced.setNextFocusableComponent(jbtnMuteToggle);
        jchckAdvanced.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchckAdvancedItemStateChanged(evt);
            }
        });

        jbtnSaveConfig.setText(resourceMap.getString("jbtnSaveConfig.text")); // NOI18N
        jbtnSaveConfig.setName("jbtnSaveConfig"); // NOI18N
        jbtnSaveConfig.setNextFocusableComponent(jchckAdvanced);
        jbtnSaveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveConfigActionPerformed(evt);
            }
        });

        jbtnLoadDefaults.setText(resourceMap.getString("jbtnLoadDefaults.text")); // NOI18N
        jbtnLoadDefaults.setName("jbtnLoadDefaults"); // NOI18N
        jbtnLoadDefaults.setNextFocusableComponent(jbtnSaveConfig);
        jbtnLoadDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnLoadDefaultsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jchckAdvanced)
                    .addComponent(jbtnLoadDefaults, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(jbtnSaveConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtnLoadDefaults)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnSaveConfig)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jchckAdvanced)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jbtnMute.setText(resourceMap.getString("jbtnMute.text")); // NOI18N
        jbtnMute.setEnabled(false);
        jbtnMute.setName("jbtnMute"); // NOI18N
        jbtnMute.setNextFocusableComponent(jtxtUnmute);
        jbtnMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMuteActionPerformed(evt);
            }
        });

        jbtnDelay.setText(resourceMap.getString("jbtnDelay.text")); // NOI18N
        jbtnDelay.setEnabled(false);
        jbtnDelay.setName("jbtnDelay"); // NOI18N
        jbtnDelay.setNextFocusableComponent(jtxtMute);
        jbtnDelay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDelayActionPerformed(evt);
            }
        });

        jbtnUnmute.setText(resourceMap.getString("jbtnUnmute.text")); // NOI18N
        jbtnUnmute.setEnabled(false);
        jbtnUnmute.setName("jbtnUnmute"); // NOI18N
        jbtnUnmute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUnmuteActionPerformed(evt);
            }
        });

        jradBrowser.setSelected(true);
        jradBrowser.setText(resourceMap.getString("jradBrowser.text")); // NOI18N
        jradBrowser.setToolTipText(resourceMap.getString("jradBrowser.toolTipText")); // NOI18N
        jradBrowser.setName("jradBrowser"); // NOI18N
        jradBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jradBrowserActionPerformed(evt);
            }
        });

        jradDesktop.setText(resourceMap.getString("jradDesktop.text")); // NOI18N
        jradDesktop.setToolTipText(resourceMap.getString("jradDesktop.toolTipText")); // NOI18N
        jradDesktop.setName("jradDesktop"); // NOI18N
        jradDesktop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jradDesktopActionPerformed(evt);
            }
        });

        jbtnCheckUpdate.setText(resourceMap.getString("jbtnCheckUpdate.text")); // NOI18N
        jbtnCheckUpdate.setName("jbtnCheckUpdate"); // NOI18N
        jbtnCheckUpdate.setNextFocusableComponent(jtxtDelay);
        jbtnCheckUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCheckUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jtxtDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnDelay))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jradBrowser)
                        .addGap(18, 18, 18)
                        .addComponent(jradDesktop))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jchckDebug)
                            .addComponent(jchckLog)
                            .addComponent(jchckVisuals)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jchckUpdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtnCheckUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxtUnmute)
                            .addComponent(jtxtMute, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtnUnmute)
                            .addComponent(jbtnMute))))
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(247, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jradBrowser)
                            .addComponent(jradDesktop))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jchckLog)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jchckDebug)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jchckVisuals)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jchckUpdate)
                            .addComponent(jbtnCheckUpdate)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxtDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnDelay))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtMute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtUnmute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jbtnUnmute)))
                    .addComponent(jbtnMute))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jbtnMuteToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 251, Short.MAX_VALUE)
                        .addComponent(jbtnExit)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnMuteToggle)
                    .addComponent(jbtnExit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(hulumutegui.HuluMuteGUIApp.class).getContext().getActionMap(HuluMuteGUIView.class, this);
        jmnuMinimize.setAction(actionMap.get("quit")); // NOI18N
        jmnuMinimize.setText(resourceMap.getString("jmnuMinimize.text")); // NOI18N
        jmnuMinimize.setToolTipText(resourceMap.getString("jmnuMinimize.toolTipText")); // NOI18N
        jmnuMinimize.setName("jmnuMinimize"); // NOI18N
        fileMenu.add(jmnuMinimize);

        jmnuQuit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jmnuQuit.setText(resourceMap.getString("jmnuQuit.text")); // NOI18N
        jmnuQuit.setName("jmnuQuit"); // NOI18N
        jmnuQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuQuitActionPerformed(evt);
            }
        });
        fileMenu.add(jmnuQuit);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        jmnuFAQ.setText(resourceMap.getString("jmnuFAQ.text")); // NOI18N
        jmnuFAQ.setName("jmnuFAQ"); // NOI18N
        jmnuFAQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuFAQActionPerformed(evt);
            }
        });
        helpMenu.add(jmnuFAQ);

        jmnuUpdate.setText(resourceMap.getString("jmnuUpdate.text")); // NOI18N
        jmnuUpdate.setName("jmnuUpdate"); // NOI18N
        jmnuUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuUpdateActionPerformed(evt);
            }
        });
        helpMenu.add(jmnuUpdate);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 260, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jToggleButton1.setText(resourceMap.getString("jToggleButton1.text")); // NOI18N
        jToggleButton1.setName("jToggleButton1"); // NOI18N

        jInternalFrame1.setName("jInternalFrame1"); // NOI18N
        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed
    //check for config changes
    if (MuteProcess.configChanged(MuteProcess.getName())) {
        promptSave();
    }
    System.exit(0);
}//GEN-LAST:event_jbtnExitActionPerformed

public final void createTray() throws Exception{
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        MuteProcess.writeLog(MuteProcess.getName(),"Unable to set LookAndFeel");
    }
    if (SystemTray.isSupported()) {
        MuteProcess.writeLog(MuteProcess.getName(),"System tray supported");
        tray = SystemTray.getSystemTray();

        imageRunning = ImageIO.read(getClass().getResource("/hulumutegui/resources/hmRunning.png"));
        imageStopped = ImageIO.read(getClass().getResource("/hulumutegui/resources/hmStopped.png"));
        
        ActionListener exitListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MuteProcess.writeLog(MuteProcess.getName(),"Exiting....");
                try {
                    MuteProcess.doUnmuteCMD();
                    if (MuteProcess.configChanged(MuteProcess.getName())) {
                        promptSave();
                    }
                } catch (Exception ex){
                    MuteProcess.writeLog(MuteProcess.getName(),"Error occured while unmuting"+ex.getMessage());
                }
                System.exit(0);
            }
        };
        PopupMenu popup = new PopupMenu();
        miOpen = new MenuItem("Open");
        miOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {      
                showWindow();
                MuteProcess.writeLog(MuteProcess.getName(),"Program window opened from system tray");
            }
        });
        popup.add(miOpen);
        
        miToggle = new MenuItem("Stop");
        miToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnMuteToggle.doClick();
            }
        });
        popup.add(miToggle);
        
        miQuit = new MenuItem("Quit");
        miQuit.addActionListener(exitListener);
        popup.add(miQuit);

        trayIcon = new TrayIcon(imageRunning, "HuluMute", popup);
        trayIcon.setImageAutoSize(true);
        MouseListener clickListener = new MouseListener() {  //open/close window on trayicon click
                public void mouseClicked(MouseEvent me) {
                    if (me.getButton()==1){
                        if (me.isControlDown()) {
                            jbtnMuteToggle.doClick();
                        } else {
                            if (windowVisible()) {
                                hideWindow();
                                MuteProcess.writeLog(MuteProcess.getName(),
                                    "Program window closed from system tray");
                            } else {
                                showWindow();
                                MuteProcess.writeLog(MuteProcess.getName(),
                                    "Program window opened from system tray");
                            }
                        }
                    }
                    if (me.getButton()==2) {
                        jbtnMuteToggle.doClick();
                    }
                }
                public void mousePressed(MouseEvent me) {}
                public void mouseReleased(MouseEvent me) {}
                public void mouseEntered(MouseEvent me) {}
                public void mouseExited(MouseEvent me) {}            
        };
        trayIcon.addMouseListener(clickListener);
    } else {
        MuteProcess.writeLog(MuteProcess.getName(),"System tray not supported");
    }
    tray.add(trayIcon);
}

public void initFocus() {
    jbtnMuteToggle.requestFocusInWindow();
}

public boolean windowVisible(){
    return this.getFrame().isVisible();
}
public void hideWindow() {
    HuluMuteGUIApp.getApplication().hide(this);
}

public void showWindow() {
    HuluMuteGUIApp.getApplication().show(this);
    jbtnMuteToggle.requestFocusInWindow();
}

private void jbtnMuteToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMuteToggleActionPerformed
    if (mp.getActive() == true) {
        mp.setActive(false);
        jbtnMuteToggle.setText("Start Muting");
        statusMessageLabel.setText("Mute process stopped.");
        trayIcon.setImage(imageStopped);
        miToggle.setLabel("Start");
    } else {
        mp.setActive(true);
        mp.doMuteProcess();
        jbtnMuteToggle.setText("Stop Muting");
        statusMessageLabel.setText("Mute process started.");
        trayIcon.setImage(imageRunning);
        miToggle.setLabel("Stop");       
    }
}//GEN-LAST:event_jbtnMuteToggleActionPerformed
public void updateChanges() {
    if (mp.getActive() == false) {
        jbtnMuteToggle.setText("Start Muting");
        //statusMessageLabel.setText("Mute process stopped.");
        trayIcon.setImage(imageStopped);
        miToggle.setLabel("Start");
    } else {
        jbtnMuteToggle.setText("Stop Muting");
        //statusMessageLabel.setText("Mute process started.");
        trayIcon.setImage(imageRunning);
        miToggle.setLabel("Stop");       
    }
}

private void jbtnCheckUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCheckUpdateActionPerformed
    try {
    if (MuteProcess.isNewVersion(MuteProcess.getVersion())) {
        promptUpdate();
    } else {
        statusMessageLabel.setText("Program version is up to date.");
    }
    } catch (IOException e) {
        statusMessageLabel.setText(e.getMessage());
    }
}//GEN-LAST:event_jbtnCheckUpdateActionPerformed

private void jbtnLoadDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnLoadDefaultsActionPerformed
    
    jradBrowser.setSelected(!Boolean.parseBoolean(MuteProcess.getDefaultValue("desktopApp")));
    jradDesktop.setSelected(Boolean.parseBoolean(MuteProcess.getDefaultValue("desktopApp")));
    jchckDebug.setSelected(Boolean.parseBoolean(MuteProcess.getDefaultValue("debug")));
    jchckLog.setSelected(Boolean.parseBoolean(MuteProcess.getDefaultValue("log")));
    jchckUpdate.setSelected(Boolean.parseBoolean(MuteProcess.getDefaultValue("checkUpdate")));
    jchckVisuals.setSelected(Boolean.parseBoolean(MuteProcess.getDefaultValue("blockVisuals")));
    jtxtDelay.setText(MuteProcess.getDefaultValue("delay"));
    jbtnDelay.setEnabled(true);
    jbtnDelay.doClick();
    jtxtMute.setText(MuteProcess.getDefaultValue("muteCommand"));
    jbtnMute.setEnabled(true);
    jbtnMute.doClick();
    jtxtUnmute.setText(MuteProcess.getDefaultValue("unmuteCommand"));
    jbtnUnmute.setEnabled(true);
    jbtnUnmute.doClick();
    
    statusMessageLabel.setText("Default configuration values loaded.");
}//GEN-LAST:event_jbtnLoadDefaultsActionPerformed

private void jchckLogItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchckLogItemStateChanged
    if (!bInitial) {
        mp.setLog(jchckLog.isSelected());
        if (jchckLog.isSelected()) {
            statusMessageLabel.setText("Log enabled.");
        } else {
            statusMessageLabel.setText("Log disabled.");
        }
    }
}//GEN-LAST:event_jchckLogItemStateChanged

private void jchckAdvancedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchckAdvancedItemStateChanged
    if (jchckAdvanced.isSelected()) {
        jchckDebug.setEnabled(true);
        jtxtDelay.setEnabled(true);
        jtxtMute.setEnabled(true);
        jtxtUnmute.setEnabled(true);
        statusMessageLabel.setText("Editing advanced settings enabled.");
    } else {
        jchckDebug.setEnabled(false);
        jtxtDelay.setEnabled(false);
        jtxtMute.setEnabled(false);
        jtxtUnmute.setEnabled(false);
        statusMessageLabel.setText("Editing advanced settings disabled.");
    }
}//GEN-LAST:event_jchckAdvancedItemStateChanged

private void jchckDebugItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchckDebugItemStateChanged
    if (!bInitial) {
        mp.setDebug(jchckDebug.isSelected());
        if (jchckDebug.isSelected()) {
            statusMessageLabel.setText("Advanced debug enabled.");
        } else {
            statusMessageLabel.setText("Advanced debug disabled.");
        }
    }
}//GEN-LAST:event_jchckDebugItemStateChanged

private void jchckVisualsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchckVisualsItemStateChanged
    if (!bInitial) {
        mp.setBlockVisuals(jchckVisuals.isSelected());
        if (jchckVisuals.isSelected()) {
            statusMessageLabel.setText("Visual ad blocking enabled.");
        } else {
            statusMessageLabel.setText("Visual ad blocking disabled.");
        }
    }
}//GEN-LAST:event_jchckVisualsItemStateChanged

private void jchckUpdateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchckUpdateItemStateChanged
    if (!bInitial) {
        mp.setCheckUpdate(jchckUpdate.isSelected());
        if (jchckUpdate.isSelected()) {
            statusMessageLabel.setText("Update check enabled.");
        } else {
            statusMessageLabel.setText("Update check disabled.");
        }
    }
}//GEN-LAST:event_jchckUpdateItemStateChanged

private void jtxtDelayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDelayKeyPressed
    jbtnDelay.setEnabled(true);
}//GEN-LAST:event_jtxtDelayKeyPressed

private void jbtnDelayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDelayActionPerformed
    try {
        int i = Integer.parseInt(jtxtDelay.getText());
        if ((i >= 50) && (i <= 2500)) {
            mp.setDelay(i);
            jbtnDelay.setEnabled(false);
            statusMessageLabel.setText("Delay valued changed.");
        } else {
            jtxtDelay.requestFocusInWindow();
            jtxtDelay.selectAll();
            statusMessageLabel.setText("Delay value entered is out of range: Must be between 50 and 2500.");
        }
    } catch (Exception e){
        statusMessageLabel.setText("Could not parse delay value. This operation requires an integer.");
    }
}//GEN-LAST:event_jbtnDelayActionPerformed

private void jtxtMuteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMuteKeyPressed
    jbtnMute.setEnabled(true);
}//GEN-LAST:event_jtxtMuteKeyPressed

private void jbtnMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMuteActionPerformed
    mp.setMuteCommand(jtxtMute.getText());
    jbtnMute.setEnabled(false);
    statusMessageLabel.setText("Mute command value changed");
}//GEN-LAST:event_jbtnMuteActionPerformed

private void jtxtUnmuteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtUnmuteKeyPressed
    jbtnUnmute.setEnabled(true);
}//GEN-LAST:event_jtxtUnmuteKeyPressed

private void jbtnUnmuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUnmuteActionPerformed
    mp.setUnmuteCommand(jtxtUnmute.getText());
    jbtnUnmute.setEnabled(false);
    statusMessageLabel.setText("Unmute command value changed");
}//GEN-LAST:event_jbtnUnmuteActionPerformed

private void jtxtDelayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDelayActionPerformed
    jbtnDelay.doClick();
}//GEN-LAST:event_jtxtDelayActionPerformed

private void jtxtMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtMuteActionPerformed
    jbtnMute.doClick();
}//GEN-LAST:event_jtxtMuteActionPerformed

private void jtxtUnmuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtUnmuteActionPerformed
    jbtnUnmute.doClick();
}//GEN-LAST:event_jtxtUnmuteActionPerformed

private void jbtnSaveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveConfigActionPerformed
    try {
        MuteProcess.newConfig(MuteProcess.getName(),MuteProcess.getCurrentVars());
        statusMessageLabel.setText("Configuration file has been updated.");
    } catch (IOException e) {
        statusMessageLabel.setText(e.getMessage());
    }
}//GEN-LAST:event_jbtnSaveConfigActionPerformed

private void jmnuUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuUpdateActionPerformed
    jbtnCheckUpdate.doClick();
}//GEN-LAST:event_jmnuUpdateActionPerformed

private void jmnuQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuQuitActionPerformed
    jbtnExit.doClick();
}//GEN-LAST:event_jmnuQuitActionPerformed

private void jPanel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseEntered
    this.getFrame().getJMenuBar().getMenu(0).setPopupMenuVisible(false);
    this.getFrame().getJMenuBar().getMenu(0).setSelected(false);
    this.getFrame().getJMenuBar().getMenu(1).setPopupMenuVisible(false);
    this.getFrame().getJMenuBar().getMenu(1).setSelected(false);
}//GEN-LAST:event_jPanel1MouseEntered

private void jradBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jradBrowserActionPerformed
    if (jradBrowser.isSelected()&&MuteProcess.isDesktopApp()) {
        jradDesktop.setSelected(false);
        mp.setDesktopApp(false);
        try {
            mp.createScreensaver();
        } catch (Exception e){
            mp.writeLog(mp.getName(), "Could not create visual block: "+e.getMessage());
        } 
    }
}//GEN-LAST:event_jradBrowserActionPerformed

private void jradDesktopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jradDesktopActionPerformed
    if (jradDesktop.isSelected()&&!MuteProcess.isDesktopApp()) {
        jradBrowser.setSelected(false);
        mp.setDesktopApp(true);
        try {
            mp.createScreensaver();
        } catch (Exception e){
            mp.writeLog(mp.getName(), "Could not create visual block: "+e.getMessage());
        }
    }
}//GEN-LAST:event_jradDesktopActionPerformed

private void jmnuFAQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuFAQActionPerformed
    MuteProcess.visitFAQ();
}//GEN-LAST:event_jmnuFAQActionPerformed

private void loadValues() {
    bInitial = true;
    try {
        this.getFrame().setIconImage(ImageIO.read(getClass().getResource("/hulumutegui/resources/hulumute256.png")));
    } catch (Exception e) {
        MuteProcess.writeLog(MuteProcess.getName(), "Could not create frame icon from specified image file"+e.getMessage());
    }
    this.getFrame().setResizable(false);
    buttonGroup1.add(jradDesktop);
    buttonGroup1.add(jradBrowser);
    jradDesktop.setSelected(MuteProcess.isDesktopApp());
    jradBrowser.setSelected(!MuteProcess.isDesktopApp());
    jchckDebug.setSelected(MuteProcess.isDebug());
    jchckLog.setSelected(MuteProcess.isLog());
    jchckUpdate.setSelected(MuteProcess.isCheckUpdate());
    jchckVisuals.setSelected(MuteProcess.isBlockVisuals());
    jtxtDelay.setText(String.valueOf(MuteProcess.getDelay()));
    jtxtMute.setText(MuteProcess.getMuteCommand());
    //jtxtSelect.setText(MuteProcess.getSelectWindow());
    jtxtUnmute.setText(MuteProcess.getUnmuteCommand());
    statusMessageLabel.setText("");
    bInitial = false;
}
private void promptSave(){
    final JPanel panel = new JPanel();      //display save warning (used on exit)
    int result = JOptionPane.showConfirmDialog(panel, 
        "It appears you have made changes to the programs configuration.\n "
        + "          Would you like to save those changes before quitting? \n\n", "Save Changes?",
    JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION) {
        try {
            MuteProcess.newConfig(MuteProcess.getName(),MuteProcess.getCurrentVars());
        } catch (IOException e) {
            statusMessageLabel.setText(e.getMessage());
        }
    }
}
public static void promptUpdate() {
    final JPanel panel = new JPanel();      //display warning for new version available

    int result = JOptionPane.showConfirmDialog(panel, 
        "    There is a newer version of HuluMute available.\n "
        + "Would you like to go to the downloads page now? \n\n"
        + "             You can disable update checks via\n "
        + "the program window or by editing the conf file", "Update available",
    JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION) {
        MuteProcess.getNewVersion();
    }
}

public static void showResError() {
    final JPanel panel = new JPanel();      //display warning for new version available
    
    JOptionPane.showMessageDialog(panel, 
            "    HuluMute cannot mute Hulu's Desktop App \n "
            + "at this screen's resolution.   \n\n"
            + "Please change the resolution and try again.\n ", "Unsupported Resolution",
    JOptionPane.ERROR_MESSAGE);
    
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton jbtnCheckUpdate;
    private javax.swing.JButton jbtnDelay;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnLoadDefaults;
    private javax.swing.JButton jbtnMute;
    private javax.swing.JButton jbtnMuteToggle;
    private javax.swing.JButton jbtnSaveConfig;
    private javax.swing.JButton jbtnUnmute;
    private javax.swing.JCheckBox jchckAdvanced;
    private javax.swing.JCheckBox jchckDebug;
    private javax.swing.JCheckBox jchckLog;
    private javax.swing.JCheckBox jchckUpdate;
    private javax.swing.JCheckBox jchckVisuals;
    private javax.swing.JMenuItem jmnuFAQ;
    private javax.swing.JMenuItem jmnuQuit;
    private javax.swing.JMenuItem jmnuUpdate;
    private javax.swing.JRadioButton jradBrowser;
    private javax.swing.JRadioButton jradDesktop;
    private javax.swing.JTextField jtxtDelay;
    private javax.swing.JTextField jtxtMute;
    private javax.swing.JTextField jtxtUnmute;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
