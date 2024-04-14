package kz.baltabayev.paypal.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for PayPal API context.
 */
@Configuration
public class PaypalConfig {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    /**
     * Creates a new APIContext bean.
     *
     * @return a new instance of APIContext configured with the client ID, client secret, and mode from the application properties
     */
    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}