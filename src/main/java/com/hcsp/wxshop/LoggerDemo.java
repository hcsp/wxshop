package com.hcsp.wxshop;

// simple log facade for java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class LoggerDemo {
    // a.b.c.d 是 a.b.c 的孩子 是a.b的孩子 是a的孩子，ROOT logger的孩子
    private static Logger logger1 = LoggerFactory.getLogger("com");
    private static Logger logger2 = LoggerFactory.getLogger("com.hcsp");
    // 等价于 com.hcsp.wxshop.LoggerDemo 一个logger
    private static Logger logger3 = LoggerFactory.getLogger(LoggerDemo.class);

    private static Logger logger4 = LoggerFactory.getLogger("org");


    public static void main(String[] args) {
        RuntimeException e = new RuntimeException();
        logger1.debug("1 {} {} {}", new HashMap<>(), 1, new Object());
        logger1.info("1 {}", new ArrayList<>());
        logger1.error("1", e);
        logger2.debug("1 {}", new HashMap<>());
        logger2.info("1 {}", new ArrayList<>());
        logger2.error("1", e);
        logger3.debug("4 {}", new HashMap<>());
        logger3.info("4 {}", new ArrayList<>());
        logger3.error("4", e);

        logger4.info("I'm org!");
    }
}
