package com.huawei.analyze.analyze.controller;

import com.alibaba.fastjson.JSON;
import com.huawei.analyze.analyze.flink.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: likui
 * @Date: 2019/9/11 20:19
 * @Description:
 */
@RestController
@Slf4j
public class Test {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping("/test")
    public String product(){
        for(int i=0;i<1000;i++){
            kafkaTemplate.send("tmf",i+","+i);
        }
        return "123";
    }

    @KafkaListener(topics = "wiki-result")
    public void customer(String student){
        //Student student1 = (Student) JSON.toJavaObject(JSON.parseObject(student), Student.class);
        log.info(student);
    }
}
