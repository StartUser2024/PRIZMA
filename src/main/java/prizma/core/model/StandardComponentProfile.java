package prizma.core.model;

import java.time.LocalDate;

public class StandardComponentProfile implements ComponentProfile {
    private final String certificateId;
    private final LocalDate registrationDate;
    private final LocalDate expirationDate;
    private final String name;
    private final String type;
    private final String requirements;
    private final String vendor;
    private final String operationalFeatures;

    public StandardComponentProfile(String certificateId, LocalDate registrationDate,
                                    LocalDate expirationDate, String name, String type,
                                    String requirements, String vendor, String operationalFeatures) {
        this.certificateId = certificateId;
        this.registrationDate = registrationDate;
        this.expirationDate = expirationDate;
        this.name = name;
        this.type = type;
        this.requirements = requirements;
        this.vendor = vendor;
        this.operationalFeatures = operationalFeatures;
    }

    @Override
    public String getCertificateId() { return certificateId; }
    @Override
    public LocalDate getRegistrationDate() { return registrationDate; }
    @Override
    public LocalDate getExpirationDate() { return expirationDate; }
    @Override
    public String getName() { return name; }
    @Override
    public String getType() { return type; }
    @Override
    public String getRequirements() { return requirements; }
    @Override
    public String getVendor() { return vendor; }
    @Override
    public String getOperationalFeatures() { return operationalFeatures; }

    @Override
    public String toString() {
        return name + " [" + certificateId + "]";
    }
}