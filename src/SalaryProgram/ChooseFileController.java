package SalaryProgram;

import CSVUtils.CSVUtils;
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

public class ChooseFileController {

    private File hourList = null;
    private FileChooser chooser;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button okayButton;

    @FXML
    private Text filepath;

    /**
     * Opens up the file choosing window.
     * After the file has been successfully selected, allows user to press the OK button.
     * @param Event
     */
    public void selectFile(ActionEvent Event) {

        chooser = new FileChooser();
        hourList = chooser.showOpenDialog(null);

        if(hourList != null) {
            filepath.setText(hourList.getAbsolutePath());
            okayButton.setDisable(false);

        } else {
            okayButton.setDisable(true);
            filepath.setText("File not selected !");
        }
    }

    /**
     * Parses the workshift list and prompts the user to save the freshly converted .CSV file
     */
    public void parseAndCalculateWorkShiftList() {

        CSVUtils parser = new CSVUtils();

        ArrayList<String[]> parsedFile = null;

        try {
            parsedFile = parser.parseFile(hourList, true, ',');

            //Set extension filter
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            chooser.getExtensionFilters().add(filter);
            File calculatedShifts = chooser.showSaveDialog(null);

            if(calculatedShifts != null) {
                try {
                    parser.intoCSVFile(calculatedShifts.getPath(), parsedFile);
                } catch(IOException io) {
                    System.out.println("Cannot save file !");
                }
            }
        } catch(FileNotFoundException f) {
            filepath.setText("There seems to be a problem with this file, try again or try another file !");
        }
    }
}
