package oldtricks.exec;

import java.util.Properties;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import oldtricks.exec.builder.CmdBuilderUtil;
import oldtricks.exec.builder.xml.Configuration;
import oldtricks.executor.TooManyTaskException;
import oldtricks.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 外部コマンド実行のよく使う処理を集めたテンプレートファサードです。
 *
 * @author $Author: kubota $
 *
 */
public class CommandExecTemplate {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(CommandExecTemplate.class);
	private String configFile;
	private CommandExecutor commandExecutor;
	protected Configuration configuration;

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(commandExecutor, "commandExecutor must not be null.");
		configuration = CmdBuilderUtil.getConfigFromXml(configFile);
	}

	/**
	 * 指定されたコマンドIDのコマンドを実行します。コマンド起動の直後に復帰します。コマンドはまだ未完了かも知れません。
	 *
	 * @param id
	 *            コマンドID
	 * @param filter
	 *            コマンドフィルタ
	 * @return コマンドのプロセスFuture
	 * @throws RejectedExecutionException
	 *             プロセス実行用Executorが受け付けを拒否した場合
	 * @throws oldtricks.executor.TooManyTaskException.TooManyTaskException
	 *             プロセス実行用Executorがlimitで指定した同時受け付け数超過した場合
	 * @throws InterruptedException
	 *             プロセスの起動待ち中に割り込みが発生した場合
	 */
	public SubProcess execute(String id, Properties filter)
			throws RejectedExecutionException, TooManyTaskException, InterruptedException {
		return executeWithConsume(id, filter, null);
	}

	/**
	 * 指定されたコマンドIDのコマンドを実行します。コマンド起動の直後に復帰します。コマンドはまだ未完了かも知れません。
	 *
	 * @param id
	 *            コマンドID
	 * @param filter
	 *            コマンドフィルタ
	 * @param handler
	 *            プロセスの入出力ハンドラ
	 * @return コマンドのプロセスFuture
	 * @throws RejectedExecutionException
	 *             プロセス実行用Executorが受け付けを拒否した場合
	 * @throws oldtricks.executor.TooManyTaskException.TooManyTaskException
	 *             プロセス実行用Executorがlimitで指定した同時受け付け数超過した場合
	 * @throws InterruptedException
	 *             プロセスの起動待ち中に割り込みが発生した場合
	 */
	public SubProcess execute(String id, Properties filter, ProcessIoHandler handler)
			throws RejectedExecutionException, TooManyTaskException, InterruptedException {
		return executeWithConsume(id, filter, handler);
	}

	/**
	 * 指定されたIDのコマンドを実行します。 コマンドの標準出力、エラー出力が不要の場合、ストリームを消費してから、復帰します。
	 *
	 * @param id
	 *            コマンドID
	 * @param filter
	 *            コマンドフィルタ
	 * @param handler
	 *            コマンドの入出力ハンドラー
	 * @return コマンドのプロセスFuture
	 * @throws RejectedExecutionException
	 *             プロセス実行用Executorが受け付けを拒否した場合
	 * @throws oldtricks.executor.TooManyTaskException.TooManyTaskException
	 *             プロセス実行用Executorがlimitで指定した同時受け付け数超過した場合
	 * @throws InterruptedException
	 *             プロセスの起動待ち中に割り込みが発生した場合
	 */
	public SubProcess executeWithConsume(String id, Properties filter, ProcessIoHandler handler)
			throws RejectedExecutionException, TooManyTaskException, InterruptedException {
		Command cmd = CmdBuilderUtil.createCommand(configuration, id, filter);
		SubProcess process = commandExecutor.submitCmd(cmd, handler);
		while (process.awaitLaunchProcess(5, TimeUnit.SECONDS)) {
			LOG.info("waiting start process... " + process);
		}
		return process;
	}

	/**
	 * configFileを取得します。
	 *
	 * @return configFile
	 */
	public String getConfigFile() {
		return configFile;
	}

	/**
	 * configFileを設定します。
	 *
	 * @param configFile
	 *            configFile
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	/**
	 * commandExecutorを取得します。
	 *
	 * @return commandExecutor
	 */
	public CommandExecutor getCommandExecutor() {
		return commandExecutor;
	}

	/**
	 * commandExecutorを設定します。
	 *
	 * @param commandExecutor
	 *            commandExecutor
	 */
	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}
}
