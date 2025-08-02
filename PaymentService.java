package com.codility.infrastructure.health;

import com.codility.payment.PaymentService;
import com.codility.payment.PaymentServiceException;
import com.codility.payment.PaymentServiceResponse;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component(“payment-service”)
public class PaymentServiceHealthIndicator implements HealthIndicator {

```
private final PaymentService paymentService;

public PaymentServiceHealthIndicator(PaymentService paymentService) {
this.paymentService = paymentService;
}

@Override
public Health health() {
try {
PaymentServiceResponse response = paymentService.check();

if (response == null) {
return Health.down()
.withDetail("status", "DOWN")
.withDetail("details", createDetails("Service returned null response", getCurrentTimestamp()))
.build();
}

String status = response.getStatus();

if ("UP".equals(status)) {
return Health.up()
.withDetail("status", "UP")
.build();
} else if ("DOWN".equals(status)) {
return Health.down()
.withDetail("status", "DOWN")
.withDetail("details", createDetails(
response.getReason() != null ? response.getReason() : "maintenance",
response.getEta() != null ? response.getEta().toString() : getCurrentTimestamp()
))
.build();
} else {
// Handle any other status as DOWN
return Health.down()
.withDetail("status", "DOWN")
.withDetail("details", createDetails("Unknown status: " + status, getCurrentTimestamp()))
.build();
}

} catch (PaymentServiceException e) {
return Health.down()
.withDetail("status", "DOWN")
.withDetail("details", createDetails("Service exception: " + e.getMessage(), getCurrentTimestamp()))
.build();
} catch (Exception e) {
return Health.down()
.withDetail("status", "DOWN")
.withDetail("details", createDetails("Unexpected error: " + e.getMessage(), getCurrentTimestamp()))
.build();
}
}

private Object createDetails(String reason, String eta) {
return new HealthDetails(reason, eta);
}

private String getCurrentTimestamp() {
return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
}

private static class HealthDetails {
private final String reason;
private final String eta;

public HealthDetails(String reason, String eta) {
this.reason = reason;
this.eta = eta;
}

public String getReason() {
return reason;
}

public String getEta() {
return eta;
}
}
```

}
