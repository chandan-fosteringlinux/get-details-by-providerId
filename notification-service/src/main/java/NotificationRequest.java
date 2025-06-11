
import lombok.Data;

@Data
public class NotificationRequest {
    private Filter filter;
    private Auth auth;
}