package ClassResources;

import LogFiles.LogFile;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class FileChangeCheckerLifecycle {

    void onStart(@Observes StartupEvent ev) {
        // Start the checker with a 1 hour interval for testing
        LogFile.startFileChangeChecker(1, TimeUnit.HOURS);
        System.out.println("File change checker started with 10-second interval.");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LogFile.stopFileChangeChecker();
        System.out.println("File change checker stopped.");
    }
}