package org.lpw.tephra.dao.orm;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.page-list")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PageListImpl<T extends Model> implements PageList<T> {
    @Inject
    private ModelHelper modelHelper;
    private List<T> list;
    private int count;
    private int size;
    private int number;
    private int page;
    private int pageStart;
    private int pageEnd;

    @Override
    public PageList<T> setPage(int count, int size, int number) {
        this.count = Math.max(0, count);
        this.size = Math.max(1, size);
        this.number = Math.min(number, this.count / this.size + (this.count % this.size == 0 ? 0 : 1));
        this.number = Math.max(1, this.number);
        page = Math.max(1, this.count / this.size + (this.count % this.size == 0 ? 0 : 1));
        pageStart = Math.max(1, this.number - 9);
        pageEnd = Math.min(page, pageStart + 19);

        return this;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageStart() {
        return pageStart;
    }

    @Override
    public int getPageEnd() {
        return pageEnd;
    }

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public JSONObject toJson() {
        return toJson(true);
    }

    @Override
    public JSONObject toJson(BiConsumer<T, JSONObject> biConsumer) {
        JSONObject object = toJson(false);
        object.put("list", modelHelper.toJson(this.list, biConsumer));

        return object;
    }

    @Override
    public JSONObject toJson(Function<T, JSONObject> function) {
        JSONObject object = toJson(false);
        object.put("list", modelHelper.toJson(this.list, function));

        return object;
    }

    private JSONObject toJson(boolean listable) {
        JSONObject object = new JSONObject();
        object.put("count", count);
        object.put("size", size);
        object.put("number", number);
        object.put("page", page);
        object.put("pageStart", pageStart);
        object.put("pageEnd", pageEnd);
        if (listable)
            object.put("list", modelHelper.toJson(list));

        return object;
    }
}
