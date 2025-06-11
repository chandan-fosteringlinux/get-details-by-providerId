import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProviderRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(org.apache.camel.ValidationException.class)
        .handled(true)
        .setHeader(org.apache.camel.Exchange.HTTP_RESPONSE_CODE, constant(400))
        .setHeader(org.apache.camel.Exchange.CONTENT_TYPE, constant("application/json"))
        .setBody().simple("{\"error\": \"Validation failed: ${exception.message}\"}");



        restConfiguration()
            .contextPath("/")
            .port(8080)
            .bindingMode(org.apache.camel.model.rest.RestBindingMode.json);

        rest("/providers")
            .post()
                .consumes("application/json")
                .type(Provider.class)
                .to("direct:saveProvider")
            .get()
                .produces("application/json")
                .to("direct:getAllProviders");


                from("direct:saveProvider")
    .routeId("save-provider-route")
    .to("bean-validator://x") // This triggers validation using annotations
    .log("Validation passed, proceeding with DB insert")
    .log("Saving provider with name: ${body.name}")
    .process(exchange -> {
        Provider provider = exchange.getIn().getBody(Provider.class);

        // Insert into providers table
        exchange.getContext().createProducerTemplate().sendBody("jdbc:camel",
            "INSERT INTO providers (partner_id, name, contact_info) VALUES ('" +
                provider.getPartnerId() + "', '" +
                provider.getName() + "', '" +
                provider.getContactInfo() + "')");

        // Get generated ID
        List<Map<String, Object>> idResult = exchange.getContext().createProducerTemplate()
            .requestBody("jdbc:camel", "SELECT MAX(id) AS id FROM providers", List.class);
        Long providerId = ((Number) idResult.get(0).get("id")).longValue();

        // Insert supportedChannels only if not null
        if (provider.getSupportedChannels() != null) {
            for (String channel : provider.getSupportedChannels()) {
                exchange.getContext().createProducerTemplate().sendBody("jdbc:camel",
                    "INSERT INTO provider_supported_channels (provider_id, channel) VALUES (" +
                        providerId + ", '" + channel + "')");
            }
        }

        // Insert SLA if present
        if (provider.getSla() != null) {
            exchange.getContext().createProducerTemplate().sendBody("jdbc:camel",
                "INSERT INTO sla (provider_id, delivery_time_ms, uptime_percent) VALUES (" +
                    providerId + ", " +
                    provider.getSla().getDeliveryTimeMs() + ", " +
                    provider.getSla().getUptimePercent() + ")");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("providerId", providerId);
        exchange.getMessage().setBody(response);
    });

    from("direct:getAllProviders")
    .routeId("get-all-providers-complete")
    .process(exchange -> {
        List<Map<String, Object>> providers = exchange.getContext()
            .createProducerTemplate()
            .requestBody("jdbc:camel", "SELECT * FROM providers", List.class);

        for (Map<String, Object> provider : providers) {
            Long providerId = ((Number) provider.get("id")).longValue();

            // Get supported channels
            List<Map<String, Object>> channels = exchange.getContext()
                .createProducerTemplate()
                .requestBody("jdbc:camel",
                    "SELECT channel FROM provider_supported_channels WHERE provider_id = " + providerId,
                    List.class);

            List<String> supportedChannels = channels.stream()
                .map(c -> (String) c.get("channel"))
                .toList();

            provider.put("supportedChannels", supportedChannels);

            // Get SLA
            List<Map<String, Object>> slaList = exchange.getContext()
                .createProducerTemplate()
                .requestBody("jdbc:camel",
                    "SELECT delivery_time_ms, uptime_percent FROM sla WHERE provider_id = " + providerId,
                    List.class);

            if (!slaList.isEmpty()) {
                Map<String, Object> sla = slaList.get(0);
                Map<String, Object> slaMap = new HashMap<>();
                slaMap.put("deliveryTimeMs", sla.get("delivery_time_ms"));
                slaMap.put("uptimePercent", sla.get("uptime_percent"));
                provider.put("sla", slaMap);
            }
        }

        exchange.getMessage().setBody(providers);
    });





        // from("direct:saveProvider")
        //     .routeId("save-provider-route")
        //     .log("Saving provider with name: ${body.name}")
        //     .process(exchange -> {
        //         Provider provider = exchange.getIn().getBody(Provider.class);

        //         // Insert into providers table
        //         exchange.getContext().createProducerTemplate().sendBody("jdbc:camel",
        //             "INSERT INTO providers (partner_id, name, contact_info) VALUES ('" +
        //                 provider.getPartnerId() + "', '" +
        //                 provider.getName() + "', '" +
        //                 provider.getContactInfo() + "')");

        //         // Get generated ID (you may need a SELECT MAX or RETURNING strategy depending on DB)
        //         List<Map<String, Object>> idResult = exchange.getContext().createProducerTemplate()
        //             .requestBody("jdbc:camel", "SELECT MAX(id) AS id FROM providers", List.class);
        //         Long providerId = (Long) idResult.get(0).get("id");

        //         // Insert supportedChannels
        //         for (String channel : provider.getSupportedChannels()) {
        //             exchange.getContext().createProducerTemplate().sendBody("jdbc:camel",
        //                 "INSERT INTO provider_supported_channels (provider_id, channel) VALUES (" +
        //                     providerId + ", '" + channel + "')");
        //         }

        //         // Insert SLA
        //         if (provider.getSla() != null) {
        //             exchange.getContext().createProducerTemplate().sendBody("jdbc:camel",
        //                 "INSERT INTO sla (provider_id, delivery_time_ms, uptime_percent) VALUES (" +
        //                     providerId + ", " +
        //                     provider.getSla().getDeliveryTimeMs() + ", " +
        //                     provider.getSla().getUptimePercent() + ")");
        //         }

        //         // Response
        //         Map<String, Object> response = new HashMap<>();
        //         response.put("status", "success");
        //         response.put("providerId", providerId);
        //         exchange.getMessage().setBody(response);
        //     });

        // from("direct:getAllProviders")
        //     .routeId("get-all-providers")
        //     .setBody(constant("SELECT * FROM providers"))
        //     .to("jdbc:camel")
        //     .process(exchange -> {
        //         List<Map<String, Object>> result = exchange.getIn().getBody(List.class);
        //         exchange.getMessage().setBody(result);
        //     });

    //     from("direct:getAllProviders")
    // .routeId("get-all-providers-complete")
    // .process(exchange -> {
    //     List<Map<String, Object>> providers = exchange.getContext()
    //         .createProducerTemplate()
    //         .requestBody("jdbc:camel", "SELECT * FROM providers", List.class);

    //     for (Map<String, Object> provider : providers) {
    //         Long providerId = ((Number) provider.get("id")).longValue();

    //         // Get supported channels
    //         List<Map<String, Object>> channels = exchange.getContext()
    //             .createProducerTemplate()
    //             .requestBody("jdbc:camel",
    //                 "SELECT channel FROM provider_supported_channels WHERE provider_id = " + providerId,
    //                 List.class);

    //         List<String> supportedChannels = channels.stream()
    //             .map(c -> (String) c.get("channel"))
    //             .toList();

    //         provider.put("supportedChannels", supportedChannels);

    //         // Get SLA
    //         List<Map<String, Object>> slaList = exchange.getContext()
    //             .createProducerTemplate()
    //             .requestBody("jdbc:camel",
    //                 "SELECT delivery_time_ms, uptime_percent FROM sla WHERE provider_id = " + providerId,
    //                 List.class);

    //         if (!slaList.isEmpty()) {
    //             Map<String, Object> sla = slaList.get(0);
    //             Map<String, Object> slaMap = new HashMap<>();
    //             slaMap.put("deliveryTimeMs", sla.get("delivery_time_ms"));
    //             slaMap.put("uptimePercent", sla.get("uptime_percent"));
    //             provider.put("sla", slaMap);
    //         }
    //     }

    //     exchange.getMessage().setBody(providers);
    // });

    }
}






























// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.apache.camel.builder.RouteBuilder;

// import jakarta.enterprise.context.ApplicationScoped;

// @ApplicationScoped
// public class ProviderRoute extends RouteBuilder {

//     @Override
//     public void configure() throws Exception {

//         // Enable REST DSL
//         restConfiguration()
//             .contextPath("/") // base URL is localhost:8080/
//             .port(8080)
//             .bindingMode(org.apache.camel.model.rest.RestBindingMode.json); // auto convert JSON to POJO
// rest("/providers")
//     .post()
//     .consumes("application/json")
//     .type(Provider.class) // Automatically map JSON to Provider POJO
//     .to("direct:logProvider")
//     .get()
//         .produces("application/json")
//         .to("direct:getAllProviders");
    

// from("direct:logProvider")
//     .routeId("log-incoming-provider-fields")
//     .log("Received Provider object:")
//     .log("  ID: ${body.id}")
//     .log("  Name: ${body.name}")
//     .log("  Speciality: ${body.speciality}")
//     .setBody(simple("INSERT INTO testDB2 (id, name, speciality) VALUES (${body.id}, '${body.name}', '${body.speciality}')"))
//     .to("jdbc:camel")
//     .process(exchange -> {
//         Map<String, Object> response = new HashMap<>();
//         response.put("status", "success");
//         response.put("message", "Provider received");
//         exchange.getMessage().setBody(response);
//     });

