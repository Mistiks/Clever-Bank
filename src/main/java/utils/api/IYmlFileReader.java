package utils.api;

/** Interface for reading String from YML files*/
public interface IYmlFileReader {

    /**
     * Reads YML file as Map and returns String with specified key
     *
     * @param key the key whose associated value is to be returned
     * @return String to which the specified key is mapped,
     * or null if this map contains no mapping for the key
     */
    String getStringValueFromFile(String key);
}
