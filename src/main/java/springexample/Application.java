package springexample;

import com.braintreegateway.BraintreeGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {
    public static String DEFAULT_CONFIG_FILENAME = "config.properties";
    public static String DEFAULT_URL_CONFIG_FILENAME = "config.url.properties";
    public static BraintreeGateway gateway;
    public static BackendURLs backendURLs;
    public static String clientToken;

    public static void main(String[] args) {
        File configFile = new File(DEFAULT_CONFIG_FILENAME);
        File urlConfigFile = new File(DEFAULT_URL_CONFIG_FILENAME);
        try {
            if (configFile.exists() && !configFile.isDirectory()) {
                gateway = BTandBEURLsFactory.fromConfigFileBT(configFile);
                backendURLs = BTandBEURLsFactory.fromConfigFileURL(urlConfigFile);
            } else {
                gateway = BTandBEURLsFactory.fromConfigMappingBT(System.getenv());
                backendURLs = BTandBEURLsFactory.fromConfigMappingURL(System.getenv());
            }
            clientToken = gateway.clientToken().generate();
        } catch (NullPointerException e) {
            System.err.println("Could not load Braintree or URL configuration from config file or system environment.");
            System.exit(1);
        }

        SpringApplication.run(Application.class, args);
    }
}
