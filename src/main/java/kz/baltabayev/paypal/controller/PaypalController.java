package kz.baltabayev.paypal.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import kz.baltabayev.paypal.paypal.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for handling PayPal payments.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;

    @Value("${url.cansel}")
    private String cancelURL;
    @Value("${url.success}")
    private String successURL;

    /**
     * Endpoint for the home page.
     *
     * @return the name of the home page view
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Endpoint for creating a payment.
     *
     * @param method the payment method
     * @param amount the payment amount
     * @param currency the currency of the payment
     * @param description the description of the payment
     * @return a RedirectView to the approval URL if successful, or to the error page if an exception occurred
     */
    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description

    ) {
        try {
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelURL,
                    successURL
            );

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error("Error ocured: ", e);
        }

        return new RedirectView("/payment/error");
    }

    /**
     * Endpoint for handling successful payments.
     *
     * @param paymentId the ID of the payment
     * @param payerId the ID of the payer
     * @return the name of the success view if the payment was approved, or the error view if an exception occurred
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return "payment-success";
            }
        } catch (PayPalRESTException e) {
            log.error("Error ocured: ", e);
            return "payment-error";
        }

        return "payment-success";
    }

    /**
     * Endpoint for handling cancelled payments.
     *
     * @return the name of the cancel view
     */
    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "payment-cancel";
    }

    /**
     * Endpoint for handling payment errors.
     *
     * @return the name of the error view
     */
    @GetMapping("/payment/error")
    public String paymentError() {
        return "payment-error";
    }
}