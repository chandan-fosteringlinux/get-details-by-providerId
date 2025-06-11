package camel.routes;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileRouterRoute extends RouteBuilder {

    @ConfigProperty(name = "file.input.directory")
    String inputDirectory;

    @ConfigProperty(name = "file.output.directory")
    String outputDirectory;

    @ConfigProperty(name = "file.error.directory")
    String errorDirectory;

    @ConfigProperty(name = "file.invalid.directory")
    String invalidFiles;

    @Override
    public void configure() {
    //    from("file:" + inputDirectory
    //       + "?include=^.*\\.txt$"
    //       + "&moveFailed=" + errorDirectory)
    //       .routeId("file-router")
    //       .log("file: ${file:name} picked up")
    //       .to("file:" + outputDirectory);
     // Global exception handler
    onException(Exception.class)
        .handled(true)
        .process(exchange -> {
            Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
            System.err.println("Unreadable file: " + fileName + ", cause: " + cause.getMessage());
        })
        // You can choose to move to a dead-letter dir if possible, or just log
        .to("log:unreadable-file?level=ERROR");

    from("file:" + inputDirectory
        + "?include=^.*\\.txt$"
        + "&moveFailed=" + errorDirectory
        + "&readLock=none") // Don't wait/lock — useful for unreadable or immutable files
        .routeId("file-router")
        .log("file: ${file:name} picked up")
        .to("file:" + outputDirectory);
    }
}







// package camel.routes;
// import org.apache.camel.builder.RouteBuilder;
// import org.eclipse.microprofile.config.inject.ConfigProperty;

// import jakarta.enterprise.context.ApplicationScoped;

// @ApplicationScoped
// public class FileRouterRoute extends RouteBuilder {

//     @ConfigProperty(name = "file.input.directory")
//     String inputDirectory;

//     @ConfigProperty(name = "file.output.directory")
//     String outputDirectory;

//     @ConfigProperty(name = "file.error.directory")
//     String errorDirectory;

//     @ConfigProperty(name = "file.invalid.directory")
//     String invalidFiles;

//     @Override
//     public void configure() {
//        from("file:" + inputDirectory
//           + "?include=^.*\\.txt$"
//           + "&moveFailed=" + errorDirectory)
//           .routeId("file-router")
//           .log("file: ${file:name} picked up")
//           .to("file:" + outputDirectory);

//         from("file:" + inputDirectory 
//             + "?exclude=^.*\\.txt$"
//             + "&moveFailed=" + errorDirectory)
//             .routeId("file-router2")
//             .log("file: ${file:name} with invalid extention picked up")
//             .to("file:" + invalidFiles);
//     }
// }




// package camel.routes;
// import org.apache.camel.builder.RouteBuilder;
// import org.eclipse.microprofile.config.inject.ConfigProperty;

// import jakarta.enterprise.context.ApplicationScoped;

// @ApplicationScoped
// public class FileRouterRoute extends RouteBuilder {

//     @ConfigProperty(name = "file.input.directory")
//     String inputDirectory;

//     @ConfigProperty(name = "file.output.directory")
//     String outputDirectory;

//     @ConfigProperty(name = "file.error.directory")
//     String errorDirectory;

//     @ConfigProperty(name = "file.invalid.directory")
//     String invalidFiles;

//     @Override
//     public void configure() {
//        from("file:" + inputDirectory
//           + "&moveFailed=" + errorDirectory)
//         .routeId("file-router")
//         .log("Picked up file: ${file:name}")
//         .choice()
//             .when(simple("${file:ext} == 'txt'"))
//                 .log(".txt file detected: ${file:name}")
//                 .convertBodyTo(String.class)
//                 .choice()
//                     .when(simple("${body} != null && ${body} contains 'to' && ${body} contains 'from' && ${body} contains 'body'"))
//                         .log("📄 Valid content found in file: ${file:name}")
//                         .to("file:" + outputDirectory)
//                     .otherwise()
//                         .log("⚠️ Missing required content in file: ${file:name}, moving to invalid_files")
//                         .to("file:" + invalidFiles)
//                 .endChoice()
//             .otherwise()
//                 .log("❌ Invalid file type: ${file:name}, moving to invalid_files")
//                 .to("file:" + invalidFiles);
//     }
// }














