package myproject.java.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.Set;

/**
 * {@link java.util.Properties}の操作を提供します。
 *
 * Copyright ycookjp
 * https://github.com/ycookjp/
 */
public class PropertiesUtils {
    /**
     * 外部からのインスタンス化を抑止するためのコンストラクタ。
     */
    private PropertiesUtils() { }

    /**
     * プロパティファイルのクラスパスを指定して、{@link java.util.Properties}をロードします。
     * <p>
     * クラスパスの通ったディレクトリに配置されているプロパティファイルのクラスパスを指定して
     * プロパティファイルの内容を読み込んだ{@link java.util.Properties}のインスタンスを返します。
     * クラスパスを指定したディレクトリの下の「test/properties/test.properteis」を読み込む場合は、
     * クラスパスの名前に「test/properties/test.properties」を指定します。
     * </p>
     * @param name クラスパスの名前
     * @return {@link java.util.Properties}を返します。nameにnullを指定した場合は、nullを返します。
     * @throws IOException {@link java.util.Properties}のロードに失敗した場合
     */
    public static Properties loadResource(String name) throws IOException {
        if (name == null) {
            return null;
        }

        Properties properties = new Properties();
        InputStream is = null;
        Reader reader = null;
        try {
            is = ClassLoader.getSystemResourceAsStream(name);
            if (is == null) {
                throw new IOException("Resource " + name + " not found.");
            }
            reader = new InputStreamReader(is, "ISO8859-1");
            properties.load(reader);
            return properties;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) { }
            } else if (is != null) {
                try {
                    is.close();
                } catch (IOException ie) { }
            }
        }
    }

    /**
     * プロパティの値の"{&lt;keyword&gt;}の部分を、システムプロパティ及びパラメータで指定された
     * プロパティの&lt;keyword&gt;の値で置換します。
     * @param properties プロパティを指定します。nullを指定すると何もせずに復帰します。
     */
    public static void replaceKeyword(Properties properties) {
        if (properties == null) {
            return;
        }
        Properties keywords = new Properties();
        keywords.putAll(System.getProperties());
        keywords.putAll(properties);
        Set<Object> keyset = properties.keySet();
        for (Object key : keyset) {
            String value = properties.getProperty(key.toString());
            if (value != null) {
                Set<Object> replacementKeySet = keywords.keySet();
                for (Object replacementKey : replacementKeySet) {
                    value = value.replace("${" + replacementKey.toString() + "}",
                            keywords.getProperty(replacementKey.toString()));
                }
                properties.put(key, value);
            }
        }
    }
}
