package org.lpw.tephra.bean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class ContainerTest {
    private static List<Integer> contextRefreshedList = new ArrayList<>();

    @Autowired
    protected Converter converter;

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
