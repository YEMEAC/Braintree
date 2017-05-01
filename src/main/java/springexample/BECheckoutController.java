package springexample;

import com.braintreegateway.BraintreeGateway;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static springexample.BackendURLs.*;

/**
 * @author Yeison Melo {@literal <mailto:melo.yeison@gmail.com/>}
 *         BackEnd Checkout Controller
 *         This controller works agains your backend to do all the transactions with braintree.
 *         This is usefull to test that your backedn integration is working with braintree but you
 *         dont have  to spend a lot of time developing  the frondend in order to  get a real nonce
 *         token  to make a real transaction. You only have to substitute the config.url.properties under
 *         the root of the project with the urls of your backend. You also should substitute the parameters
 *         my backend service was expecting for yours.
 */

@Controller
public class BECheckoutController {

    private BraintreeGateway gateway = Application.gateway;

    @RequestMapping(value = {"/be", "/be/checkouts", "/be/new"}, method = RequestMethod.GET)
    public String rootBE(Model model) {
        model.addAttribute("clientToken", getClientToken());
        return "checkouts/new";
    }

    @RequestMapping(value = {"/be/reserve"}, method = RequestMethod.GET)
    public String reserveIniBE(Model model) {
        model.addAttribute("clientToken", getClientToken());
        return "checkouts/reserve";
    }

    @RequestMapping(value = {"/be/void"}, method = RequestMethod.GET)
    public String voidIniBE(Model model) {
        return "checkouts/void";
    }

    @RequestMapping(value = {"/be/settle"}, method = RequestMethod.GET)
    public String settleIniBE(Model model) {
        return "checkouts/settle";
    }

    @RequestMapping(value = "/be/createCreditCardPaymentMethod", method = RequestMethod.POST)
    public String getFormBE(@RequestParam("payment_method_nonce") String nonce, @RequestParam("default") boolean isDefault,
                          @RequestParam("deviceData") String deviceData, @RequestParam("userId") Long userId, @RequestParam("label") String label,
                          @RequestParam("email") String email, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
                          @RequestParam("paymentProvider") String paymentProvider, Model model, final RedirectAttributes redirectAttributes) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(createPaymentMethod);

            JSONObject json = new JSONObject();
            json.put("default", isDefault).put("deviceData", deviceData).put("Email", email).put("firstName", firstName)
                    .put("label", label).put("lastName", lastName).put("paymentProvider", paymentProvider)
                    .put("token", nonce).put("userId", userId);

            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = new JSONObject(reader.readLine().toString());

            model.addAttribute("isSuccess", true).addAttribute("paymentmethod", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "checkouts/show";
    }

    @RequestMapping(value = "/be/createCreditCardPayment", method = RequestMethod.POST)
    public String getFormBE(@RequestParam("amount") String amount, @RequestParam("brand") String brand,
                          @RequestParam("currency") String currency, @RequestParam("deviceData") String deviceData, @RequestParam("dynamicDescriptor") String dynamicDescriptor,
                          @RequestParam("settle") boolean settle, @RequestParam("paymentMethodId") Long paymentMethodId, @RequestParam("userId") Long userId,
                          @RequestParam("paymentProvider") String paymentProvider, Model model, final RedirectAttributes redirectAttributes) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
            HttpPost request = new HttpPost(createPayment);

            JSONObject json = new JSONObject();
            json.put("currency", currency).put("deviceData", deviceData).put("settle", settle).put("paymentMethodId", paymentMethodId)
                    .put("brand", brand).put("paymentProvider", paymentProvider)
                    .put("dynamicDescriptor", dynamicDescriptor).put("userId", userId).put("amount", amount);

            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = new JSONObject(reader.readLine().toString());

            if (json.isNull("message")) {
                model.addAttribute("isSuccess", true).addAttribute("paymentId", json.get("paymentId"))
                        .addAttribute("userId", userId).addAttribute("amount", amount);
            } else {
                model.addAttribute("isSuccess", false).addAttribute("message", json.get("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "checkouts/show";
    }

    @RequestMapping(value = "/be/settleCreditCardPayment", method = RequestMethod.POST)
    public String getFormBE(@RequestParam("amount") String amount, @RequestParam("paymentId") Long paymentId, Model model, final RedirectAttributes redirectAttributes) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
            HttpPost request = new HttpPost(settlePayment);

            JSONObject json = new JSONObject();
            json.put("amount", amount).put("paymentId", paymentId);

            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = new JSONObject(reader.readLine().toString());

            if (json.isNull("message")) {
                model.addAttribute("isSuccess", true).addAttribute("paymentSettledId", json.get("paymentId"));
            } else {
                model.addAttribute("isSuccess", false).addAttribute("message", json.get("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "checkouts/show";
    }

    @RequestMapping(value = "/be/voidCreditCardPayment", method = RequestMethod.POST)
    public String getFormBE(@RequestParam("paymentId") Long paymentId, Model model, final RedirectAttributes redirectAttributes) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
            HttpPost request = new HttpPost(voidPayment);

            JSONObject json = new JSONObject();
            json.put("paymentId", paymentId);

            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = new JSONObject(reader.readLine().toString());

            if (json.isNull("message")) {
                model.addAttribute("isSuccess", true).addAttribute("paymentVoidedId", json.get("paymentId"));
            } else {
                model.addAttribute("isSuccess", false).addAttribute("message", json.get("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "checkouts/show";
    }

    private Object getClientToken() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
            HttpPost request = new HttpPost(clientToken);

            JSONObject json = new JSONObject();
            json.put("brand", "brand");
            json.put("paymentProvider", "Braintree");

            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = new JSONObject(reader.readLine().toString());
            return json.get("clientToken");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