// from("direct:getAllProviders")
//     .routeId("get-all-providers-route")
//     .log("Fetching all providers from DB")
//     .setBody(constant("SELECT id, name, speciality FROM testDB2"))
//     .to("jdbc:camel")
//     .process(exchange -> {
//         List<Map<String, Object>> result = exchange.getIn().getBody(List.class);
//         exchange.getMessage().setBody(result);
//     });


//         // POST endpoint to insert into "rest" table
//         // rest("/providers")
//         //     .post() // convert JSON body into Provider POJO
//         //         .to("direct:insertProvider");

//         //         from("direct:insertProvider")
//         //             .routeId("insert-provider-to-db")
//         //             .log("Received provider: ${body}");
// //         rest("/providers")
// //     .post()
// //     .consumes("application/json")
// //     .to("direct:logProvider");

// // from("direct:logProvider")
// //     .routeId("log-incoming-provider-json")
// //     .log("Raw JSON received: ${body}");
                    

// // from("direct:insertProvider")
// //     .routeId("insert-provider-to-db")
// //     .log("Received provider: ${body}")
// //     .setHeader("name", simple("${body.name}"))
// //     .setHeader("age", simple("${body.age}"))
// //     .setHeader("role", simple("${body.role}"))
// //     .setHeader("CamelSqlParameters", simple("#{name=${header.name}, age=${header.age}, role=${header.role}}"))
// //     .setHeader("CamelSqlQuery", constant("INSERT INTO testDB(name, age, role) VALUES (?, ?, ?)"))
// //     .to("jdbc:camel")
// //     .setBody(constant("Provider saved successfully in 'rest' table"));

//         // The actual insert logic
//         // from("direct:insertProvider")
//         //     .routeId("insert-provider-to-db")
//         //     .log("Received provider: ${body}")
//         //     .setHeader("CamelSqlQuery", simple(
//         //         "INSERT INTO testDB(name, age, role) VALUES (:'name', :'age', :'role')"
//         //     ))
//         //     .log("Executing SQL: ${header.CamelSqlQuery} with params ${header.CamelSqlParameters}")
//         //     .to("jdbc:camel")
//         //     .setBody(constant("Provider saved successfully in 'rest' table"));

//         // rest("/providers")
//         // .get()
//         // .to("direct:getAllProviders");

//         // from("direct:getAllProviders")
//         //     .to("jdbc:camel?useHeadersAsParameters=true")
//         //     .setBody(simple("SELECT * FROM rest"));

//     }
// }