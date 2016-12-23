package org.lpw.tephra.dao.orm.mybatis;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.dao.orm.TestModel;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

/**
 * @author lpw
 */
public class MybatisOrmTest extends DaoTestSupport{
    @Inject
    private Generator generator;
    @Inject
    private MybatisOrm mybatisOrm;

    @Test
    public void crudByMapper() {
        TestMapper mapper = mybatisOrm.getMapper(null, Mode.Write, TestMapper.class);
        Assert.assertNotNull(mapper);
        Assert.assertNull(mapper.findById("id"));

        TestModel model1 = new TestModel();
        model1.setId(generator.uuid());
        model1.setSort(1);
        model1.setName("MyBatis");
        Assert.assertEquals(1, mapper.insert(model1));
        TestModel model2 = mapper.findById(model1.getId());
        Assert.assertNotNull(model2);
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode());
        Assert.assertEquals(model1.getSort(), model2.getSort());
        Assert.assertEquals(model1.getName(), model2.getName());

        TestModel model3 = new TestModel();
        model3.setId(model1.getId());
        model3.setName("new name");
        Assert.assertEquals(1, mapper.update(model3));
        TestModel model4 = mapper.findById(model1.getId());
        Assert.assertNotNull(model4);
        Assert.assertEquals(model1.getSort(), model4.getSort());
        Assert.assertEquals(model3.getName(), model4.getName());

        Assert.assertEquals(1, mapper.delete(model1.getId()));
        Assert.assertNull(mapper.findById(model1.getId()));

        List<TestModel> list = mapper.selectAll();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());

        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setId("id" + i);
            model.setSort(i);
            model.setName("name" + i);
            Assert.assertEquals(1, mapper.insert(model));
        }
        checkList(mapper.selectAll());

        close();
    }

    @Test
    public void crudByStatement() {
        crudUseStatement("");
    }

    @Test
    public void crudByXml() {
        crudUseStatement("FromXml");
    }

    protected void crudUseStatement(String suffix) {
        Assert.assertNull(mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter("id")));

        TestModel model1 = new TestModel();
        model1.setId(generator.uuid());
        model1.setSort(1);
        model1.setName("MyBatis");
        Assert.assertEquals(1, mybatisOrm.insert(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.insert" + suffix).parameter(model1)));
        TestModel model2 = mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter(model1.getId()));
        Assert.assertNotNull(model2);
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode());
        Assert.assertEquals(model1.getSort(), model2.getSort());
        Assert.assertEquals(model1.getName(), model2.getName());

        TestModel model3 = new TestModel();
        model3.setId(model1.getId());
        model3.setName("new name");
        Assert.assertEquals(1, mybatisOrm.update(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.update" + suffix).parameter(model3)));
        TestModel model4 = mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter(model1.getId()));
        Assert.assertNotNull(model4);
        Assert.assertEquals(model1.getSort(), model4.getSort());
        Assert.assertEquals(model3.getName(), model4.getName());

        Assert.assertEquals(1, mybatisOrm.delete(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.delete" + suffix).parameter(model1.getId())));
        Assert.assertNull(mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter(model1.getId())));

        List<TestModel> list = mybatisOrm.selectList(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.selectAll" + suffix));
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());

        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setId("id" + i);
            model.setSort(i);
            model.setName("name" + i);
            Assert.assertEquals(1, mybatisOrm.insert(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.insert" + suffix).parameter(model)));
        }
        checkList(mybatisOrm.selectList(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.selectAll" + suffix)));

        close();
    }

    protected void checkList(List<TestModel> list) {
        Assert.assertEquals(10, list.size());
        for (int i = 0; i < 10; i++) {
            TestModel model = list.get(i);
            Assert.assertEquals("id" + i, model.getId());
            Assert.assertEquals(i, model.getSort());
            Assert.assertEquals("name" + i, model.getName());
        }
    }
}
