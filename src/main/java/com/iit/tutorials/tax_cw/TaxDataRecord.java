package com.iit.tutorials.tax_cw;

import java.util.List;

public class TaxDataRecord {
    private long billNo;
    private String itemCode;
    private double internalPrice;
    private double salesPrice;
    private double discount;
    private int quantity;
    private double profit;
    private String valid;
    private String originalTransactionLine;
    private int originalChecksum;

    public TaxDataRecord(long billNo, String itemCode, double internalPrice, double salesPrice, double discount, int quantity, String valid) {
        this.billNo = billNo;
        this.itemCode = itemCode;
        this.internalPrice = internalPrice;
        this.salesPrice = salesPrice;
        this.discount = discount;
        this.quantity = quantity;
        this.valid = valid;
        recalculateProfit();
    }

    public long getBillNo() {
        return billNo;
    }

    public String getItemCode() {
        return itemCode;
    }

    public double getInternalPrice() {
        return internalPrice;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getProfit() {
        return profit;
    }

    public String getValid() {
        return valid;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
        recalculateProfit();
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        recalculateProfit();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateProfit();
    }

    public void setInternalPrice(double internalPrice) {
        this.internalPrice = internalPrice;
        recalculateProfit();
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public void recalculateProfit() {
        this.profit = ((salesPrice * quantity - discount) - (internalPrice * quantity));
    }

    public void setOriginalTransactionLine(String originalTransactionLine) {
        this.originalTransactionLine = originalTransactionLine;
    }

    public String getOriginalTransactionLine() {
        return originalTransactionLine;
    }

    public int getOriginalChecksum() {
        return originalChecksum;
    }

    public void setOriginalChecksum(int originalChecksum) {
        this.originalChecksum = originalChecksum;
    }

    public void validateChecksum(List<TaxDataRecord> allRecords) {
        List<TaxDataRecord> relatedRecords = allRecords.stream()
                .filter(r -> r.getBillNo() == this.getBillNo())
                .toList();

        String rebuiltTransaction = buildTransactionLine(relatedRecords);
        int newChecksum = TransactionFileReader.calculateChecksum(rebuiltTransaction);
        relatedRecords.forEach(r -> {
            if (newChecksum != this.getOriginalChecksum()
                    || !r.getItemCode().matches("^[a-zA-Z0-9]+$")
                    || r.getInternalPrice() < 0
                    || r.getSalesPrice() < 0) {
                r.setValid("False");
            } else {
                r.setValid("True");
            }
        });
    }

    public String buildTransactionLine(List<TaxDataRecord> recordsWithSameBill) {
        StringBuilder transactionLine = new StringBuilder();
        transactionLine.append("bill_number:").append(this.getBillNo());

        for (TaxDataRecord record : recordsWithSameBill) {
            transactionLine.append(" | item_code:").append(record.getItemCode())
                    .append(",internal_price:").append(record.getInternalPrice())
                    .append(",discount:").append(record.getDiscount())
                    .append(",sale_price:").append(record.getSalesPrice())
                    .append(",quantity:").append(record.getQuantity());
        }

        double grandTotal = recordsWithSameBill.stream()
                .mapToDouble(r -> (r.getSalesPrice() * r.getQuantity()))
                .sum();
        transactionLine.append(" | grand_total:").append(grandTotal);

        return transactionLine.toString();
    }
}
