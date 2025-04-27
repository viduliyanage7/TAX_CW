package com.iit.tutorials.tax_cw;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TransactionFileReader {
    public static int calculateChecksum(String transactionLine) {
        int capitalLetters = 0;
        int simpleLetters = 0;
        int numbersAndDecimals = 0;

        for (char ch : transactionLine.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                capitalLetters++;
            } else if (Character.isLowerCase(ch)) {
                simpleLetters++;
            } else if (Character.isDigit(ch) || ch == '.') {
                numbersAndDecimals++;
            }
        }
        return capitalLetters + simpleLetters + numbersAndDecimals;
    }


    public static List<TaxDataRecord> readTransactionFile(File file) throws Exception {
        List<TaxDataRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JsonArray dataArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (int i = 0; i < dataArray.size(); i++) {
                String valid;
                JsonObject obj = dataArray.get(i).getAsJsonObject();
                String transaction = obj.get("Transaction").getAsString();
                int json_checksum = obj.get("Checksum").getAsInt();
                int checksum = calculateChecksum(transaction);
                if(json_checksum == checksum) {
                    valid = "True";
                }else{
                    valid = "False";
                }
                String[] parts = transaction.split("\\|");
                long billNo = Long.parseLong(parts[0].split(":")[1].trim());

                for (int j = 1; j < parts.length - 1; j++) {
                    String itemPart = parts[j].trim();
                    String[] itemFields = itemPart.replace("item_code:", "")
                            .split(",");

                    String itemCode = itemFields[0];
                    if (itemCode.matches(".*[^a-zA-Z0-9].*")) {
                        valid = "False";
                    }
                    double internalPrice = Double.parseDouble(itemFields[1].split(":")[1]);
                    if (internalPrice < 0) {
                        valid = "False";
                    }

                    double salePrice = Double.parseDouble(itemFields[3].split(":")[1]);
                    if (salePrice < 0) {
                        valid = "False";
                    }

                    double discount = Double.parseDouble(itemFields[2].split(":")[1]);
                    int quantity = Integer.parseInt(itemFields[4].split(":")[1]);

                    TaxDataRecord record = new TaxDataRecord(billNo, itemCode, internalPrice, salePrice, discount, quantity, valid);
                    record.setOriginalTransactionLine(transaction);
                    record.setOriginalChecksum(json_checksum);
                    records.add(record);

                }
            }
        }
        return records;
    }
}
