package oldtricks.io.stream;

@FunctionalInterface
public interface LineConsumer<T> {
	void accept(T line, int lineNo) throws Exception;
}
