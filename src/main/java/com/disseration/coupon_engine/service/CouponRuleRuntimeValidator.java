package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.Cart;
import com.disseration.coupon_engine.dto.CartItem;
import com.disseration.coupon_engine.dto.Condition;
import com.disseration.coupon_engine.dto.Rule;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponRuleRuntimeValidator {

    private final CouponRuleService couponRuleService;

    public CouponRuleRuntimeValidator(CouponRuleService couponRuleService) {
        this.couponRuleService = couponRuleService;
    }

    public boolean isRuleValid(String couponCode) {
        Rule ruleJson = couponRuleService.getCouponRuleByCode(couponCode).getRuleJson();
        // Expiry Validation
        if (ruleJson.getExpiryDate()!=null) {
            LocalDate expiryDate = ruleJson.getExpiryDate();
            boolean isValid = !expiryDate.isBefore(LocalDate.now());
            if (!isValid) {
                System.out.println("❌ Coupon Expired at "+expiryDate);
                return false;
            }
        }

        // Usage Limit Validation
        if (ruleJson.getUsageLimit()<=0) {
            System.out.println("❌ Coupon usage limit reached");
            return false;
        }

        // ✅ If passed both checks
        return true;
    }

    public boolean isCartValid(Cart cart) {
        Rule ruleJson = couponRuleService.getCouponRuleByCode(cart.getCouponCode()).getRuleJson();
        // 1️⃣ Basic sanity check
        if (!isCartStructureValid(cart)) return false;

        // 2️⃣ Evaluate each condition dynamically
        for (Condition condition : ruleJson.getConditions()) {
            Object actualValue = resolveActualValue(cart, condition.getField());
            if (actualValue == null) {
                System.out.printf("⚠️ Unsupported field in condition: %s%n", condition.getField());
                continue;
            }

            boolean passed = evaluateCondition(actualValue, condition.getValue(), condition.getOperator());

            if (!passed) {
                System.out.printf("❌ Condition failed: %s %s %s%n",
                        condition.getField(), condition.getOperator(), condition.getValue());
                return false;
            }
        }
        System.out.println("✅ All rule conditions satisfied for cart");
        return true;
    }

    // Basic cart sanity check
    private boolean isCartStructureValid(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            System.out.println("❌ Invalid or empty cart");
            return false;
        }
        for (CartItem item : cart.getItems()) {
            if (item.getPrice() <= 0 || item.getQuantity() <= 0) {
                System.out.printf("❌ Invalid item: %s (price/quantity issue)%n", item.getProductId());
                return false;
            }
        }
        double total = cart.getTotal();
        if (total <= 0) {
            System.out.println("❌ Cart total must be greater than 0");
            return false;
        }
        return true;
    }

    // Dynamically resolves actual value from cart based on condition field
    private Object resolveActualValue(Cart cart, String field) {
        return switch (field.toLowerCase()) {

            // 🔹 1️⃣ Numeric Field
            case "cart_total" -> cart.getTotal();

            // 🔹 2️⃣ Single String Field
            case "membership_type" -> cart.getMemberShipType();

            // 🔹 3️⃣ List Fields — return distinct string lists
            case "category" -> cart.getItems()
                    .stream()
                    .map(item -> item.getCategory().toLowerCase())
                    .distinct()
                    .collect(Collectors.toList());

            case "brand" -> cart.getItems()
                    .stream()
                    .map(item -> item.getBrand().toLowerCase())
                    .distinct()
                    .collect(Collectors.toList());

            case "product_id" -> cart.getItems()
                    .stream()
                    .map(item -> item.getProductId().toLowerCase())
                    .distinct()
                    .collect(Collectors.toList());
            // 🔹 Extendable: user_region, payment_method, etc.
            default -> null;
        };
    }

    // Evaluates condition (handles both numeric and text-based fields)
    private boolean evaluateCondition(Object actualValue, String expectedValue, String operator) {
        if (actualValue instanceof Number) {
            double actual = ((Number) actualValue).doubleValue();
            double expected = parseDoubleSafe(expectedValue);
            return switch (operator.toLowerCase()) {
                case "above" -> actual > expected;
                case "below" -> actual < expected;
                case "equals" -> actual == expected;
                default -> false;
            };
        } else if (actualValue instanceof String) {
            String actual = (String) actualValue;
            return switch (operator.toLowerCase()) {
                case "equals" -> actual.equalsIgnoreCase(expectedValue);
                case "not_equals" -> !actual.equalsIgnoreCase(expectedValue);
                case "in" -> List.of(expectedValue.split(",")).stream()
                        .map(String::trim)
                        .anyMatch(v -> v.equalsIgnoreCase(actual));
                case "not_in" -> List.of(expectedValue.split(",")).stream()
                        .noneMatch(v -> v.equalsIgnoreCase(actual));
                default -> false;
            };
        }
        return false;
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
