package oldtricks.blogic.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * {@link CodableEnum}を実装したenumのマッピングを行うMybatis用TypeHandlerです。
 * mybatis-config.xmlで以下のように定義します。<br>
 * {@link CodableEnum}を実装したenumを一つ一つ定義する必要があります。 <div> &lt;typeHandlers&gt; <br>
 * &lt;typeHandler handler=&quot;oldtricks.blogic.mybatis.typehandler.CodableEnumTypeHandler<E>&quot;
 * javaType=&quot;sample.UDataType&quot; /&gt; <br>
 * &lt;/typeHandlers&gt; </div>
 *
 * @author kubota
 *
 * @param <E>
 */
@MappedJdbcTypes({ JdbcType.INTEGER })
public class CodableEnumTypeHandler<E extends CodableEnum> extends BaseTypeHandler<E> {
	private Class<E> type;

	public CodableEnumTypeHandler(Class<E> type) {
		if (type == null)
			throw new IllegalArgumentException("Type argument cannot be null");
		if (!CodableEnum.class.isAssignableFrom(type))
			throw new IllegalArgumentException("Type argument must be " + CodableEnum.class.getName());
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.getCode());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int s = rs.getInt(columnName);
		return fromCode(s);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		int s = rs.getInt(columnIndex);
		return fromCode(s);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		int s = cs.getInt(columnIndex);
		return fromCode(s);
	}

	@SuppressWarnings("unchecked")
	E fromCode(int s) throws SQLException {

		CodableEnum _enum = null;
		for (E constant : type.getEnumConstants()) {
			if (constant.getCode() == s) {
				_enum = constant;
				break;
			}
		}
		return (E) _enum;
	}

}