// package camel.routes;


// import org.apache.camel.builder.RouteBuilder;
// import org.eclipse.microprofile.config.inject.ConfigProperty;

// import camel.processor.FilePermissionValidatorProcessor;
// import jakarta.enterprise.context.ApplicationScoped;

// @ApplicationScoped
// public class FileRouterRoute extends RouteBuilder {

//     @ConfigProperty(name = "file.input.directory")
//     String inputDirectory;

//     @ConfigProperty(name = "file.output.directory")
//     String outputDirectory;

//     @ConfigProperty(name = "file.invalid.directory")
//     String invalidDirectory;

//     @Override
//     public void configure() {
//         // from("file:" + inputDirectory + "?delete=true")
//         //     .routeId("file-router")
//         //     .log("Processing file: ${file:name}")
//         //     .to("file:" + outputDirectory);
//         // from("file:" + inputDirectory + "?delete=true&include=.*\\.txt$")
//         // .routeId("txt-file-router")
//         // .log("Processing valid file: ${file:name}")
//         // .to("file:" + outputDirectory);
//         // from("file:" + inputDirectory + "?delete=true")
//         // .routeId("file-router")
//         // .log("Picked up file: ${file:name}")
//         // .choice()
//         //     .when(simple("${file:ext} == 'txt'"))
//         //         .log("✅ Valid .txt file: ${file:name}")
//         //         .to("file:" + outputDirectory)
//         //     .otherwise()
//         //         .log("❌ Invalid file type: ${file:name} — moving to invalid_files")
//         //         .to("file:" + invalidDirectory)");

//     // Route for invalid files (not .txt)
//     // from("file:" + inputDirectory + "?delete=true&exclude=.*\\.txt$")
//     //     .routeId("invalid-file-router")
//     //     .log("Invalid file moved: ${file:name}")
//     //     .to("file:" + invalidDirectory);

//     // from("file:" + inputDirectory + "?delete=true")
//     //     .routeId("file-router")
//     //     .log("📥 Picked up file: ${file:name}")
//     //     .choice()
//     //         .when(simple("${file:ext} == 'txt'"))
//     //             .log("✅ .txt file detected: ${file:name}")
//     //             .convertBodyTo(String.class)
//     //             .choice()
//     //                 .when(simple("${body} != null && ${body} contains 'to' && ${body} contains 'from' && ${body} contains 'body'"))
//     //                     .log("📄 Valid content found in file: ${file:name}")
//     //                     .to("file:" + outputDirectory)
//     //                 .otherwise()
//     //                     .log("⚠️ Missing required content in file: ${file:name}, moving to invalid_files")
//     //                     .to("file:" + invalidDirectory)
//     //             .endChoice()
//     //         .otherwise()
//     //             .log("❌ Invalid file type: ${file:name}, moving to invalid_files")
//     //             .to("file:" + invalidDirectory);

//     from("file:" + inputDirectory + "?delete=false") // note: don't delete before checking
//             .routeId("file-router")
//             .log("📥 Picked up file: ${file:name}")
//             .process(new FilePermissionValidatorProcessor())
//             .onException(Exception.class)
//                 .handled(true)
//                 .log("🚫 File is not processable due to permission issue: ${file:name}")
//                 .to("file:" + invalidDirectory)
//             .end()
//             .choice()
//                 .when(simple("${file:ext} == 'txt'"))
//                     .log("✅ .txt file detected: ${file:name}")
//                     .convertBodyTo(String.class)
//                     .choice()
//                         .when(simple("${body} != null && ${body} contains 'to' && ${body} contains 'from' && ${body} contains 'body'"))
//                             .log("📄 Valid content found in file: ${file:name}")
//                             .to("file:" + outputDirectory)
//                         .otherwise()
//                             .log("⚠️ Missing required content in file: ${file:name}, moving to invalid_files")
//                             .to("file:" + invalidDirectory)
//                     .endChoice()
//                 .otherwise()
//                     .log("❌ Invalid file type: ${file:name}, moving to invalid_files")
//                     .to("file:" + invalidDirectory);

