package springexample;

/**
 * @author Yeison Melo {@literal <mailto:melo.yeison@gmail.com/>}
 *         Class to wrap the URLS of your backend so this example can connect
 *         to it and test your implementation
 */

public class BackendURLs {

    public static String clientToken;
    public static String createPaymentMethod;
    public static String createPayment;
    public static String settlePayment;
    public static String voidPayment;



    public BackendURLs(String clientToken, String createPaymentMethod, String createPayment, String settlePayment, String voidPayment) {
        this.clientToken = clientToken;
        this.createPaymentMethod = createPaymentMethod;
        this.createPayment = createPayment;
        this.settlePayment = settlePayment;
        this.voidPayment = voidPayment;
    }
}
