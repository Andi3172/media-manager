package project.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import project.models.MediaFile;
import project.services.FileScanner;
import project.services.LocationService;
import project.exceptions.InvalidLocationException;


public class MainFrame extends JFrame{
    ///logic services
    private final LocationService locationService;
    private final FileScanner fileScanner;

    ///JList models
    private final DefaultListModel<String> locationListModel;
    private final DefaultListModel<String> filesListModel;

    //GUI components
    private JList<String> locationList;
    private JList<String> filesList;
    private JButton btnAddLocation;
    private JButton btnRemoveLocation;
    private JButton btnRefreshFiles;
    private JLabel lblStatus;

    //vectors
    private final String[] supportedFileTypes = {".mp3", ".wav", ".jpg", ".png"};
    private int[] fileTypeCounts;

    private final ExecutorService scanExecutor;

    public MainFrame(){
        this.locationService = new LocationService("location.txt");
        this.fileScanner = new FileScanner(supportedFileTypes);
        this.locationListModel = new DefaultListModel<>();
        this.filesListModel = new DefaultListModel<>();
        this.fileTypeCounts = new int[supportedFileTypes.length];
        this.scanExecutor = Executors.newSingleThreadExecutor();

        initGui();
        loadInitialData();
        setupWindowListeners();
    }


    private void initGui(){
        setTitle("Media Manager");
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 20));

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            createLocationPanel(),
            createFilesPanel()
        );
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblStatus = new JLabel("Done");
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusPanel.add(lblStatus);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private Component createLocationPanel(){
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 5));
        panel.add(new JLabel("Monitored Locations:"), BorderLayout.NORTH);

        locationList = new JList<>(locationListModel);
        locationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(locationList), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAddLocation = new JButton("Add Location");
        btnRemoveLocation = new JButton("Remove Location");
        buttonsPanel.add(btnAddLocation);
        buttonsPanel.add(btnRemoveLocation);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        btnAddLocation.addActionListener(e -> addLocationAction());
        btnRemoveLocation.addActionListener(e -> removeLocationAction());
    
        return panel;
    }


    private Component createFilesPanel(){
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 5, 10, 10));
        panel.add(new JLabel("Files found:"), BorderLayout.NORTH);

        filesList = new JList<>(filesListModel);
        panel.add(new JScrollPane(filesList), BorderLayout.CENTER);

        // Buton de reîmprospătare
        btnRefreshFiles = new JButton("Refresh Files");
        panel.add(btnRefreshFiles, BorderLayout.SOUTH);

        btnRefreshFiles.addActionListener(e -> refreshFilesAction());

        return panel;
    }

    private void setupWindowListeners(){
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                lblStatus.setText("Saving locations...");
                try{
                    locationService.saveLocations();
                    System.out.println("Location saved successfully.");
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Error saving locations: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                scanExecutor.shutdown();
                dispose();
                System.exit(0);
            }
        });
    }

    private void loadInitialData(){
        lblStatus.setText("Loading locations...");
        try{
            locationService.loadLocations();
            updateLocationListModel();
            lblStatus.setText("Locations loaded");
            refreshFilesAction();
        } catch(InvalidLocationException e){
            lblStatus.setText("Error loading: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Invalid location found in config: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        } catch(Exception e){
            lblStatus.setText("Error loading locations: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Error loading locations: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addLocationAction(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select a folder to be monitored");

        int result = chooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){
            Path selectedPath = chooser.getSelectedFile().toPath();
            try{
                locationService.addLocation(selectedPath);
                updateLocationListModel();
                lblStatus.setText("Location added: " + selectedPath);
                refreshFilesAction();
            } catch(InvalidLocationException e){
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid location: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void removeLocationAction(){
        int selectedIndex = locationList.getSelectedIndex();
        if(selectedIndex == -1){
            JOptionPane.showMessageDialog(this, "Please select a location to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedLocationStr = locationListModel.getElementAt(selectedIndex);
        locationService.removeLocation(selectedLocationStr);
        updateLocationListModel();
        lblStatus.setText("Location removed: " + selectedLocationStr);
        refreshFilesAction();
    }

    private void refreshFilesAction(){
        lblStatus.setText("Scanning files...");
        btnRefreshFiles.setEnabled(false);
        filesListModel.clear();

        scanExecutor.submit(() -> {
            try{
                List<MediaFile> foundFiles = fileScanner.scanLocations(locationService.getMonitoredLocations());
            
                SwingUtilities.invokeLater(() -> {
                    for(MediaFile file : foundFiles){
                        filesListModel.addElement(file.getName() + " (" + file.getPath().getParent() + ")");
                    }
                    generateReport(foundFiles);
                    lblStatus.setText("Scan complete. " + foundFiles.size() + " files found.");
                });
            } catch (Exception e){
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Error scanning.");
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Error scanning files: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            } finally {
                SwingUtilities.invokeLater(() -> btnRefreshFiles.setEnabled(true));
            }
        });
    }

    private void updateLocationListModel(){
        locationListModel.clear();
        for (Path path : locationService.getMonitoredLocations()){
            locationListModel.addElement(path.toString());
        }
    }

    private void generateReport(List<MediaFile> files){
        for (int i = 0; i< supportedFileTypes.length; i++){
            fileTypeCounts[i] = 0;
        }

        for(MediaFile file : files){
            for(int i = 0; i< supportedFileTypes.length; i++){
                if(file.getName().endsWith(supportedFileTypes[i])){
                    fileTypeCounts[i]++;
                    break;
                }
            }
        }

        System.out.println("----- Scan Report -----");
        System.out.println("Total found files: " + files.size());
        for(int i = 0; i< supportedFileTypes.length; i++){
            System.out.println(supportedFileTypes[i] + ": " + fileTypeCounts[i]);
        }
        System.out.println("-----------------------");
    }

    
}
