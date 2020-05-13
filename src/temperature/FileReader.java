package temperature;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FileReade util class
 */
public class FileReader {

    /**
     * Query the data splitted by "," char from a given filepath
     * @param file Path to the file
     * @param jump Number of lines to ignore at the start
     * @return List of lines, lines are list of elements seperated by a "," from the file
     */
    public static List<List<String>> read(String file, int jump) {

        return read(new File(file), jump);

    }

    /**
     * Query the data splitted by "," char from a given file
     * @param file File in which is the data
     * @param jump Number of lines to ignore at the start
     * @return List of lines, lines are list of elements seperated by a "," from the file
     */
    public static List<List<String>> read(File file, int jump) {

        List<List<String>> lines = new ArrayList<>();

        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(file));
            int l = 0;

            // Read each line
            while ((line = bufferedReader.readLine()) != null) {

                l++;

                // Skip if is a jumped line
                if(l > jump) {

                    // Otherwise push splitted content
                    List<String> dataString = new ArrayList<>(Arrays.asList(line.split(",")));
                    lines.add(dataString);

                }


            }

        } catch (IOException e) {
            // Print error trace in console
            e.printStackTrace();
        }

        return lines;

    }

}
