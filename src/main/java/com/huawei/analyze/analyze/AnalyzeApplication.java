package com.huawei.analyze.analyze;

import com.alibaba.fastjson.JSON;
import com.huawei.analyze.analyze.flink.SinkToMySQL;
import com.huawei.analyze.analyze.flink.Student;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.curator.org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class AnalyzeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyzeApplication.class, args);
        try {
            //writeToKafka();
            //testFlink();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String broker_list = "localhost:9092";
    private static final String topic = "student";  //kafka topic 需要和 flink 程序用同一个 topic

    public static void writeToKafka() throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        for (int i = 1; i <= 100; i++) {
            Student student = new Student(i, "zhisheng");
            ProducerRecord record = new ProducerRecord<String, String>(topic, null, null, JSON.toJSONString(student));
            producer.send(record);
            System.out.println("发送数据: " + JSON.toJSONString(student));
            //Thread.sleep(5 * 1000); //发送一条数据 sleep 10s，相当于 1 分钟 6 条
        }
        producer.flush();
    }

    public static void testFlink() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 非常关键，一定要设置启动检查点！！
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("zookeeper.connect", "localhost:2181");
        props.put("group.id", "metric-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "latest");

        SingleOutputStreamOperator<Student> student = env.addSource(new FlinkKafkaConsumer011<>(
                "student",   //这个 kafka topic 需要和上面的工具类的 topic 一致
                new SimpleStringSchema(),
                props)).setParallelism(1)
                .map(string -> JSON.toJavaObject(JSON.parseObject(string), Student.class)); //
        student.timeWindowAll(Time.seconds(5)).apply(new AllWindowFunction<Student, List<Student>, TimeWindow>() {
            @Override
            public void apply(TimeWindow window, Iterable<Student> values, Collector<List<Student>> out) throws Exception {
                ArrayList<Student> students = Lists.newArrayList(values);
                if (students.size() > 0) {
                    System.out.println("5S内收集到 student 的数据条数是：" + students.size());
                    out.collect(students);
                }
            }
        }).addSink(new SinkToMySQL());

        env.execute("flink learning connectors kafka");
    }

}
