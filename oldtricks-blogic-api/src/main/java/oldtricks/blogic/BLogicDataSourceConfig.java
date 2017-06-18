package oldtricks.blogic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本アノテーションを付与したメソッドで宣言的データソースルーティングを適用します。 <BR>
 * 本アノテーションを付与したメソッド内では単一のデータソースにのみアクセスできます。
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BLogicDataSourceConfig {

	String type() default TYPE_MASTER;

	boolean readReplica() default true;

	String TYPE_MASTER = "master";
	String TYPE_SHARD = "shard";

}
