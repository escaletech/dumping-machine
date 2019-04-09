package com.grupozap.dumping_machine.config;

import java.util.List;
import java.util.Objects;

public class ApplicationProperties {
    private String bootstrapServers;
    private String schemaRegistryUrl;
    private String groupId;
    private Integer sessionTimeout;
    private List<TopicProperties> topics;

    public List<TopicProperties> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicProperties> topics) {
        this.topics = topics;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public void setSchemaRegistryUrl(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getSessionTimeout() {
        return Objects.requireNonNullElse(sessionTimeout, 30000);
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}