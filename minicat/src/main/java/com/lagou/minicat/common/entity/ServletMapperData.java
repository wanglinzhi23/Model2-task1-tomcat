package com.lagou.minicat.common.entity;

import com.lagou.servletx.MyServlet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Pattern;

/**
 * servlet映射
 *
 * @author wlz
 * @date 2020/5/12
 */
@Data
@AllArgsConstructor
public class ServletMapperData {
    private Pattern urlPattern;
    private MyServlet myServlet;
}
