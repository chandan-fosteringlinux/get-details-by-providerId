// // package camel.processor;

// // import org.apache.camel.Exchange;
// // import org.apache.camel.Processor;

// // import java.io.File;

// // public class FilePermissionValidatorProcessor implements Processor {

// //     @Override
// //     public void process(Exchange exchange) throws Exception {
// //         File file = exchange.getIn().getBody(File.class);

// //         if (file == null || !file.exists()) {
// //             throw new IllegalStateException("🚫 File does not exist.");
// //         }

// //         if (!file.canRead()) {
// //             throw new IllegalStateException("🚫 File is not readable: " + file.getName());
// //         }

// //         if (!file.canWrite()) {
// //             throw new IllegalStateException("🚫 File is not writable/movable: " + file.getName());
// //         }

// //         // Everything OK
// //         exchange.getIn().setHeader("fileReadableWritable", true);
// //     }
// // }



// package camel.processor;

// import java.io.File;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.StandardCopyOption;

// import org.apache.camel.Exchange;
// import org.apache.camel.Processor;

// public class FilePermissionValidatorProcessor implements Processor {

//     private final String unreadableDirectory;

//     public FilePermissionValidatorProcessor(String unreadableDirectory) {
//         this.unreadableDirectory = unreadableDirectory;
//     }

//     @Override
//     public void process(Exchange exchange) throws Exception {
//         File file = exchange.getIn().getBody(File.class);

//         if (file == null || !file.exists()) {
//             throw new IllegalStateException("File does not exist.");
//         }

//         if (!file.canRead() || !file.canWrite()) {
//             String fileName = file.getName();
//             Path source = file.toPath();
//             Path targetDir = new File(unreadableDirectory).toPath();
//             Path target = targetDir.resolve(fileName);

//             try {
//                 Files.createDirectories(targetDir);
//                 Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
//                 exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
//                 System.err.println("🚫 File moved to unreadable_files: " + fileName);
//             } catch (Exception moveEx) {
//                 // Rename in place if move fails
//                 File renamed = new File(file.getParent(), fileName + ".error");
//                 boolean renamedOk = file.renameTo(renamed);
//                 System.err.println("🚫 Could not move file, renamed to: " + renamed.getName());
//             }

//             // Stop further routing
//             exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
//         }

        
//     }
//     public void moveFile(Exchange exchange){
//             System.out.println("Move file to this directory");
//         }

        
// }