//     }
// }









// package camel.routes;

// import camel.processor.FilePermissionValidatorProcessor;
// import org.apache.camel.builder.RouteBuilder;
// import org.eclipse.microprofile.config.inject.ConfigProperty;

// import jakarta.enterprise.context.ApplicationScoped;

// @ApplicationScoped
// public class FileRouterRoute extends RouteBuilder {

//     @ConfigProperty(name = "file.input.directory")
//     String inputDirectory;

//     @ConfigProperty(name = "file.output.directory")
//     String outputDirectory;

//     @ConfigProperty(name = "file.invalid.directory")
//     String invalidDirectory;

//     @ConfigProperty(name = "file.unreadable.directory")
//     String unreadableDirectory;

//     @Override
//     public void configure() {

//         from("file:" + inputDirectory + "?delete=true")
//             .routeId("file-router")
//             .log("📥 Picked up file: ${file:name}")
//             .process(new FilePermissionValidatorProcessor(unreadableDirectory))
//             .choice()
//                 .when(simple("${file:ext} == 'txt'"))
//                     .log("✅ .txt file detected: ${file:name}")
//                     .convertBodyTo(String.class)
//                     .choice()
//                         .when(simple("${body} != null && ${body} contains 'to' && ${body} contains 'from' && ${body} contains 'body'"))
//                             .log("📄 Valid content found in file: ${file:name}")
//                             .to("file:" + outputDirectory)
//                         .otherwise()
//                             .log("⚠️ Missing required content in file: ${file:name}, moving to invalid_files")
//                             .to("file:" + invalidDirectory)
//                     .endChoice()
//                 .otherwise()
//                     .log("❌ Invalid file type: ${file:name}, moving to invalid_files")
//                     .to("file:" + invalidDirectory);
//     }
// }



// from("file:" + inputDirectory 
//             + "?exclude=^.*\\.txt$"
//             + "&noop=true"
//             + "&moveFailed=" + errorDirectory)
//             .routeId("file-router2")
//             .log("file: ${file:name} with invalid extention picked up")
//             .to("file:" + invalidFiles);
// from("file:" + inputDirectory 
//           + "?include=^.*\\.txt$" + "&noop=true"
//           + "&moveFailed=" + errorDirectory
//           + "&idempotent=true"
//           + "&idempotentRepository=#fileIdempotentRepo")
//           .routeId("file-router")
//           .log("file: ${file:name} picked up")
//           .to("file:" + outputDirectory + "?allowNullBody=false");
//     }
    // from("file:" + inputDirectory 
    //     + "?exclude=^.*\\.txt$"
    //     + "&moveFailed=" + errorDirectory)
    //     .routeId("file-router2")
    //     .log("file: ${file:name} picked up")
    //     .to("file:" + invalidFiles);

// from("file:" + inputDirectory 
//         + "?include=^.*\\.txt$"
//         + "&moveFailed=" + errorDirectory)
//         .routeId("file-router")
//         .convertBodyTo(String.class)
//         .filter(body().regex("(?s)^to:.+\\nfrom:.+\\nbody:.+"))
//             .log("Valid file: ${file:name} passed content check")
//             .to("file:" + outputDirectory)
//         .end();

    // @Override
    // public void configure(){
    //     onException(Exception.class).handled(true).to("file:" + errorDirectory);
    //     from("file:" + inputDirectory +"?include=^.*\\.txt$")
    //     .routeId("file-router")
    //     .log("file: ${file:name} picked up")
    //     .to("file:" + outputDirectory);
    // }
