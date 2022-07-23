package myproject.java.utils;

/**
 * {@link java.lang.Exception}の操作を提供します。
 * 
 * Copyright ycookjp
 * https://github.com/ycookjp/
 */
public class ExceptionUtils {
    /**
     * 外部空のインスタンス化を抑止するためのコンストラクタ。
     */
    private ExceptionUtils() { }

    /**
     * {@link java.lang.Exception}のスタックトレースを文字列に変換します。
     * @param e 例外
     * @return スタックトレースを文字列に変換した結果を返します。引数 e にnullを指定すると
     *      nullを返します。
     */
    public static String getStackTraceString(Exception e) {
        if (e == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(e.toString());
        sb.append(System.getProperty("line.separator"));
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements) {
            sb.append("    at " + element.toString());
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }
}
