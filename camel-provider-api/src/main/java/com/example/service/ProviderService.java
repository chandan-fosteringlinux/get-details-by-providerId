package com.example.service;

import java.util.List;

import com.example.model.Provider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProviderService {

    @Inject
    EntityManager em;

    public void validateProvider(Provider provider) {
    if (provider.getPartnerId() == null || !provider.getPartnerId().matches("^[a-zA-Z0-9]{3,10}$")) {
        throw new IllegalArgumentException("Invalid partnerId");
    }

    if (provider.getName() == null || !provider.getName().matches("^[A-Za-z0-9 _-]{3,50}$")) {
        throw new IllegalArgumentException("Invalid name");
    }

    if (provider.getContactInfo() == null || !provider.getContactInfo().matches("^[\\w._%+-]+@[\\w.-]+\\.\\w{2,}$")) {
        throw new IllegalArgumentException("Invalid email");
    }

    if (provider.getSla() == null || provider.getSla().getDeliveryTimeMs() < 0 || provider.getSla().getDeliveryTimeMs() > 10000) {
        throw new IllegalArgumentException("Invalid delivery time");
    }

    if (provider.getSla().getUptimePercent() < 0 || provider.getSla().getUptimePercent() > 100) {
        throw new IllegalArgumentException("Invalid uptime percent");
    }

    if (provider.getSupportedChannels() == null || provider.getSupportedChannels().isEmpty()) {
        throw new IllegalArgumentException("At least one supported channel is required");
    }
}

    @Transactional
    public void saveProvider(Provider provider) {
        em.persist(provider);
    }

    @Transactional
public List<Provider> getAllProviders() {
    return em.createQuery(
        "SELECT DISTINCT p FROM Provider p LEFT JOIN FETCH p.supportedChannels", Provider.class)
        .getResultList();
}

}