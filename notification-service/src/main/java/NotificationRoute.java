
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRoute extends RouteBuilder {

    @ConfigProperty(name = "auth.appId")
    String appId;

    @ConfigProperty(name = "auth.accountId")
    String accountId;

    @ConfigProperty(name = "auth.apiKey")
    String apiKey;

    // Inject full external API URL with bridgeEndpoint param
    // @ConfigProperty(name = "external.notification.url")
    // String externalNotificationUrl;

    @Override
    public void configure() throws Exception {

        // Error handling
        onException(Exception.class)
            .handled(true)
            .log("Error occurred: ${exception.message}")
            .setBody(simple("{\"status\": \"failed\", \"reason\": \"${exception.message}\"}"))
            .setHeader("Content-Type", constant("application/json"));

        restConfiguration()
            .bindingMode(RestBindingMode.json);

        rest("/notify")
            .post()
            .consumes("application/json")
            .produces("application/json")
            .type(Filter.class)
            .to("direct:callNotification");

        from("direct:callNotification")
            .routeId("call-notification-service")
            .log("Received filter: ${body}")
            .process(exchange -> {
                Filter filter = exchange.getIn().getBody(Filter.class);

                Map<String, Object> payload = new HashMap<>();
                payload.put("filter", filter);

                Map<String, String> auth = new HashMap<>();
                auth.put("appId", appId);
                auth.put("accountId", accountId);
                auth.put("apiKey", apiKey);

                payload.put("auth", auth);

                exchange.getIn().setBody(payload);
            })
            .marshal().json()
            .log("Final payload to external API: ${body}")
            .setHeader("Content-Type", constant("application/json"))
            .toD("{{external.notification.url}}")  // <--- Directly from properties file
            .log("Response from external API: ${body}");
    }
}
