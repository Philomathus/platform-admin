package com.qiqilm.server.admin.proportion;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Setter
@Component
@Slf4j
public class SmsLoadBalancerConfiguration {

    private static final List<Pair<SmsProvider, Double>> providerWeights = new ArrayList<>();
    private static EnumeratedDistribution<SmsProvider> distribution;

    public void addProvider(SmsProvider smsProvider, Double weight) {
        providerWeights.add(new Pair<>(smsProvider, weight));
    }

    public boolean hasProviderWeights() {
        return providerWeights.size() > 0;
    }

    public void initializeDistribution() {
        distribution = new EnumeratedDistribution<>(providerWeights);
    }

    public SmsProvider chooseProvider() {
        try {
            return distribution.sample();
        } catch (MathArithmeticException mae) {
            log.info("Failed to create distribution, weights might be 0. Total Weight: {}",
                    providerWeights.stream().mapToDouble(Pair::getValue).sum());
        } catch (NullPointerException npe) {
            log.info("Failed to initialize distribution");
        }
        return randomProvider();
    }

    private SmsProvider randomProvider() {
        int random = ThreadLocalRandom.current().nextInt(providerWeights.size());
        SmsProvider provider = providerWeights.get(random).getKey();
        log.info("Failed to get weighted provider, returning random provider: {}", provider.toString());
        return provider;
    }
}
