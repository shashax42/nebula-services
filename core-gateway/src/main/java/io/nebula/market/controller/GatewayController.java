package io.nebula.market.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final DiscoveryClient discoveryClient;

    @GetMapping("/")
    public ResponseEntity<?> home() throws IOException {

        Map<String, Object> info = Map.ofEntries(
                Map.entry("timestamp", LocalDateTime.now()),
                Map.entry("description", "Nebula Market API Gateway 🌼"),
                Map.entry("ip", InetAddress.getLocalHost().getHostAddress())
        );

        return ResponseEntity.ok(info);
    }

    @GetMapping("/services")
    public ResponseEntity<?> services() {
        Map<String, List<String>> services = new LinkedHashMap<>();

        discoveryClient.getServices().forEach(service -> {
            List<String> instances = discoveryClient.getInstances(service).stream()
                    .map(instance -> instance.getUri().toString())
                    .toList();
            services.put(service, instances);
        });

        return ResponseEntity.ok(services);
    }

    @GetMapping("/login")
    public RedirectView login() {
        return new RedirectView("/oauth2/authorization/okta");
    }
}
