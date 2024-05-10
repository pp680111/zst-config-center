import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.locks.LockSupport;

public class LangTest {
    @Test
    public void testnotify() {
        LinkedTransferQueue queue = new LinkedTransferQueue();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                try {
                    queue.transfer("hello");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.err.println("Thread " + Thread.currentThread().getName() + " notified");
            });
        }

        LockSupport.parkNanos(Duration.ofSeconds(3).toNanos());
        queue.clear();
    }

}
