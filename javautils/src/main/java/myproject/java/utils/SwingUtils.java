package myproject.java.utils;

import java.awt.Component;
import java.awt.Container;

/**
 * SwingのComponentに対するユーティリティ機能を提供します。
 * <p>
 * この実装は、性能について考慮されていないため実際に適用する場合は適宜ソースコードの
 * 改変を検討してください。
 * 
 * Copyright ycookjp
 * https://github.com/ycookjp/
 */
public class SwingUtils {
    /**
     * 外部空のインスタンスかを抑止するためのコンストラクタ。
     */
    private SwingUtils() { }

    /**
     * {@link Component}の名前を指定して、{@link Container}に保持されている{@link Component}を
     * 取得します。引数で指定された{@link Container}が保持する{@link Component}から指定された
     * 名前のものが見つからなかった場合は、再帰的に{@link Component}を検索します。
     * <dl>
     * <dt>JFrameからComponentを取得する例</dt>
     * <dd>
     *   JFrameの中に保持されている{@link Component}を取得する場合は、{@link Container}に
     *   JFrameのcontentPainを指定して、{@link Component}を取得します。
     *   <table border="1"><caption>コードの例</caption><tr><td><pre>
     * JFrame frame = new JFrame();
     * ...
     * Component component = SwingUtils.getComponentByName(frame.getComponentPane(), "MyComponentName");
     * ...
     *   </pre></td></tr></table>
     * </dd>
     * </dl>
     * @param container 取得対象の{@link Component}を保持している{@link Container}
     * @param name 検索対象の{@link Container}の名前
     * @return 取得した{@link Container}のインスタンスを返します。
     *     引数container、またはnameにnullが指定された場合はnullを返します。
     *     指定された名前の{@link Container}が見つからなかった場合はnullを返します。
     */
    public static Component getComponentByName(Container container, String name) {
        Component result = null;
        if (container == null || name == null) {
            return null;
        }

        Component[] components = container.getComponents();
        for (Component component : components) {
            String componentName = component.getName();
            if (name.equals(componentName)) {
                // 指定された名前のComponentが見つかったらそのコンポーネントを返す。
                result = component;
                break;
            }
        }

        if (result == null) {
            // Container直下のComponentから見つからなかった場合は、再帰的にComponentを取得する
            for (Component component  : components) {
                if (component instanceof Container) {
                    Container childContainer = (Container) component;
                    Component child = getComponentByName(childContainer, name);
                    if (child != null && name.equals(child.getName())) {
                        result = child;
                        break;
                    }
                }
            }
        }

        return result;
    }
}
