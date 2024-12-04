package com.oneinstep.starter.core.converter;

import java.util.List;

/**
 * 所有属性名和类型都相同的对象转换器
 * 属性名不相同也可以使用，但是属性名不同的属性值将无法拷贝
 **/
public interface SameAttributesConverter<A, B> {

    /**
     * B 转换成 A
     *
     * @param b B
     * @return A
     */
    A toA(B b);

    /**
     * A 转换成 B
     *
     * @param a A
     * @return B
     */
    B toB(A a);

    /**
     * copy 一个新的 A 对象
     * 深拷贝
     *
     * @param a A
     * @return new instance of A
     */
    A copyA(A a);

    /**
     * copy 一个新的 B 对象
     * 深拷贝
     *
     * @param b B
     * @return new instance of B
     */
    B copyB(B b);

    /**
     * list of A 转换成 list of B
     *
     * @param list of A
     * @return list of B
     */
    List<B> toListB(List<A> list);

    /**
     * list of B 转换成 list of A
     *
     * @param list of B
     * @return list of A
     */
    List<A> toListA(List<B> list);

}
