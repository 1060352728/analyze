package com.huawei.analyze.analyze.flink;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.List;

/**
 * @Auther: likui
 * @Date: 2019/9/17 22:37
 * @Description:
 */
public class SinkToMySQL extends RichSinkFunction<List<Student>> {
    @Override
    public void invoke(List<Student> value, Context context) throws Exception {
        //遍历数据集合
        for (Student student : value) {
            System.out.println("====================================" + student + "==============================");
        }

    }

}
