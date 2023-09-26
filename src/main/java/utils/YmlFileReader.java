package utils;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import utils.api.IYmlFileReader;

import java.io.*;
import java.util.Map;

/** Class for reading values from yml configuration file */
@Getter
@Setter
public class YmlFileReader implements IYmlFileReader {

    /** Path to configuration YML file */
    private String path = "src/main/resources/config.yml";

    /**
     * Reads YML file as Map and returns String with specified key. Prints message if IOException occurred
     *
     * @param key the key whose associated value is to be returned
     * @return String to which the specified key is mapped,
     * or null if this map contains no mapping for the key
     * or IOException occurred
     */
    @Override
    public String getStringValueFromFile(String key) {
        String data = null;

        try (InputStream inputStream = new FileInputStream(path)) {
            Yaml yaml = new Yaml();
            Map<String, Object> fileData = yaml.load(inputStream);
            data = String.valueOf(fileData.get(key));

        } catch (IOException e) {
            System.err.println("Cannot read YML file");
        }

        return data;
    }
}