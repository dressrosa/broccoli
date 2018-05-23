/**
 * 唯有看书,不庸不扰
 */
package com.xiaoyu.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2017年3月24日下午5:09:40
 * 
 * @author xiaoyu
 * @description <b> 跷跷板队列 </b> <br/>
 *              |-----------------------------------------------------------|
 *              <br/>
 *              |-----------------------------------------------------------|
 *              <br/>
 *              | farLeft ←ln2←ln1←<b>Pivot</b>→rn1→rn2→farRight | <br/>
 *              | -----------------------------------------------------------|
 *              <br/>
 *              | -----------------------------------------------------------|
 *              <br/>
 *              | -----------------------------------------------------------|
 *              <br/>
 *              以支点为中心,左右俩边入队,左右俩边互不影响,每个节点持有她的前驱的引用 <br/>
 *              offer类api都会在出对之后删除节点<br/>
 *              在cglib代理里面,我们使用<b>SeesawQueue</b>在左边存储
 *              {@link com.xiaoyu.config.annotation.aop.After}注解代理, 在右边存储
 *              {@link com.xiaoyu.config.annotation.aop.Before}注解代理
 */
public class SeesawQueue<V> {

    /**
     * 暂时只提供支点的功能,
     */
    private Node pivot;// 支点
    private final AtomicInteger count = new AtomicInteger(0);// 节点数量 不包括支点

    /**
     * 最左节点
     */
    private Node farLeft;
    /**
     * 最右节点
     */
    private Node farRight;

    public SeesawQueue() {
        this(null);
    }

    public SeesawQueue(V value) {
        pivot = new Node(value);
    }

    /**
     * 节点
     */
    private class Node {
        private final V value;// 节点值
        private Node prev;// 前驱

        public Node(V value) {
            this.value = value;
        }

    }

    /**
     * 左边入队
     */
    public void pushLeft(V value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        Node node = new Node(value);
        if (farLeft == null) {
            node.prev = pivot;
        } else {
            node.prev = farLeft;
        }
        farLeft = node;
        this.incr();
    }

    /**
     * 右边入队
     */
    public void pushRight(V value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        Node node = new Node(value);
        if (farRight == null) {
            node.prev = pivot;
        } else {
            node.prev = farRight;
        }
        farRight = node;
        this.incr();
    }

    private void incr() {
        count.incrementAndGet();
    }

    private void decr() {
        count.decrementAndGet();
    }

    /**
     * 从右边先出队,右边出完再从左边出对
     * 
     * @return 节点值
     */
    public V offer() {
        V value = null;
        Node prev = null;
        if (farRight != null) {
            Node node = farRight;
            if (node.prev != pivot) {
                prev = node.prev;
            } else {
                prev = null;
            }
            farRight = prev;
            value = node.value;
            this.decr();
        } else if (farLeft != null) {
            Node node = farLeft;
            if (node.prev != pivot) {
                prev = node.prev;
            } else {
                prev = null;
            }
            farLeft = prev;
            value = node.value;
            this.decr();
        }
        return value;
    }

    public boolean hasElements() {
        return count.get() == 0 ? false : true;
    }

    public int size() {
        return count.get();
    }

    /**
     * 返回支点值,支点会被删除
     */
    public V offerPivot() {
        if (farLeft == null && farRight == null && pivot != null) {
            V value = pivot.value;
            pivot = null;
            return value;
        }
        return null;
    }

    /**
     * 左边出队
     * 
     * @return 节点值
     */
    public V offerLeft() {
        V value = null;
        Node prev = null;
        if (farLeft != null) {
            Node node = farLeft;
            if (node.prev != pivot) {
                prev = node.prev;
            } else {
                prev = null;
            }
            farLeft = prev;
            value = node.value;
            this.decr();
        }
        return value;
    }

    /**
     * 右边出队
     * 
     * @return 节点值
     */
    public V offerRight() {
        V value = null;
        Node prev = null;
        if (farRight != null) {
            Node node = farRight;
            if (node.prev != pivot) {
                prev = node.prev;
            } else {
                prev = null;
            }
            farRight = prev;
            value = node.value;
            this.decr();
        }
        return value;
    }
}
