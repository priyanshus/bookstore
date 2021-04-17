package com.bookstore.component.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public abstract class TestUtil {
    public static KafkaConsumer<String, String> consumer;

    static {
        startKafkaServices();
        connectKafkaConsumer();
    }

    private static void startKafkaServices() {
        log.info("########### Starting Kafka Services ################");
        DockerComposeContainer composeContainer = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                .withExposedService("kafka_1", 9092, Wait.forListeningPort())
                .withLocalCompose(true);
        composeContainer.start();
    }

    protected static void produceEvent(String topic, String message) throws ExecutionException, InterruptedException {
        KafkaProducer<String, String> producer = null;
        try {
            producer = new KafkaProducer<>(
                    ImmutableMap.of(
                            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
                    ),
                    new StringSerializer(),
                    new StringSerializer()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


        producer.send(new ProducerRecord<>(topic, message)).get();
        log.info("Produced event on topic {}", topic);
    }

    private static void connectKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-consumer");
        props.put("client.id", "test-client");
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "500");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
    }

    protected static String read(String filePath) throws IOException {
        File file = ResourceUtils.getFile(filePath);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
