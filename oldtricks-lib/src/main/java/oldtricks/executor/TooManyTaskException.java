package oldtricks.executor;

/**
 * 滞留数上限超過例外です。
 *
 *
 */
public class TooManyTaskException extends Exception {

  private static final long serialVersionUID = -18012865092614738L;

  public TooManyTaskException() {
    super();
  }

  public TooManyTaskException(String paramString, Throwable paramThrowable, boolean paramBoolean1,
      boolean paramBoolean2) {
    super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
  }

  public TooManyTaskException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }

  public TooManyTaskException(String paramString) {
    super(paramString);
  }

  public TooManyTaskException(Throwable paramThrowable) {
    super(paramThrowable);
  }

}
