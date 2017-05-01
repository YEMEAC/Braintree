package springexample;

import com.braintreegateway.BraintreeGateway;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * * Proocess File with our properties
 * Brain Tree and Backend URLs Factory
 **/
public class BTandBEURLsFactory {
    public static BraintreeGateway fromConfigMappingBT(Map<String, String> mapping) {
        return new BraintreeGateway(
                mapping.get("BT_ENVIRONMENT"),
                mapping.get("BT_MERCHANT_ID"),
                mapping.get("BT_PUBLIC_KEY"),
                mapping.get("BT_PRIVATE_KEY")
        );
    }

    public static BackendURLs fromConfigMappingURL(Map<String, String> mapping) {
        return new BackendURLs(
                mapping.get("URL_BACKEND_GET_CLIENT_TOKEN"),
                mapping.get("URL_BACKEND_CREATE_PAYMENT_METHOD"),
                mapping.get("URL_BACKEND_CREATE_PAYMENT"),
                mapping.get("URL_BACKEND_SETTLE"),
                mapping.get("URL_BACKEND_VOID")
        );
    }

    public static BraintreeGateway fromConfigFileBT(File configFile) {
        InputStream inputStream = null;
        Properties properties = new Properties();

        checkInput(inputStream, properties, configFile);

        return new BraintreeGateway(
                properties.getProperty("BT_ENVIRONMENT"),
                properties.getProperty("BT_MERCHANT_ID"),
                properties.getProperty("BT_PUBLIC_KEY"),
                properties.getProperty("BT_PRIVATE_KEY")
        );
    }

    public static BackendURLs fromConfigFileURL(File configFile) {
        InputStream inputStream = null;
        Properties properties = new Properties();

        checkInput(inputStream, properties, configFile);

        return new BackendURLs(
                properties.getProperty("URL_BACKEND_GET_CLIENT_TOKEN"),
                properties.getProperty("URL_BACKEND_CREATE_PAYMENT_METHOD"),
                properties.getProperty("URL_BACKEND_CREATE_PAYMENT"),
                properties.getProperty("URL_BACKEND_SETTLE"),
                properties.getProperty("URL_BACKEND_VOID")
        );
    }

    private static void checkInput(InputStream inputStream, Properties properties, File configFile) {
        try {
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println("Exception: " + e);
            }
        }
    }
}
