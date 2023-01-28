package com.dezhi.common.util;

import com.dezhi.common.util.concurrent.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/** 集合工具类测试
 * @author liaodezhi
 * @date 2023/1/28
 */
public class CollectionUtilsTest {

    /**
     * 测试集合判空方法
     */
    @Test
    void test_is_empty() {
        List<Integer> list1 = null;
        boolean result1 = CollectionUtils.isEmpty(list1);
        Assertions.assertTrue(result1);
        List<Integer> list2 = new ArrayList<>();
        boolean result2 = CollectionUtils.isEmpty(list2);
        Assertions.assertTrue(result2);
        list2.add(1);
        boolean result3 = CollectionUtils.isEmpty(list2);
        Assertions.assertFalse(result3);
    }
}
