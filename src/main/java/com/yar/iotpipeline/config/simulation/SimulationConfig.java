package com.yar.iotpipeline.config.simulation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Simulation Configuration
 */
@Configuration
@EnableScheduling
@Profile("simulation")
public class SimulationConfig {

    /**
     * Create the simulation scheduler bean
     *
     * @return {@link SimulationScheduler}
     */
    @Bean
    public SimulationScheduler simulationScheduler() {
        return new SimulationScheduler();
    }
}
