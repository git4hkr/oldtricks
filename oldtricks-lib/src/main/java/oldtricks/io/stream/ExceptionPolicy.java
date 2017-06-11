package oldtricks.io.stream;


public interface ExceptionPolicy<T> {
	public void apply(StreamTemplate<T> template, Exception e, T line) throws Exception;
}
