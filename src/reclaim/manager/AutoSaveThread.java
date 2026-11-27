package reclaim.manager;

import java.io.IOException;

/** Background thread that saves all items every 30 seconds. */
public class AutoSaveThread extends Thread {
    private static final long SAVE_INTERVAL_MS = 30000;

    private final ReClaimManager manager;

    public AutoSaveThread(ReClaimManager manager) {
        this.manager = manager;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(SAVE_INTERVAL_MS);
                manager.saveToFile();
            } catch (InterruptedException e) {
                return;
            } catch (IOException e) {
                // stay silent; it retries in 30 seconds
            }
        }
    }
}
