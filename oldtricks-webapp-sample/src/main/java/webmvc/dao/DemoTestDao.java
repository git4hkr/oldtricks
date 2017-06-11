package webmvc.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface DemoTestDao {
	public static final String TABLE = "demo_test";

	@Insert("insert into " + TABLE + " (c1, c2, c3, c4, c5) values(#{c1}, #{c2}, #{c3}, #{c4}, #{c5})")
	int insertTable(DemoTestDto dto);

	@Update("UPDATE sequence SET id=LAST_INSERT_ID(id+1);")
	void incrementSequence();

	@Options(fetchSize = Integer.MIN_VALUE)
	@Select("SELECT LAST_INSERT_ID()")
	int getSequence();
}
