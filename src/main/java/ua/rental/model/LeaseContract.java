package ua.rental.model;

import ua.common.BaseEntity;
import ua.rental.util.RentalUtils;

import java.time.LocalDate;
import java.util.Objects;

public class LeaseContract extends BaseEntity {

    private final String contractNumber;
    private final Property property;
    private final String tenantName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double depositAmount;
    private ContractStatus status;

    public LeaseContract(
            String contractNumber,
            Property property,
            String tenantName,
            LocalDate startDate,
            LocalDate endDate,
            double depositAmount,
            ContractStatus status
    ) {
        super();
        this.contractNumber = RentalUtils.requireValidCode(contractNumber, "Contract number");
        this.property = RentalUtils.requireNotNull(property, "Property");
        this.tenantName = RentalUtils.requireValidName(tenantName, "Tenant name");
        RentalUtils.validateContractDates(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.depositAmount = RentalUtils.requireAtLeast(
                depositAmount, property.getMonthlyRent(), "Deposit amount"
        );
        setStatus(status);
    }

    public final void setStatus(ContractStatus status) {
        this.status = RentalUtils.requireNotNull(status, "Contract status");
    }

    public String getContractNumber() { return contractNumber; }
    public Property getProperty() { return property; }
    public String getTenantName() { return tenantName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getDepositAmount() { return depositAmount; }
    public ContractStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaseContract that = (LeaseContract) o;
        return Objects.equals(contractNumber, that.contractNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractNumber);
    }

    @Override
    public String toString() {
        return "LeaseContract{" +
                "contractNumber='" + contractNumber + '\'' +
                ", propertyCadastral='" + property.getCadastralNumber() + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", depositAmount=" + RentalUtils.formatMoney(depositAmount) +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}