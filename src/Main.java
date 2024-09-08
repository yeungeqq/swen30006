import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/testShort.properties";

    public static Properties loadPropertiesFile(String propertiesFile) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("Properties file = " + propertiesFile);
        try (FileInputStream input = new FileInputStream(propertiesFile)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
            
        }
        return null;
    }

    public static void main(String[] args) {
        String propertiesPath = (args.length > 0) ? args[0] : DEFAULT_PROPERTIES_PATH;
        final Properties properties = loadPropertiesFile(propertiesPath);
        new Simulation(properties).run();
    }
}