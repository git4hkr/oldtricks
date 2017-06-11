package webmvc;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import oldtricks.blogic.BLogic;
import oldtricks.blogic.BLogicExceptionHandler;
import oldtricks.blogic.BLogicFunction;
import oldtricks.blogic.BLogicStatistic;
import oldtricks.blogic.BLogicStatisticConfig;
import oldtricks.blogic.BLogicTransaction;
import oldtricks.blogic.SysLog;
import oldtricks.blogic.exception.BLogicException;
import oldtricks.ex.bean.Jsr303ValidationBean;
import oldtricks.ex.util.DateTimeUtil;
import oldtricks.ex.util.UUIDUtil;
import webmvc.DefaultController.Param;
import webmvc.dao.D3ContractDao;
import webmvc.dao.D3ContractDto;
import webmvc.dao.DemoTestDto;

@RestController
@BLogicStatisticConfig(dumpOnExit = true)
public class DefaultController implements BLogic<Param, OutputParam> {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultController.class);
	@Autowired
	@Qualifier("jdbcTemplate.1")
	private JdbcTemplate mysqlTemplate;
	@Autowired
	@Qualifier("jdbcTemplate.2")
	private JdbcTemplate postgresTemplate;
	@Autowired
	@Qualifier("mapper.1")
	private D3ContractDao mysqlDao;
	@Autowired
	@Qualifier("mapper.2")
	private D3ContractDao postgresDao;

	@BLogicStatistic("sample.do")
	@BLogicTransaction
	@RequestMapping("/sample.do")
	@ResponseBody
	public OutputParam process(Param params) throws Exception {
		validate(params);
		D3ContractDto dto = new D3ContractDto(UUIDUtil.getVersion1UUID(),
				DateTimeUtil.now().toString("yyyy-MM-dd HH:mm:ss.SSS"));
		postgresDao.insert(dto);
		mysqlDao.insert(dto);
		return new OutputParam(ResultCode.NORMAL_END, null);
	}

	@BLogicFunction(value = "パラメータチェック", msgId = "BLM_0999", showMsg = false)
	void validate(Param params) {
		params.throwValidationExceptionIfNecessary();
	}

	@BLogicFunction(value = "初期化", msgId = "BLM_0999", showMsg = false)
	public void init(Param params) {
		LOG.debug(params.name);
		String sql = "select * from demo_test";
		Object[] args = null;
		List<DemoTestDto> list = mysqlTemplate.query(sql, args, new BeanPropertyRowMapper<>(DemoTestDto.class));
		list.stream().forEach(dto -> LOG.debug("{}", dto));
	}

	@BLogicExceptionHandler
	public OutputParam handleException(BLogicException ex) {
		String msg = SysLog.syslog(ex);
		LOG.error("{}{}", msg, ExceptionUtils.getRootCause(ex).getMessage());
		return new OutputParam(ResultCode.LOGIC_ERR, msg);
	}

	@BLogicExceptionHandler
	public OutputParam handleException(Throwable ex) {
		LOG.error("Exception", ex);
		return new OutputParam(ResultCode.SYSTEM_ERR, ex.getMessage());
	}

	public static class Param extends Jsr303ValidationBean {
		private static final long serialVersionUID = 1907296300243176324L;
		@Max(value = 200, message = "最大200でなければなりません")
		private int resultCode = 200;
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
