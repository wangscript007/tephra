package org.lpw.tephra.workbench.crud;

import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.workbench.Suffix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller("tephra.workbench.crud" + Suffix.CTRL)
public class CrudCtrl {
    @Autowired
    protected Request request;
    @Autowired
    protected Templates templates;
    @Autowired
    protected CrudService crudService;

    @Execute(name = ".+metadata$")
    public Object ui() {
        return crudService.metadata(request.getUri());
    }

    @Execute(name = ".+query$")
    public Object query() {
        return crudService.query(request.getUri(), request.getAsInt("pageSize"), request.getAsInt("pageNumber"));
    }

    @Execute(name = ".+create$")
    public Object create() {
        return crudService.create(request.getUri());
    }

    @Execute(name = ".+modify$", validates = {@Validate(validator = CrudService.SELECT_VALIDATOR, parameter = "id", failureCode = 8001, failureArgKeys = {"tephra.workbench.modify"})})
    public Object modify() {
        return crudService.modify(request.getUri(), request.get("id"));
    }

    @Execute(name = ".+save$")
    public Object save() {
        Object object = crudService.save(request.getUri());

        return object == null ? templates.get().success("success", "tephra.workbench.save.success") : object;
    }

    @Execute(name = ".+delete$", validates = {@Validate(validator = CrudService.SELECT_VALIDATOR, parameter = "id", failureCode = 8021, failureArgKeys = {"tephra.workbench.delete"})})
    public Object delete() {
        crudService.delete(request.getUri(), request.get("id"));

        return templates.get().success(null, "tephra.workbench.delete.success");
    }
}
