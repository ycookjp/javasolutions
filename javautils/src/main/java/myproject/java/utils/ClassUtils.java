package myproject.java.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;

/**
 * {@link java.lang.Class}の操作を提供します。
 *
 * Copyright ycookjp
 * https://github.com/ycookjp/
 */
public class ClassUtils {
    /**
     *  外部からのインスタンス化を抑止するためのコンストラクタ。
     */
    private ClassUtils() { }

    /**
     * クラスを指定して、そのクラスが配置されているクラスパスのディレクトリの親ディレクトリ、
     * またはjarファイルのパスの親ディレクトリを取得します。
     * @param cls クラスを指定します。
     * @return 取得したディレクトリの{@link java.io.File}オブジェクトを返します。
     *      ディレクトリを取得できなかった場合（主にセキュリティの製薬による）はnullを返します。
     * @throws URISyntaxException URIの構文が間違っている場合
     */
    public static File getClassDirParent(Class<? extends Object> cls) throws URISyntaxException {
        if (cls == null) {
            return null;
        }

        // 指定されたクラスが配置されたクラスパスのディレクトリまたはJarファイルのパスを取得
        CodeSource cs = cls.getProtectionDomain().getCodeSource();

        if (cs != null) {
            // CodeSourceが取得できた場合
            URL location = cs.getLocation();
            URI clsUri = location.toURI();

            File clsDirPath = new File(clsUri.getPath());

            // 取得したファイルの親ディレクトリを返す。
            File clsDirParent = clsDirPath.getParentFile();

            return clsDirParent;
        } else {
            // CoudeSourceが取得できなかった場合 - 主にセキュリティ的な理由による
            return null;
        }
    }

    /**
     * フィールド名を指定して、クラスで定義されているフィールドを取得します。
     * <p>
     * クラス定義されているフィールドの中で、指定された名前と一致するものを返します。フィールドの
     * スコープはpublic以外のものでも名前が一致すればそのフィールドを返します。指定されたクラスに
     * フィールドが見つからなかった場合は、以下の手順でインターフェース及びスーパー・クラスを
     * 再帰的に取得して指定されたフィールド名に一致するフィールドを探索します。
     * <ol>
     * <li>
     *   指定されたクラスを実装しているインターフェースを順番に取得して、そのインターフェースで
     *   定義されているフィールドを検索する
     * </li>
     * <li>
     *   指定されたクラスのスーパー・クラスを取得して、そのクラスで定義されているフィールドを
     *   検索する。
     * </li>
     * </ol>
     *
     * @param classz クラス
     * @param name フィールド名
     * @param modifiers フィールドの取得対象のアクセス修飾子を{@link java.lang.reflect.Modifier}クラスの定数の和で
     *      指定する。取得対象のアクセス修飾子を指定しない場合は、-1を指定すること。
     * @return 見つかったフィールドを返します。クラスまたはフィールド名にnullを指定した場合は
     *      nullを返します。
     * @throws NoSuchFieldException 指定された名前と一致するフィールドが見つからなかった場合
     * @throws SecurityException セキュリティ・マネージャでエラーが発生した場合。エラーの
     *      発生する条件は、{@link Class#getDeclaredField(String)}の仕様を参照のこと。
     */
    public static Field findField(Class<?> classz, String name, int modifiers)
            throws NoSuchFieldException, SecurityException {
        if (classz == null || name == null) {
            return null;
        }

        Field field = null;
        try {
            field = classz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            for (Class<?> iface : classz.getInterfaces()) {
                try {
                    field = findField(iface, name, modifiers);
                    break;
                } catch (NoSuchFieldException ee) { }
            }

            if (field == null) {
                Class<?> superClass = classz.getSuperclass();
                if (superClass != null) {
                    field = findField(superClass, name, modifiers);
                } else {
                    throw e;
                }
            }
        }

        if (modifiers > 0 && (field.getModifiers() & modifiers) == 0) {
            throw new NoSuchFieldException(classz.getName() + "." + name + "does not match modifiers");
        }

        return field;
    }

    /**
     * メソッド名とパラメータの型を指定して、クラスで定義されているフィールドを取得します。
     * <p>
     * クラス定義されているメソッドの中で、指定された名前及びパラメータの型一致するものを
     * 返します。メソッドのスコープはpublic以外のものでも名前と型が一致すればそのメソッドを
     * 返します。指定されたクラスにメソッドが見つからなかった場合は、以下の手順で
     * インターフェース及びスーパー・クラスを再帰的に取得して指定されたメソッド名及び型に
     * 一致するメソッドを探索します。
     * <ol>
     * <li>
     *   指定されたクラスを実装しているインターフェースを順番に取得して、そのインターフェースで
     *   定義されているメソッドを検索する
     * </li>
     * <li>
     *   指定されたクラスのスーパー・クラスを取得して、そのクラスで定義されているメソッドを
     *   検索する。
     * </li>
     * </ol>
     *
     * @param classz クラス
     * @param name メソッド名
     * @param parameterTypes パラメータの型の配列。メソッドのパラメータの順番にパラメータの
     *      クラスを配列に設定する。
     * @param modifiers フィールドの取得対象のアクセス修飾子を{@link java.lang.reflect.Modifier}クラスの定数の和で
     *      指定する。取得対象のアクセス修飾子を指定しない場合は、-1を指定すること。
     * @return 見つかったメソッドを返します。クラス名またはメソッド名にnullを指定した場合はnullを
     *      返します。
     * @throws NoSuchMethodException 指定された名前及び方に一致するメソッドが見つからなかった場合
     * @throws SecurityException セキュリティ・マネージャでエラーが発生した場合。エラーの
     *      発生する条件は、{@link Class#getDeclaredMethod(String, Class...)}の仕様を参照のこと。
     */
    public static Method findMethod(Class<?> classz, String name, Class<?>[] parameterTypes, int modifiers)
            throws NoSuchMethodException, SecurityException {
        if (classz == null || name == null) {
            return null;
        }

        Method method = null;
        try {
            method = classz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            for (Class<?> iface : classz.getInterfaces()) {
                try {
                    method = findMethod(iface, name, parameterTypes, modifiers);
                    break;
                } catch (NoSuchMethodException ee) { }
            }

            if (method == null) {
                Class<?> superClass = classz.getSuperclass();
                if (superClass != null) {
                    method = findMethod(superClass, name, parameterTypes, modifiers);
                } else {
                    throw e;
                }
            }
        }

        if (modifiers > 0 && (method.getModifiers() & modifiers) == 0) {
            if (parameterTypes == null) {
                parameterTypes = new Class[] {};
            }
            throw new NoSuchMethodException(classz.getName() + "." + name + "(" + Arrays.asList(parameterTypes)
                    + ") does not match modifiers");
        }

        return method;
    }
}
