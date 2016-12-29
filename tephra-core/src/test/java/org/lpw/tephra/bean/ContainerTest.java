package org.lpw.tephra.bean;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Converter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lpw
 */
public class ContainerTest extends CoreTestSupport {
    private static List<Integer> contextRefreshedList = new ArrayList<>();

    @Inject
    private Container container;
    @Inject
    private Converter converter;

    @Test
    public void getBeanByClass() {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(container.getBean(SingletonBean.class).hashCode());
        Assert.assertEquals(1, set.size());

        set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(container.getBean(PrototypeBean.class).hashCode());
        Assert.assertEquals(10, set.size());

        Assert.assertNull(container.getBean(NullBean.class));
    }

    @Test
    public void contextRefreshed() {
        Assert.assertEquals(2, contextRefreshedList.size());
        for (int i = 0; i < contextRefreshedList.size(); i++)
            Assert.assertEquals(i + 1, converter.toInt(contextRefreshedList.get(i)));
    }

    public static void runContextRefreshListener(int sort) {
        contextRefreshedList.add(sort);
    }
}
