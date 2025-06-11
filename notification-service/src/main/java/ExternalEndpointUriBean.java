
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ExternalEndpointUriBean {

    @ConfigProperty(name = "external.notification.url")
    String externalNotificationUrl;

    public String getExternalEndpointUri() {
        return externalNotificationUrl + "?bridgeEndpoint=true";
    }
}