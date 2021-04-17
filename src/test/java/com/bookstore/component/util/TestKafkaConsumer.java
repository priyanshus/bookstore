package com.bookstore.component.util;

import kafka.utils.ShutdownableThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class TestKafkaConsumer extends ShutdownableThread {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private int messageRemaining;
    private final CountDownLatch latch;
    private List<String> messagesReceived;

    public TestKafkaConsumer(KafkaConsumer<String, String> consumer, String topic, final int numMessageToConsume,
                             final CountDownLatch latch) {
        super("Test Consumer", false);
        this.topic = topic;
        this.messageRemaining = numMessageToConsume;
        this.latch = latch;
        messagesReceived = Collections.synchronizedList(new ArrayList<String>());
        this.consumer = consumer;
    }

    @Override
    public void doWork() {
        log.info("########## Polling for event ###############");
        consumer.subscribe(Collections.singletonList(this.topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
            for (ConsumerRecord<String, String> record : partitionRecords) {
                log.info(String.format("Received Message on Kafka Topic - %s Offset - %s", this.topic, record.offset()));
                messagesReceived.add(record.value());
            }
            long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
            consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
            messageRemaining -= records.count();
            if (messageRemaining <= 0) {
                latch.countDown();
            }
        }
    }

    public List<String> getMessagesReceived() {
        return messagesReceived;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
