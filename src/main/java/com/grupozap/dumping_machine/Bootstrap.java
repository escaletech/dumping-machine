package com.grupozap.dumping_machine;

import com.grupozap.dumping_machine.config.ApplicationProperties;
import com.grupozap.dumping_machine.streamers.KafkaStreamer;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

class Bootstrap {
    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Bootstrap.class);
        String properties = System.getProperty("config");

        if( properties == null ) {
            System.out.println( "Usage: -Dconfig=<file.yml>" );
            return;
        }

        ApplicationProperties applicationProperties = null;
        Yaml yaml = new Yaml();

        try( InputStream in = Files.newInputStream( Paths.get( properties ) ) ) {
            applicationProperties = yaml.loadAs( in, ApplicationProperties.class );
        } catch (IOException e) {
            logger.error("Config error.", e);
        }

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, applicationProperties.getSchemaRegistryUrl());

        KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);

        String schemaString = "{\"type\": \"record\",\"name\": \"value\",\"fields\":[{\"type\": \"string\",\"name\": \"name\"}]}}";

        Schema avroKeySchema = new Schema.Parser().parse(schemaString);
        Schema avroValueSchema = new Schema.Parser().parse(schemaString);

        for (int k = 0; k < 10; k++) {
            GenericRecord thisKeyRecord = new GenericData.Record(avroKeySchema);
            GenericRecord thisValueRecord = new GenericData.Record(avroValueSchema);

            thisValueRecord.put("name", Integer.toString(k));
            thisKeyRecord.put("name", Integer.toString(k));

            System.out.println("Sending record " + k);
            producer.send(new ProducerRecord<>("test-dumping-machine", thisKeyRecord, thisValueRecord));
        }

        producer.flush();
        producer.close();

        System.out.println("TESTING");
    }
}
