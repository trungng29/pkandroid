package androidx.arch.core.executor.testing;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.concurrent.Executor;

/**
 * Local fallback for unit tests when core-testing dependency is not resolved by IDE.
 */
public class InstantTaskExecutorRule extends TestWatcher {

    @Override
    protected void starting(Description description) {
        super.starting(description);
        ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            @Override
            public void executeOnDiskIO(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    public Executor getBackgroundExecutor() {
        return Runnable::run;
    }

    public Executor getMainThreadExecutor() {
        return Runnable::run;
    }
}

