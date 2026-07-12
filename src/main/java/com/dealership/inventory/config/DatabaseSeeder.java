package com.dealership.inventory.config;

import com.dealership.inventory.model.Role;
import com.dealership.inventory.model.User;
import com.dealership.inventory.model.Vehicle;
import com.dealership.inventory.repository.UserRepository;
import com.dealership.inventory.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository,
                          VehicleRepository vehicleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedSampleVehicles();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            Set<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_ADMIN);
            roles.add(Role.ROLE_USER);

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            logger.info("Successfully seeded default admin user profile (username: admin / password: admin123)");
        }
    }

    private void seedSampleVehicles() {
        if (vehicleRepository.count() == 0) {
            List<Vehicle> sampleVehicles = List.of(
                    Vehicle.builder()
                            .make("Tesla")
                            .model("Model S")
                            .year(2023)
                            .price(89990.0)
                            .stock(5)
                            .description("Model S Plaid, tri-motor all-wheel drive.")
                            .category("Electric")
                            .build(),
                    Vehicle.builder()
                            .make("Ford")
                            .model("Mustang Mach-E")
                            .year(2023)
                            .price(45995.0)
                            .stock(8)
                            .description("All-electric SUV with Mustang soul.")
                            .category("SUV")
                            .build(),
                    Vehicle.builder()
                            .make("Porsche")
                            .model("911 GT3")
                            .year(2022)
                            .price(169700.0)
                            .stock(2)
                            .description("High-performance homologation model.")
                            .category("Sports")
                            .build()
            );

            vehicleRepository.saveAll(sampleVehicles);
            logger.info("Successfully seeded sample vehicles catalog into inventory");
        }
    }
}
