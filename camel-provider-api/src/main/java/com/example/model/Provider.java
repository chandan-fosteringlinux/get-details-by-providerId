package com.example.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "providers")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partnerId;

    private String name;

    private String contactInfo;

    @ElementCollection
    @CollectionTable(name = "provider_supported_channels", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "channel")
    private List<String> supportedChannels;

    @Embedded
    private Sla sla;

    @Embeddable
    @Data
    public static class Sla {
        private int deliveryTimeMs;
        private double uptimePercent;
    }
}
