package com.example.routes;

import java.util.Collections;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import com.example.model.Provider;
import com.example.service.ProviderService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProviderRoute extends RouteBuilder {

    @Inject
    ProviderService providerService;

    @Override
    public void configure() throws Exception {

        onException(IllegalArgumentException.class)
            .handled(true)
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("CamelHttpResponseCode", constant(400))
            .setBody().simple("{\"error\": \"${exception.message}\"}");

        restConfiguration()
            .bindingMode(RestBindingMode.json);

        rest("/providers")
            .post()
                .consumes("application/json")
                .type(Provider.class)
                .to("direct:saveProvider")
            .get()
                .produces("application/json")
                .to("direct:getAllProviders");

        from("direct:saveProvider")
            .routeId("save-provider")
            .log("Received provider: ${body}")
            .bean(providerService, "validateProvider")
            .bean(providerService, "saveProvider")
            .setHeader("Content-Type", constant("application/json"))
            .setBody().constant(Collections.singletonMap("status", "saved"));

        from("direct:getAllProviders")
            .routeId("get-all-providers")
            .bean(providerService, "getAllProviders")
            .log("Returning providers: ${body}");

    }
}