package com.bookstore.component;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TestUtil {
    private static final DockerImageName KAFKA_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.0.0");

    static void startKafka() {
        KafkaContainer kafka = new KafkaContainer(KAFKA_TEST_IMAGE);
        kafka.setPortBindings(Arrays.asList("9092:9092", "9093:9093", "2181:2181"));
        kafka.start();
    }

    static void produceEvent(String topic, String message) throws ExecutionException, InterruptedException {
        KafkaProducer<String, String> producer = null;
        try {
            producer = new KafkaProducer<>(
                    ImmutableMap.of(
                            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093",
                            ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                    ),
                    new StringSerializer(),
                    new StringSerializer()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        producer.send(new ProducerRecord<>(topic, message)).get();
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
    }

    static String read(String filePath) throws IOException {
        var file = ResourceUtils.getFile(filePath);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
