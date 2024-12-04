package com.oneinstep.starter.core.dao;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 分页查询转换工具
 * PageHelper 会拦截查询SQL，为其动态增加一些分页SQL语句和排序SQL语句。
 */
public class PageConverter {

    private PageConverter() {
    }

    /**
     * 普通分页转换
     *
     * @param sourcePage 查询返回的结果对象
     * @return 分页对象
     */
    public static <T> Page<T> convert(PageData<?> sourcePage) {
        Page<T> page = new Page<>();
        page.setCurrent(sourcePage.getPageNo());
        page.setSize(sourcePage.getPageSize());
        page.setSearchCount(sourcePage.getNeedTotalCount());
        List<OrderItem> orderItemList = new ArrayList<>();
        if (StringUtils.isNotEmpty(sourcePage.getOrderBy())) {

            String orderBy = sourcePage.getOrderBy();
            String order = sourcePage.getOrder();

            String[] orderByArray = orderBy.split(" ");
            OrderItem orderItem = new OrderItem();
            if (orderByArray.length == 2) {
                orderBy = orderByArray[0].trim();
                order = orderByArray[1].trim();
            }

            orderItem.setColumn(orderBy);
            boolean isAsc = "ASC".equalsIgnoreCase(order);
            orderItem.setAsc(isAsc);

            orderItemList.add(orderItem);
        }

        page.setOrders(orderItemList);

        return page;
    }


    /**
     * copy分页对象，不拷贝数据
     *
     * @param sourcePage 源拷贝对象
     * @param <S>        源数据类型
     * @param <T>        结果数据类型
     * @return 拷贝结果
     */
    public static <S, T> PageData<T> copyWithoutData(Page<S> sourcePage) {
        PageData<T> targetPage = new PageData<>();
        if (sourcePage != null) {
            targetPage.setPageNo((int) sourcePage.getCurrent());
            targetPage.setPageSize((int) sourcePage.getSize());
            targetPage.setTotalCount(sourcePage.getTotal());
            targetPage.setNeedTotalCount(sourcePage.searchCount());
            targetPage.setNeedData(true);
            List<OrderItem> orderItemList = sourcePage.orders();

            StringBuilder order = new StringBuilder();
            StringBuilder orderBy = new StringBuilder();
            if (CollectionUtils.isNotEmpty(orderItemList)) {
                for (int i = 0; i < orderItemList.size(); i++) {
                    OrderItem orderItem = orderItemList.get(i);
                    order.append(orderItem.getColumn());

                    boolean isAsc = orderItem.isAsc();
                    if (isAsc) {
                        orderBy.append(orderItem.getColumn()).append(" ").append("ASC");
                    } else {
                        orderBy.append(orderItem.getColumn()).append(" ").append("DESC");
                    }

                    if (i != orderItemList.size() - 1) {
                        order.append(",");
                        orderBy.append(",");
                    }
                }

                targetPage.setOrder(order.toString());
                targetPage.setOrderBy(orderBy.toString());
            }
            targetPage.setDataList(new ArrayList<>());
        }

        return targetPage;
    }

}
