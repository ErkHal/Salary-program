package SalaryProgram;

import CSVUtils.CSVUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.jnlp.FileSaveService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erkki Halinen
 * Controller class for Salary Program application.
 * Controls the UI for the user to select a file and save the freshly converted payment list.
 */

public class ChooseFileController {

    private File hourList = null;
    private FileChooser chooser;
    private CSVUtils parser;
    private ArrayList<String[]> parsedFile;

    @FXML
    private Button saveButton;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button convertButton;

    @FXML
    private Text selectedFilepath;

    @FXML
    private Text saveFilepath;

    @FXML
    private Text message;

    /**
     * Exits the program when clicking the Quit button.
     * @param event
     */
    public void exitProgram(ActionEvent event) {

        System.exit(1);
    }

    /**
     * Opens up the file choosing window.
     * After the file has been successfully selected, allows user to press the OK button.
     * @param event
     */
    public void selectFile(ActionEvent event) {

        chooser = new FileChooser();
        hourList = chooser.showOpenDialog(null);

        if(hourList != null) {
            message.setText("");
            selectedFilepath.setText(hourList.getAbsolutePath());
            convertButton.setDisable(false);

        } else {
            convertButton.setDisable(true);
            saveButton.setDisable(true);
            selectedFilepath.setText("File not selected !");
        }
    }

    /**
     * Parses the workshift list and prompts the user to save the freshly converted .CSV file
     */
    public void parseAndCalculateWorkShiftList(ActionEvent event) {

        parser = new CSVUtils();

        try {
            parsedFile = parser.parseFile(hourList, true, ',');
            message.setText("File converted !");
            saveButton.setDisable(false);

        } catch(Exception f) {
            message.setText("There seems to be a problem with this file, try again or try another file !");
        }
    }

    /**
     * Saves the converted CSV File into a new CSV file
     * @param event
     */
    public void saveNewFile(ActionEvent event) {

        //Set extension filter
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        chooser.getExtensionFilters().add(filter);
        //Open dialog to choose save location
        File calculatedShifts = chooser.showSaveDialog(null);

        if(calculatedShifts != null) {
            try {
                saveFilepath.setText(calculatedShifts.getAbsolutePath());
                parser.intoCSVFile(calculatedShifts.getPath(), parsedFile);
                message.setText("Saved " + calculatedShifts.getName() + " successfully !");
            } catch(IOException io) {
                message.setText("Cannot save file ! There might exist a file with the same name, try another ! ");
            }
        }
    }
}
