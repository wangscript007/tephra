package org.lpw.tephra.dao.orm.hibernate;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.dao.orm.TestModel;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Converter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
public class HibernateOrmTest extends DaoTestSupport {
    @Inject
    private Converter converter;
    @Inject
    private HibernateOrm hibernateOrm;

    @Test
    public void crud() {
        PageList<TestModel> pl = hibernateOrm.query(new HibernateQuery(TestModel.class), null);
        Assert.assertNotNull(pl);
        Assert.assertEquals(0, pl.getCount());
        Assert.assertNotNull(pl.getList());
        Assert.assertTrue(pl.getList().isEmpty());

        TestModel model1 = new TestModel();
        model1.setSort(1);
        model1.setName("HibernateOrm");
        hibernateOrm.save(model1);
        hibernateOrm.close();
        Assert.assertNotNull(model1.getId());
        Assert.assertEquals(36, model1.getId().length());

        TestModel model2 = hibernateOrm.findById(TestModel.class, model1.getId());
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode());
        Assert.assertEquals(model1.getId(), model2.getId());
        Assert.assertEquals(1, model2.getSort());
        Assert.assertEquals("HibernateOrm", model2.getName());
        hibernateOrm.close();

        TestModel model3 = new TestModel();
        model3.setId(model1.getId());
        model3.setName("new name");
        hibernateOrm.save(model3);
        TestModel model4 = hibernateOrm.findById(TestModel.class, model1.getId());
        Assert.assertEquals(model1.getId(), model4.getId());
        Assert.assertEquals(0, model4.getSort());
        Assert.assertEquals("new name", model4.getName());
        hibernateOrm.close();

        hibernateOrm.delete(model1);
        Assert.assertNull(hibernateOrm.findById(TestModel.class, model1.getId()));
        hibernateOrm.close();

        pl = hibernateOrm.query(new HibernateQuery(TestModel.class), null);
        Assert.assertNotNull(pl);
        Assert.assertEquals(0, pl.getCount());
        Assert.assertNotNull(pl.getList());
        Assert.assertTrue(pl.getList().isEmpty());

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setSort(i);
            model.setName("name" + i);
            hibernateOrm.save(model);
            list.add(model.hashCode());
        }
        pl = hibernateOrm.query(new HibernateQuery(TestModel.class).where("c_sort<?").order("c_sort"), new Object[]{5});
        Assert.assertNotNull(pl);
        Assert.assertEquals(0, pl.getCount());
        Assert.assertEquals(5, pl.getList().size());
        for (int i = 0; i < 5; i++) {
            TestModel model = pl.getList().get(i);
            Assert.assertEquals(36, model.getId().length());
            Assert.assertEquals(i, model.getSort());
            Assert.assertEquals("name" + i, model.getName());
            Assert.assertEquals(converter.toInt(list.get(i)), model.hashCode());
        }

        hibernateOrm.close();
    }
}
