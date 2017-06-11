package cmd;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oldtricks.bean.VoidBean;
import oldtricks.blogic.BLogic;
import oldtricks.blogic.BLogicExceptionHandler;
import oldtricks.blogic.BLogicFunction;
import oldtricks.blogic.BLogicStatistic;
import oldtricks.blogic.BLogicStatisticConfig;
import oldtricks.blogic.SysLog;
import oldtricks.blogic.exception.BLogicException;
import oldtricks.ex.bean.Jsr303ValidationBean;

@BLogicStatisticConfig(dumpOnExit = true)
public class SampleCmd implements BLogic<String[], VoidBean> {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(SampleCmd.class);

	@BLogicStatistic("samplecmd")
	public VoidBean process(String[] params) throws Exception {
		Param param = parseAndValidate(params);
		LOG.info("param=[{}]", param);
		return null;
	}

	@BLogicStatistic("parseAndValidate")
	@BLogicFunction(value = "パラメータチェック", msgId = "BLM_0999", showMsg = false, resultCode = 99)
	Param parseAndValidate(String[] params) throws CmdLineException {
		Param param = new Param();
		new CmdLineParser(param).parseArgument(params);
		param.throwValidationExceptionIfNecessary();
		return param;
	}

	@BLogicStatistic("handleException(BLogicException ex)")
	@BLogicExceptionHandler
	public void handleException(BLogicException ex) throws BLogicException {
		String msg = SysLog.syslog(ex);
		LOG.error("{}{}", msg, ExceptionUtils.getRootCause(ex).getMessage());
		throw ex;
	}

	@BLogicStatistic("handleException(Exception ex)")
	@BLogicExceptionHandler
	public void handleException(Throwable ex) throws Throwable {
		throw ex;
	}

	public static class Param extends Jsr303ValidationBean {
		private static final long serialVersionUID = 1907296300243176324L;
		@Max(value = 200, message = "最大200でなければなりません")
		private int resultCode = 200;

		@Argument(index = 0, metaVar = "name")
		@NotNull
		private String name;

		public int getResultCode() {
			return resultCode;
		}

		public void setResultCode(int resultCode) {
			this.resultCode = resultCode;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
