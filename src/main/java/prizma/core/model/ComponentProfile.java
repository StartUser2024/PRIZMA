package prizma.core.model;

import java.time.LocalDate;

public interface ComponentProfile {
    String getCertificateId();
    LocalDate getRegistrationDate();
    LocalDate getExpirationDate();
    String getName();
    String getType();
    String getRequirements();
    String getVendor();
    String getOperationalFeatures();
}