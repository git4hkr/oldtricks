package oldtricks.executor.tgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import oldtricks.executor.DefaultExecutor;
import oldtricks.executor.TooManyTaskException;

public class TaskGroupImpl<T> extends TaskGroup<T> {
	private DefaultExecutor executor = new DefaultExecutor();
	private final List<TaskWrapper> wrappedTasks = new ArrayList<>();
	private final BlockingQueue<TaskWrapper> completionQueue = new LinkedBlockingQueue<>();
	private final TaskExecutionPolicy failurePolicy;
	private final AtomicInteger doneTaskCount = new AtomicInteger();

	public TaskGroupImpl(List<Callable<T>> tasks, String taskName, int concurrentry, TaskExecutionPolicy policy) {
		super();
		failurePolicy = policy;
		executor.setName(taskName);
		executor.setMaxThreadsSize(concurrentry);
		executor.setThreadsSize(concurrentry);
		for (final Callable<T> callable : tasks) {
			wrappedTasks.add(new TaskWrapper(callable));
		}

	}

	@Override
	public TaskGroup<T> submit() throws TooManyTaskException {
		executor.start();
		for (TaskWrapper runnable : wrappedTasks) {
			runnable.submit();
		}
		return this;
	}

	@Override
	public synchronized boolean awaitCompletion(long timeoutSec) throws InterruptedException, TaskFailureException {
		long start = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		while (doneTaskCount.get() != wrappedTasks.size()) {
			long now = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
			if (now >= start + timeoutSec)
				return true;
			TaskWrapper wrappedTask = completionQueue.poll(1, TimeUnit.SECONDS);
			if (wrappedTask == null)
				continue;
			try {
				wrappedTask.get();
			} catch (ExecutionException e) {
				failurePolicy.handleTaskException(e.getCause(), wrappedTask.task);
			} finally {
				doneTaskCount.incrementAndGet();
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		executor.shutdown(5);
		for (TaskWrapper task : wrappedTasks) {
			task.finish();
		}
	}

	@Override
	public List<T> getResults() {
		List<T> ret = new ArrayList<>();
		for (TaskWrapper t : wrappedTasks) {
			ret.add(t.ret);
		}
		return ret;
	}

	private class TaskWrapper implements Callable<T> {
		private Callable<T> task;
		private T ret;
		private Future<T> future;
		private boolean isFinish = false;

		public TaskWrapper(Callable<T> task) {
			super();
			this.task = task;
		}

		public T get() throws InterruptedException, ExecutionException {
			return future.get();
		}

		public void submit() throws TooManyTaskException {
			future = (Future<T>) executor.submit(this);
		}

		private synchronized void finish() {
			if (isFinish == false) {
				isFinish = true;
				completionQueue.offer(this);
			}
		}

		@Override
		public T call() throws Exception {
			try {
				ret = task.call();
				return ret;
			} finally {
				finish();
			}
		}
	}
}
