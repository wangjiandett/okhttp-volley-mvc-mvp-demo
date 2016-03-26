package cn.ieclipse.af.view.expendview;

import java.io.Serializable;

/**
 * popView 的一级菜单显示item数据模型
 * <p>
 * 其他字段请继承后自行添加
 * </p>
 *
 * @author czw
 * @date 2015年12月4日
 */
public class BasePopItem implements Serializable {

    /**
     * 一级菜单的item点击后无二级菜单显示
     * true：默认有，false ：无
     */
    public boolean hasChildList = true;

}
