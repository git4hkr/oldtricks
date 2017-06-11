package webmvc.dao;

import org.apache.ibatis.annotations.Insert;

public interface D3ContractDao {
	public static final String TABLE = D3ContractDto.TABLE;

	@Insert("insert into " + TABLE + " (su_id, contract_date) values(#{su_id}, #{contract_date})")
	int insert(D3ContractDto dto);
}
