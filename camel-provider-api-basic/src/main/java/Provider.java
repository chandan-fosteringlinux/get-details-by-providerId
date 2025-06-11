import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Provider {

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "partnerId must be alphanumeric (3-20 chars)")
    private String partnerId;

    @NotNull
    @Pattern(regexp = "^[A-Za-z ]{3,50}$", message = "Name must be alphabetic (3-50 chars)")
    private String name;

    @NotNull
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    private String contactInfo;

    @Size(min = 1, message = "At least one supported channel required")
    private List<
        @Pattern(regexp = "^(SMS|WhatsApp|Email|IVR)$", message = "Supported channel must be one of: SMS, WhatsApp, Email, IVR")
        String> supportedChannels;

    @Valid
    private SLA sla;
}






















// import java.util.List;

// import lombok.Data;

// @Data
// public class Provider {
//     private String partnerId;
//     private String name;
//     private String contactInfo;
//     private List<String> supportedChannels;
//     private SLA sla;
// }
















// public class Provider {
//     private int id;
//     private String name;
//     private String speciality;

//     // Getters & setters (required for Camel/Jackson to work)
//     public int getId() { return id; }
//     public void setId(int id) { this.id = id; }

//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }

//     public String getSpeciality() { return speciality; }
//     public void setSpeciality(String speciality) { this.speciality = speciality; }
// }
