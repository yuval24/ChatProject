package com.example.darkproject;
import java.util.UUID;
public class UniqueIdGenerator {
    public static String generateUniqueId() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();

        // Convert UUID to a String

        return uuid.toString().replaceAll("-","");
    }
}
