package temperature;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main application controller
 */
public class Controller implements Initializable {

    /**
     * Temperature graph
     */
    public LineChart<Double, Double> tempGraph;

    /**
     * Temperature graph axis (X and Y)
     */
    public NumberAxis tempX;
    public NumberAxis tempY;

    /**
     * Glacier graph
     */
    public LineChart<Double, Double> glacierGraph;

    /**
     * Glacier graph axis (X and Y)
     */
    public NumberAxis glacierYear;
    public NumberAxis glacierVariation;

    /**
     * Mean Temperature graph
     */
    public BarChart<String, Double> meanGraph;

    /**
     * Mean Temperature axis (X and Y)
     */
    public CategoryAxis meanX;
    public NumberAxis meanY;

    /**
     * Data queried from the temperature file
     */
    List<List<String>> temperatureData;
    /**
     * Data queried from the glacier file
     */
    List<List<String>> glacierData;

    /**
     * Initializes the application content
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Query all the needed data
        queryTemperatureData();
        queryGlacierData();

        // Set labels for the temperature graph
        tempX.setLabel("Temps");
        tempY.setLabel("Evolution de la température");

        // Query data and fill temperature series
        plotTemperatures();

        // Set labels for the glacier graph
        glacierYear.setLabel("Année");
        glacierVariation.setLabel("Evolution de la taille (%)");

        // Query data and fill glaciers series
        plotGlacier();

        // WIP
        meanX.setLabel("Décénie");
        meanY.setLabel("Variation moyenne");

        /*XYChart.Series<String, Double> a = new XYChart.Series<>();
        XYChart.Series<String, Double> b = new XYChart.Series<>();

        a.getData().add(new XYChart.Data<String, Double>("2000", 2.));
        a.getData().add(new XYChart.Data<String, Double>("2001", 2.));
        b.getData().add(new XYChart.Data<String, Double>("2000", 3.));
        b.getData().add(new XYChart.Data<String, Double>("2001", 4.));

        a.setName("A series");
        b.setName("B series");

        meanGraph.setData(FXCollections.observableArrayList(a, b));*/

    }

    /**
     * Query the temperature data from the temperature file
     */
    private void queryTemperatureData() {

        // Get the file path
        String file = this.getClass().getResource("/temperature/data/temperature.txt").toString().replace("file:/","");
        String finalFile = Paths.get(file).toAbsolutePath().normalize().toString();

        // Return the file data
        temperatureData = FileReader.read(finalFile, 1);

    }

    /**
     * Query the glacier data from the glacier file
     */
    private void queryGlacierData() {

        // Get the file path
        String file = this.getClass().getResource("/temperature/data/glaciers.txt").toString().replace("file:/","");
        String finalFile = Paths.get(file).toAbsolutePath().normalize().toString();

        // Return the file data
        glacierData = FileReader.read(finalFile, 1);

    }

    /**
     * Query data and update temperatures changes graph
     */
    private void plotTemperatures() {

        // GCAG Series
        final XYChart.Series<Double, Double> gcag = new XYChart.Series<>();
        gcag.setName("GCAG");

        // GISTEMP Series
        final XYChart.Series<Double, Double> gistemp = new XYChart.Series<>();
        gistemp.setName("GISTEMP");

        // Will be used to set bounds
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        // Read each line
        for(List<String> line : temperatureData) {

            // Check that the line has the good length
            if(line.size() == 3) {

                // Set the date double from the date string
                double date = Double.parseDouble(line.get(1).replace("-", ""));
                // Get the temperature from the data
                double temp = Double.parseDouble(line.get(2));

                // Compute new bounds
                if(date > max) max = date;
                if(date < min) min = date;

                // Append to GCAG series if it is a GCAG value
                if(line.get(0).equalsIgnoreCase("GCAG")) {
                    gcag.getData().add(new XYChart.Data<>(date, temp));
                }

                // or to GISTEMP series if it is a GISTEMP value
                if(line.get(0).equalsIgnoreCase("GISTEMP")) {
                    gistemp.getData().add(new XYChart.Data<>(date, Double.valueOf(line.get(2))));
                }
            }

        }

        // Set the bounds
        tempX.setLowerBound(min);
        tempX.setUpperBound(max);

        // Add the series
        tempGraph.getData().add(gcag);
        tempGraph.getData().add(gistemp);

    }

    /**
     * Query data and update glaciers changes graph
     */
    private void plotGlacier() {

        // Main series
        final XYChart.Series<Double, Double> sizes = new XYChart.Series<>();
        sizes.setName("Variation");

        // Will be used to set bounds
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for(List<String> line : glacierData) {

            // Check that the line has the good minimal length
            if(line.size() >= 2) {

                // Get the usable data
                double year = Double.parseDouble(line.get(0));
                double size = Double.parseDouble(line.get(1));

                // Compute new bounds
                if(year > max) max = year;
                if(year < min) min = year;

                // Add data to the series
                sizes.getData().add(new XYChart.Data<>(year, size));
            }

        }

        // Set the bounds
        glacierYear.setLowerBound(min);
        glacierYear.setUpperBound(max);

        // Add the series to the graph
        glacierGraph.getData().add(sizes);

    }
}
