package org.lpw.tephra.workbench.ui;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.dao.model.Model;

import java.util.List;
import java.util.Set;

/**
 * @author lpw
 */
public interface UiHelper {
    /**
     * 获取菜单集。
     *
     * @return 菜单集。
     */
    List<Menu> getMenus();

    /**
     * 获取UI数据元定义。
     *
     * @param uri URI地址。
     * @return UI数据元定义；如果不存在则返回null。
     */
    JSONObject getMetadata(String uri);

    /**
     * 获取Model类定义。
     *
     * @param uri URI地址。
     * @return Model类定义；如果不存在则返回null。
     */
    Class<? extends Model> getModelClass(String uri);

    /**
     * 获取可编辑属性名称集。
     *
     * @param uri URI地址。
     * @return 可编辑属性名称集。
     */
    Set<String> getEditables(String uri);

    /**
     * 获取验证规则集。
     *
     * @param uri URI地址。
     * @return 验证规则集。
     */
    Validate[] getValidates(String uri);

    /**
     * 验证URI对应的Model是否为StatusModel实例。
     *
     * @param uri URI地址。
     * @return 如果是则返回true；否则返回false。
     */
    boolean isStatus(String uri);

    /**
     * 验证URI对应的Model是否为DomainModel实例。
     *
     * @param uri URI地址。
     * @return 如果是则返回true；否则返回false。
     */
    boolean isDomain(String uri);
}
