import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SLA {

    @Min(value = 1, message = "deliveryTimeMs must be positive")
    private long deliveryTimeMs;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    private double uptimePercent;
}









// import lombok.Data;

// @Data
// public class SLA {
//     private int deliveryTimeMs;
//     private double uptimePercent;
// }
