package kz.baltabayev.paypal.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service for handling PayPal payments.
 */
@Service
@RequiredArgsConstructor
public class PaypalService {

    private final APIContext apiContext;

    /**
     * Creates a payment.
     *
     * @param total the total amount of the payment
     * @param currency the currency of the payment
     * @param method the payment method
     * @param intent the payment intent
     * @param description the description of the payment
     * @param cancelUrl the URL to redirect to if the payment is cancelled
     * @param succesUrl the URL to redirect to if the payment is successful
     * @return the created Payment
     * @throws PayPalRESTException if an error occurs while creating the payment
     */
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String succesUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(succesUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    /**
     * Executes a payment.
     *
     * @param paymentId the ID of the payment to execute
     * @param payerId the ID of the payer
     * @return the executed Payment
     * @throws PayPalRESTException if an error occurs while executing the payment
     */
    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
}