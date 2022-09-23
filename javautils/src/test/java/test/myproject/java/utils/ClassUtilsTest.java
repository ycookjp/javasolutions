package test.myproject.java.utils;

import java.awt.Component;
import java.awt.image.ImageObserver;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.SortedMap;

import javax.swing.JComponent;
import javax.swing.table.JTableHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myproject.java.utils.ClassUtils;
import myproject.java.utils.ExceptionUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ClassUtils}クラスのテストを実行します。
 * @author ycookjp my project
 *
 */
public class ClassUtilsTest {
    /** Logger. */
    private static Logger logger = LoggerFactory.getLogger(ClassUtilsTest.class);

    /**
     * {@link ClassUtils#getClassDirParent(Class)}メソッドのテストを実行します。
     * <ul>
     * <li>引数に{@link ClassUtilsTest}(テストクラス)を指定すると、結果が/targetで終わる</li>
     * <li>引数に{@link org.junit.Test}(Jarのクラス)を指定すると、結果に"/junit/junit/"が含まれる</li>
     * <li>引数に{@link java.lang.StackOverflowError}のクラスを指定するとnullが返る</li>
     * <li>引数にnullを設定するとnullが返る</li>
     * </ul>
     */
    @Test
    public void testGetClassFilePath() {
        logger.debug("***** BEGIN testGetClassFilePath");
        try {
            File file = ClassUtils.getClassDirParent(ClassUtilsTest.class);
            String absolutePath = file.getAbsolutePath();
            logger.debug("ClassUtils.getClassDirParent(TestClassUtils.class) ==> {}", absolutePath);
            Assert.assertEquals("引数にTestClassUtils(テストクラス)を指定すると、結果が/targetで終わる",
                    "/target", absolutePath.substring(absolutePath.length() - "/target".length()));

            file = ClassUtils.getClassDirParent(org.junit.Test.class);
            absolutePath = file.getAbsolutePath();
            logger.debug("ClassUtils.getClassDirParent(org.junit.Test.class) ==> {}", absolutePath);
            Assert.assertTrue("引数にorg.junit.Test(Jarのクラス)を指定すると、結果に\"/junit/junit/\"が含まれる",
                    absolutePath.indexOf("/junit/junit/") > 0);

            file = ClassUtils.getClassDirParent(java.lang.StackOverflowError.class);
            logger.debug("ClassUtils.getClassDirParent(java.lang.StackOverflowError.class) ==> {}", file);
            Assert.assertNull("java.lang.StackOverflowErrorのクラスを指定するとnullが返る", file);

            file = ClassUtils.getClassDirParent(null);
            logger.debug("ClassUtils.getClassDirParent(null) ==> {}", file);
            Assert.assertNull("引数にnullを設定するとnullが返る", file);
        } catch (URISyntaxException use) {
            use.printStackTrace();
            Assert.fail(use.toString());
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTraceString(e));
            Assert.fail(e.toString());
        } finally {
            logger.debug("***** END testGetClassFilePath");
        }
    }

    /**
     * {@link ClassUtils#findField(Class, String, int)}メソッドをテストします。
     * <ul>
     * <li>
     *   引数に JTableHeader.class, "columnModel", Modifier.PUBLIC + Modifier.PROTECTEDを指定すると
     *   protectedフィールド JTableHeader.columnModel を取得できること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "WIDTH", -1を指定するとpackageフィールド ImageObserver.WIDTH を
     *   取得できること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "columnModel", Modifier.PUBLIC + Modifier.PROTECTED を指定すると
     *   protectedフィールド Component.accessibleContext を取得できること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "defaultRenderer", Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE
     *   を指定するとprivateフィールド JTableHeader.defaultRenderer を取得できること
     * </li>
     * <li>
     *   引数に TableHeader.class, "graphicsConfig", Modifier.PRIVATE を指定すると privateフィールド
     *   JTableHeader.defaultRenderer を取得できること
     * </li>
     * <li>
     *   引数に null, "graphicsConfig", Modifier.PRIVATE を指定すると null が返ること
     * </li>
     * <li>
     *   引数に JTableHeader.class, null, Modifier.PRIVATE を指定すると null が返ること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "graphicsConfig", Modifier.PROTECTED を指定すると
     *   NoSuchFieldException例外が発生すること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "foo", -1 を指定すると NoSuchFieldException例外が発生すること
     * </li>
     * </ul>
     */
    @Test
    public void testFindField() {
        logger.debug("***** BEGIN testFindField");
        try {
            Field field = ClassUtils.findField(JTableHeader.class, "columnModel", Modifier.PUBLIC + Modifier.PROTECTED);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"columnModel\", Modifier.PUBLIC + Modifier.PROTECTED) ==> {}",
                    "protected TableColumnModel JTableHeader.columnModel");
            Assert.assertNotNull("結果がnullでないこと", field);
            Assert.assertEquals("定義クラスはJTableHeaderであること", JTableHeader.class, field.getDeclaringClass());
            Assert.assertEquals("フィールド名はcolumnModeであること", "columnModel", field.getName());
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Field field = ClassUtils.findField(JTableHeader.class, "WIDTH", -1);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"WIDTH\", -1) ==> {}", "static int ImageObserver.WIDTH");
            Assert.assertNotNull("結果がnullでないこと", field);
            Assert.assertEquals("定義インターフェースはImageObserverであること", ImageObserver.class, field.getDeclaringClass());
            Assert.assertEquals("staticフィールド名はWIDTHであること", "WIDTH", field.getName());
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Field field = ClassUtils.findField(JTableHeader.class, "accessibleContext", -1);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"accessibleContext\", -1) ==> {}",
                    "protected AccessibleContext Component.accessibleContext");
            Assert.assertNotNull("結果がnullでないこと", field);
            Assert.assertEquals("定義クラスComponentであること", Component.class, field.getDeclaringClass());
            Assert.assertEquals("フィールド名はaccessibleContextであること", "accessibleContext", field.getName());
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Field field = ClassUtils.findField(JTableHeader.class, "defaultRenderer",
                    Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"defaultRenderer\"Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE) ==> {}",
                    "private TableCellRenderer JTableHeader.defaultRenderer");
            Assert.assertNotNull("結果がnullでないこと", field);
            Assert.assertEquals("定義クラスはJTableHeaderであること", JTableHeader.class, field.getDeclaringClass());
            Assert.assertEquals("フィールド名はdefaultRendererであること", "defaultRenderer", field.getName());
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Field field = ClassUtils.findField(JTableHeader.class, "graphicsConfig", Modifier.PRIVATE);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"defaultRenderer\", Modifier.PRIVATE) ==> {}",
                    "private TableCellRenderer JTableHeader.defaultRenderer");
            Assert.assertNotNull("結果がnullでないこと", field);
            Assert.assertEquals("定義クラスはComponentであること", Component.class, field.getDeclaringClass());
            Assert.assertEquals("フィールド名はgraphicsConfigであること", "graphicsConfig", field.getName());
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Field field = ClassUtils.findField(null, "graphicsConfig", Modifier.PRIVATE);
            logger.debug("ClassUtils.findField(null, \"defaultRenderer\", Modifier.PRIVATE) ==> {}", "null");
            Assert.assertNull("結果がnullであること", field);
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Field field = ClassUtils.findField(JTableHeader.class, null, Modifier.PRIVATE);
            logger.debug("ClassUtils.findField(JTableHeader.class, null, Modifier.PRIVATE) ==> {}", "null");
            Assert.assertNull("結果がnullであること", field);
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            ClassUtils.findField(JTableHeader.class, "graphicsConfig", Modifier.PROTECTED);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"defaultRenderer\", Modifier.PROTECTED) ==> {}",
                    "NoSuchFeildException");
            Assert.fail("NoSuchFieldException例外が発生すること");
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.assertTrue("NoSuchFieldException例外が発生すること", e instanceof NoSuchFieldException);
        }

        try {
            ClassUtils.findField(JTableHeader.class, "foo", -1);
            logger.debug("ClassUtils.findField(JTableHeader.class, \"foo\", -1) ==> {}", "NoSuchFeildException");
            Assert.fail("NoSuchFieldException例外が発生すること");
        } catch (NoSuchFieldException | SecurityException e) {
            Assert.assertTrue("NoSuchFieldException例外が発生すること", e instanceof NoSuchFieldException);
        }

        logger.debug("***** END testFindField");
    }

    /**
     * {@link ClassUtils#findMethod(Class, String, Class[], int)}メソッドをテストします。
     * <ul>
     * <li>
     *   引数に JTableHeader.class, "writeObject", new Class[] {ObjectOutputStream.class},
     *   Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE を指定すると private メソッド
     *   writeObject(java.io.ObjectOutputStream) を取得できること
     * </li>
     * <li>
     *    引数に JTableHeader.class, "getRecursivelyVisibleBounds", null, -1 を指定すると privateメソッド
     *    Component.getRecursivelyVisibleBounds() を取得できること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "toString", null, Modifier.PUBLIC を指定すると publicメソッド
     *   Component.toString() を取得できること
     * </li>
     * <li>
     *   引数に SortedMap.class, "entrySet", new Class[] {}, Modifier.PUBLIC を指定すると
     *   メソッド SortedMap.entrySet() を取得できること
     * </li>
     * <li>
     *   引数に SortedMap.class, "remove", new Class[] {Object.class, Object.class}, Modifier.PUBLIC を指定すると
     *   メソッド Map.remove(Object, Object) を取得できること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "addVetoableChangeListener", new Class[] {VetoableChangeListener.class},
     *   Modifier.PUBLIC を指定すると publicメソッド JComponent.addVetoableChangeListener(VetoableChangeListener)
     *    を取得できること
     * </li>
     * <li>
     *   引数に null, "getRecursivelyVisibleBounds", null, -1 を指定すると null が返ること
     * </li>
     * <li>
     *   引数に JTableHeader.class, null, null, -1 を指定すると null が返ること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "bar", null, -1 を指定すると NoSuchMethodException が発生すること
     * </li>
     * <li>
     *   引数に JTableHeader.class, "getRecursivelyVisibleBounds", null, Modifier.PUBLIC + Modifier.PROTECTED を
     *   指定すると NoSuchMethodException が発生すること
     * </li>
     * </ul>
     */
    @Test
    public void testFindMethod() {
        logger.debug("***** BEGIN testFindMethod");

        try {
            Method method = ClassUtils.findMethod(JTableHeader.class, "writeObject", new Class[] {ObjectOutputStream.class},
                    Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE);
            logger.debug("ClassUtils.findMethod(JTableHeader.class, \"writeObject\", new Class[] {ObjectOutputStream.class}, Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE) ==> {}",
                    "private void JTable.writeObject(java.io.ObjectOutputStream s)");
            Assert.assertNotNull("結果がnullでないこと", method);
            Assert.assertEquals("定義クラスはJTableHeaderであること", JTableHeader.class, method.getDeclaringClass());
            Assert.assertEquals("メソッド名はwriteObjectであること", "writeObject", method.getName());
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(JTableHeader.class, "getRecursivelyVisibleBounds", null, -1);
            logger.debug("ClassUtils.findMethod(JTableHeader.class, \"getRecursivelyVisibleBounds\", null, -1) ==> {}",
                    "private Rectangle Component.getRecursivelyVisibleBounds()");
            Assert.assertNotNull("結果がnullでないこと", method);
            Assert.assertEquals("定義クラスはComponentであること", Component.class, method.getDeclaringClass());
            Assert.assertEquals("メソッド名はgetRecursivelyVisibleBoundsであること", "getRecursivelyVisibleBounds", method.getName());
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(JTableHeader.class, "toString", null, Modifier.PUBLIC);
            logger.debug("ClassUtils.findMethod(JTableHeader.class, \"toString\", null, Modifier.PUBLIC) ==> {}",
                    "public String Component.toString()");
            Assert.assertNotNull("結果がnullでないこと", method);
            Assert.assertEquals("定義クラスはComponentであること", Component.class, method.getDeclaringClass());
            Assert.assertEquals("メソッド名はtoStringであること", "toString", method.getName());
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(SortedMap.class, "entrySet", new Class[] {}, Modifier.PUBLIC);
            logger.debug("ClassUtils.findMethod(SortedMap.class, \"entrySet\", new Class[] {}, Modifier.PUBLIC) ==> {}",
                    "Set<Map.Entry<K,V>> entrySet()");
            Assert.assertNotNull("結果がnullでないこと", method);
            Assert.assertEquals("定義インターフェースはSortedMapであること", SortedMap.class, method.getDeclaringClass());
            Assert.assertEquals("メソッド名はentrySetであること", "entrySet", method.getName());
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(SortedMap.class, "remove", new Class[] {Object.class, Object.class}, Modifier.PUBLIC);
            logger.debug("ClassUtils.findMethod(SortedMap.class, \"remove\", new Class[] {Object.class, Object.class}, Modifier.PUBLIC) ==> {}",
                    "boolean Map.remove(Object, Object)");
            Assert.assertNotNull("結果がnullでないこと", method);
            Assert.assertEquals("定義インターフェースはMapであること", Map.class, method.getDeclaringClass());
            Assert.assertEquals("メソッド名はremoveであること", "remove", method.getName());
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(JTableHeader.class, "addVetoableChangeListener",
                    new Class[] {VetoableChangeListener.class}, Modifier.PUBLIC);
            logger.debug("ClassUtils.findMethod(addVetoableChangeListener\", new Class[] {VetoableChangeListener.class}, Modifier.PUBLIC) ==> {}",
                    "public void JComponent.addVetoableChangeListener(VetoableChangeListener listener)");
            Assert.assertNotNull("結果がnullでないこと", method);
            Assert.assertEquals("定義クラスはJComponentであること", JComponent.class,
                    method.getDeclaringClass());
            Assert.assertEquals("メソッド名はaddVetoableChangeListenerであること", "addVetoableChangeListener", method.getName());
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(null, "getRecursivelyVisibleBounds", null, -1);
            logger.debug("ClassUtils.findMethod(null, \"getRecursivelyVisibleBounds\", null, -1) ==> {}", "null");
            Assert.assertNull("結果がnullであること", method);
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            Method method = ClassUtils.findMethod(JTableHeader.class, null, null, -1);
            logger.debug("ClassUtils.findMethod(JTableHeader.class, null, null, -1) ==> {}", "null");
            Assert.assertNull("結果がnullであること", method);
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.fail(e.toString());
        }

        try {
            ClassUtils.findMethod(JTableHeader.class, "bar", null, -1);
            logger.debug("ClassUtils.findMethod(JTableHeader.class, \"bar\", null, Modifier.PROTECTED) ==> {}",
                    "NoSuchMethodException");
            Assert.fail("NoSuchFieldException例外が発生すること");
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.assertTrue("NoSuchMethodException例外が発生すること", e instanceof NoSuchMethodException);
        }

        try {
            ClassUtils.findMethod(JTableHeader.class, "getRecursivelyVisibleBounds", null, Modifier.PUBLIC + Modifier.PROTECTED);
            logger.debug("ClassUtils.findMethod(JTableHeader.class, \"getRecursivelyVisibleBounds\", null, Modifier.PUBLIC + Modifier.PROTECTED) ==> {}",
                    "NoSuchMethodException");
            Assert.fail("NoSuchFieldException例外が発生すること");
        } catch (NoSuchMethodException | SecurityException e) {
            Assert.assertTrue("NoSuchMethodException例外が発生すること", e instanceof NoSuchMethodException);
        }

        logger.debug("***** END testFindMethod");
    }
}
