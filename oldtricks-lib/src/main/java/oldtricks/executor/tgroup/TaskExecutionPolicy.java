package oldtricks.executor.tgroup;

import java.util.concurrent.Callable;


/**
 * タスクグループの実行ポリシーのストラテジーです。
 * 
 * @author $Author: kubota $
 * 
 */
public interface TaskExecutionPolicy {
	<V> void handleTaskException(Throwable throwable, Callable<V> task) throws TaskFailureException;
}
