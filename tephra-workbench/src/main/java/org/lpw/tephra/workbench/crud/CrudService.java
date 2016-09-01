package org.lpw.tephra.workbench.crud;

import net.sf.json.JSONObject;

/**
 * @author lpw
 */
public interface CrudService {
    /**
     * 选择记录验证器Bean名称。
     */
    String SELECT_VALIDATOR = "tephra.workbench.crud.validator.select";

    /**
     * 获取UI定义。
     *
     * @param uri URI地址。
     * @return UI定义。
     */
    JSONObject metadata(String uri);

    /**
     * 检索数据。
     *
     * @param uri        URI地址。
     * @param pageSize   每页显示记录数。
     * @param pageNumber 当前显示页数。
     * @return 数据。
     */
    JSONObject query(String uri, int pageSize, int pageNumber);

    /**
     * 新增数据。
     *
     * @param uri URI地址。
     * @return 数据。
     */
    JSONObject create(String uri);

    /**
     * 获取数据。
     *
     * @param uri URI地址。
     * @param id  数据ID值。
     * @return 数据。
     */
    JSONObject modify(String uri, String id);

    /**
     * 保存数据。
     *
     * @param uri URI地址。
     * @return 验证结果；如果验证不通过则返回错误信息，否则返回null。
     */
    Object save(String uri);

    /**
     * 删除数据。
     *
     * @param uri URI地址。
     * @param id  数据ID值。
     */
    void delete(String uri, String id);
}
