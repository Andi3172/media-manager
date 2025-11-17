# Java Multimedia File Manager

A Java desktop application for managing multimedia files (`.mp3`, `.wav`, `.jpg`, `.png`) from local directories.

This project uses Java Swing for a cross-platform graphical user interface (GUI) and `java.nio.file` for efficient, recursive file scanning. The application persists monitored locations to a `location.txt` file on exit and reloads them on startup.

## Tech Stack

* **Core:** Java 8+
* **GUI:** Java Swing (`javax.swing`)
* **File I/O:** `java.nio.file` (Path, Files) & `java.io` (BufferedReader/Writer)
* **Concurrency:** `java.util.concurrent` (ExecutorService for background scanning)

---

## How to Run

This project is a standard Java desktop application designed to be compiled and run from the terminal.

### 1. Open Your Terminal
Navigate to the project's root folder (the directory containing this `README.md` file and the `src` folder).

### 2. Create 'bin' Directory
Create a `bin` directory to store the compiled `.class` files. This keeps the source directory clean.

```bash
mkdir bin
```

## Compile the Project

COmpile all the source files from the `src` directory into the `bin` directory.

```
javac -cp src -d bin src/project/MainApp.java src/project/gui/MainFrame.java src/project/models/MediaFile.java src/project/services/FileScanner.java src/project/services/LocationService.java src/project/exceptions/InvalidLocationException.java
```
## Run the Application

Run the app bu specifying the `bin` folder as your classpath and calling the main class.

```
java -cp bin project.MainApp
```
The GUI window will launch.

## Application Usage
### 1. Add Location
Click the **"Add Location"** button to open a directory chooser.
### 2.Scan & View Files
Select a folder. The app will automatically scan it (and all subfolders) and display the found files (`.jpg`, `.png`, `.mp3`, `.waw`) in the **"Files found"** list on the right.
### 3. Refresh
Click **"Refresh Files"** to manually trigger a re-scan of all monitored locations.
### 4. Remove Locations
Select a location from the list on the left and click **"Remove Location"** To stop monitoring it.
### 5. Save & Exit
Simply close the window. The application automatically saves your list of locations to `locations.txt` and will restore them the next time you open the app.


