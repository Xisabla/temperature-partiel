package temperature;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
    public NumberAxis glacierX;
    public NumberAxis glacierY;

    /**
     * Initializes the application content
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set labels for the temperature graph
        tempX.setLabel("Date");
        tempX.setLabel("Evolution de la température");

        // Query data and fill temperature series
        plotTemperatures();

        // Set labels for the glacier graph
        glacierX.setLabel("Année");
        glacierY.setLabel("Evolution de la taille (%)");

        // Query data and fill glaciers series
        plotGlacier();

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

        // Get the file path
        String file = this.getClass().getResource("/temperature/data/temperature.txt").toString().replace("file:/","");
        String finalFile = Paths.get(file).toAbsolutePath().normalize().toString();

        // Query the file content
        List<List<String>> lines = FileReader.read(finalFile, 1);

        // Will be used to set bounds
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        // Read each line
        for(List<String> line : lines) {

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

        // Get the file path
        String file = this.getClass().getResource("/temperature/data/glaciers.txt").toString().replace("file:/","");
        String finalFile = Paths.get(file).toAbsolutePath().normalize().toString();

        // Query the file content
        List<List<String>> lines = FileReader.read(finalFile, 1);

        // Will be used to set bounds
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for(List<String> line : lines) {

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
        glacierX.setLowerBound(min);
        glacierX.setUpperBound(max);

        // Add the series to the graph
        glacierGraph.getData().add(sizes);

    }
}
