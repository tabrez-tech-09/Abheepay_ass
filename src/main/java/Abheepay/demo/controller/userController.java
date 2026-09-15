package Abheepay.demo.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/users")
public class UserController {

    public static class User {
        private String id;
        private String name;
        private int balance;

        public User() {
        }

        public User(String id, String name, int balance) {
            this.id = id;
            this.name = name;
            this.balance = balance;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getBalance() {
            return balance;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }

    private final List<User> users = new ArrayList<>();

    @PostMapping
    public User createUser(@RequestBody User user) {

        
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }

        String id = "U" + String.format("%03d", users.size() + 1);

        User newUser = new User(
                id,
                user.getName(),
                0
        );

        users.add(newUser);

        return newUser;
    }

    @PostMapping("credit")
    public String credit(@RequestBody String entity) {
        
        String[] parts = entity.split(",");
        String userId = parts[0].split("=")[1].trim();
        int amount = Integer.parseInt(parts[1].split("=")[1].trim());

        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBalance(user.getBalance() + amount);

        return "Credited " + amount + " to user " + user.getName() + ". New balance: " + user.getBalance();

    }

    @PostMapping("debit")
    public String debitBalance(@RequestBody String entity) {
        String[] parts = entity.split(",");
        String userId = parts[0].split("=")[1].trim();
        int amount = Integer.parseInt(parts[1].split("=")[1].trim());

        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - amount);

        return "Debited " + amount + " from user " + user.getName() + ". New balance: " + user.getBalance();
    }

    @GetMapping("/balance")
    public int getBalance(@RequestParam String userId) {
        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getBalance();
    }

    @GetMapping("transactions")
    public String getTransactions(@RequestParam String userId) {
        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
        return "Transaction history for user " + userId;

    }

    @PostMapping("transactionId/reverse")
    public String reverseTransaction(@RequestBody String entity) {
        String[] parts = entity.split(",");
        String userId = parts[0].split("=")[1].trim();
        String transactionId = parts[1].split("=")[1].trim();

        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        return "Reversed transaction " + transactionId + " for user " + user.getName();
    }



}