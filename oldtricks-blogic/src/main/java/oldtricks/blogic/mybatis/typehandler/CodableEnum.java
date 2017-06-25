package oldtricks.blogic.mybatis.typehandler;

public interface CodableEnum {

	public abstract int getCode();

	public abstract CodableEnum fromCode(int code) throws IllegalArgumentException;

}