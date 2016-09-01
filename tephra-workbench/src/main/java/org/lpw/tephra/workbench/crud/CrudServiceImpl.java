package org.lpw.tephra.workbench.crud;

import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Validator;
import org.lpw.tephra.workbench.Suffix;
import org.lpw.tephra.workbench.domain.DomainService;
import org.lpw.tephra.workbench.model.DomainModel;
import org.lpw.tephra.workbench.model.InitableModel;
import org.lpw.tephra.workbench.model.StatusModel;
import org.lpw.tephra.workbench.ui.SearchType;
import org.lpw.tephra.workbench.ui.UiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Service("tephra.workbench.crud" + Suffix.SERVICE)
public class CrudServiceImpl implements CrudService {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected ModelHelper modelHelper;
    @Autowired
    protected ModelTables modelTables;
    @Autowired
    protected Request request;
    @Autowired
    protected Validators validators;
    @Autowired
    protected UiHelper uiHelper;
    @Autowired
    protected DomainService domainService;
    @Autowired
    protected CrudDao crudDao;

    @Override
    public JSONObject metadata(String uri) {
        return uiHelper.getMetadata(getUri(uri));
    }

    @Override
    public JSONObject query(String uri, int pageSize, int pageNumber) {
        uri = getUri(uri);
        List<Object[]> list = new ArrayList<>();
        if (uiHelper.isDomain(uri))
            list.add(new Object[]{"domain", SearchType.Equals, domainService.get().getId()});
        if (uiHelper.isStatus(uri))
            list.add(new Object[]{"status", SearchType.Equals, 0});

        return crudDao.query(uiHelper.getModelClass(uri), list, pageSize, pageNumber).toJson();
    }

    @Override
    public JSONObject create(String uri) {
        return modelHelper.toJson(newModel(uri));
    }

    @Override
    public JSONObject modify(String uri, String id) {
        Model model = findById(uri, id);

        return model == null ? null : modelHelper.toJson(model);
    }

    @Override
    public Object save(String uri) {
        String key = getUri(uri);
        Validate[] validates = uiHelper.getValidates(key);
        if (!validator.isEmpty(validates)) {
            Object object = this.validators.validate(validates, null);
            if (object != null)
                return object;
        }

        String id = request.get("id");
        Model model = validator.isEmpty(id) ? newModel(uri) : findById(uri, id);
        ModelTable modelTable = modelTables.get(uiHelper.getModelClass(key));
        uiHelper.getEditables(key).forEach(name -> modelTable.set(model, name, request.get(converter.toFirstLowerCase(name))));
        crudDao.save(model);

        return null;
    }

    protected Model newModel(String uri) {
        Model model = BeanFactory.getBean(uiHelper.getModelClass(getUri(uri)));
        if (model instanceof InitableModel)
            ((InitableModel) model).init();
        if (model instanceof DomainModel)
            ((DomainModel) model).setDomain(domainService.get().getId());

        return model;
    }

    @Override
    public void delete(String uri, String id) {
        Model model = findById(uri, id);
        if (model == null || !(model instanceof StatusModel))
            return;

        ((StatusModel) model).setStatus(1);
        crudDao.save(model);
    }

    protected Model findById(String uri, String id) {
        return crudDao.findById(uiHelper.getModelClass(getUri(uri)), id);
    }

    protected String getUri(String uri) {
        return uri.substring(0, uri.lastIndexOf('/') + 1);
    }
}
