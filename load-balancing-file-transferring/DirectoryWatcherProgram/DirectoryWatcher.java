
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class DirectoryWatcher {

    private static final String DIRECTORY_TO_WATCH = "/home/user/Desktop/mavenprojects/load-balancing-file-transferring/WhatsApp"; // Change this as needed
    private static final String TRIGGER_ENDPOINT = "http://localhost:8080/trigger"; // Change to your endpoint

    public static void main(String[] args) throws IOException, InterruptedException {
        Path pathToWatch = Paths.get(DIRECTORY_TO_WATCH);
        WatchService watchService = FileSystems.getDefault().newWatchService();

        pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        System.out.println("Watching directory: " + pathToWatch.toAbsolutePath());

        while (true) {
            WatchKey key = watchService.take(); // Wait for key to be signaled

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();

                System.out.println("New file detected: " + fileName);

                triggerEndpoint();
            }

            boolean valid = key.reset();
            if (!valid) break;
        }

        watchService.close();
    }

    private static void triggerEndpoint() {
        try {
            URL url = new URL(TRIGGER_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("Triggered endpoint, response code: " + responseCode);

            conn.disconnect();
        } catch (IOException e) {
            System.err.println("Failed to call endpoint: " + e.getMessage());
        }
    }
}
