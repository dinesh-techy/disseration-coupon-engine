package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.api.GeminiApi;
import com.disseration.coupon_engine.dto.GeminiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GeminiApi geminiApi;

    public GeminiService(GeminiApi geminiApi) {
        this.geminiApi = geminiApi;
    }

    public GeminiResponse generateText(String prompt){
        return geminiApi.generateText(prompt);
    }

    public GeminiResponse generateRule(String description) throws JsonProcessingException {
        // Strict prompt to force JSON
        String prompt = """
                You are a rule generator for a coupon engine.
                Task:
                - Convert the given description into a strict JSON object.
                - ONLY include fields explicitly provided in the description.
                - If a required field cannot be inferred, set it to null.
                Output format:
                {
                  "type": "DISCOUNT | CASHBACK | BUY_ONE_GET_ONE | null",
                  "value": number or null,
                  "condition": {
                    "field": string or null,
                    "operator": string or null,
                    "value": string or null
                  },
                  "couponRule": {
                    "name": string or null,
                    "scope": "CART_LEVEL" | "CATEGORY_LEVEL" | null,
                    "targetCategory": string or null
                 }
                  "expiryDate": string (ISO format) or null,
                  "usageLimit": number or null,
                  "stackable": boolean or null
                }
                Rules:
                1. "type":
                   - DISCOUNT if description says discount.
                   - CASHBACK if cashback.
                   - BUY_ONE_GET_ONE if BOGO.
                   - null if unclear.
                2. "value":
                   - For DISCOUNT or CASHBACK → decimal between 0 and 1 (e.g., 0.20 for 20%).
                   - For BUY_ONE_GET_ONE or missing → null.
                3. "condition":
                   - Must be a single JSON object with keys: field, operator, value.
                   - Derive field/operator/value directly from the description.
                   - Use lowercase_snake_case for field names.
                   - If no condition is mentioned, set the entire object to null.
                4. "expiryDate", "usageLimit", "stackable":
                   - Include only if explicitly mentioned; otherwise null.
                5. If a field cannot be inferred → set it to null.
                6.Rules for "couponRule":
                    1. "name":
                        - Derive from coupon context (e.g., "Electronics10Off", "Cart5PercentOff").
                        - If not explicitly given, set to null.
                    2. "scope":
                        - "CART_LEVEL" → if the discount applies to the entire order, total, or cart.
                        - "CATEGORY_LEVEL" → if it applies only to a specific category like electronics, tyres, etc.
                        - null → if unclear.
                    3. "targetCategory":
                        - Extract directly if a category is mentioned (e.g., electronics, tyres).
                        - null if coupon applies to entire cart or no category mentioned.
                    4. Keep "couponRule" consistent with main coupon type. For example:
                        - “10% off on electronics” → scope = CATEGORY_LEVEL, targetCategory = "electronics".
                        - “5% off on cart total” → scope = CART_LEVEL, targetCategory = null.
                    5. Output must include "couponRule" at the root JSON level, alongside other fields.
                7. Output ONLY the JSON. No text or explanation.
                Description: %s
        """ + description;

        GeminiResponse rawResponse = geminiApi.generateText(prompt);
        return rawResponse;

    }

    public GeminiResponse generateCouponName(String couponRawJson) throws JsonProcessingException {
        // Strict prompt to force JSON
        String prompt = """
                You are a coupon name and code generator for a coupon engine.
                Your input is a structured rule string that contains details about a coupon rule.
                Your task is to generate:
                A coupon name – short, readable, and marketing-friendly for end users.
                A coupon code – uppercase, alphanumeric, system-friendly identifier.
                Output Format
                Return a strict JSON object only in the following format:
                {
                  "couponName": "string",
                  "couponCode": "string"
                }
                Rules
                1. couponName
                Must be concise (max 6 words) and human-readable.
                Use Title Case (e.g., “10% Off Electronics”).
                Derive from the rule fields:
                type: determines the offer type (DISCOUNT → “Off”, CASHBACK → “Cashback”, BUY_ONE_GET_ONE → “BOGO Offer”).
                value: convert decimal to percentage (e.g., 0.10 → “10%”).
                conditions: include condition value (e.g., category=electronics → “Electronics”).
                Ignore null fields.
                If type/value/condition are missing, use a generic fallback like “Exclusive Spare Parts Offer”.
                2. couponCode
                Must be UPPERCASE, no spaces, ≤ 15 characters.
                Construct from key rule fields:
                Category or condition value → e.g., ELECTRONICS, OILFILTER
                Offer type/value → e.g., 10OFF, CASH200, BOGO
                Optional keyword for context → e.g., NEWUSER, FESTIVE
                Remove all non-alphanumeric symbols.
                Use underscore _ only if needed for readability.
                If insufficient data, use a fallback like "SPAREPARTSDEAL".
                Examples
                Input	Output
                Rule(type=DISCOUNT, value=0.10, conditions=[Condition(field=category, operator=equals, value=electronics)], expiryDate=null, usageLimit=null, stackable=null)	{ "couponName": "10% Off Electronics", "couponCode": "ELECTRONICS10OFF" }
                Rule(type=CASHBACK, value=0.20, conditions=[Condition(field=order_amount, operator=greater_than, value=1000)], expiryDate=null, usageLimit=50, stackable=true)	{ "couponName": "20% Cashback on ₹1000+", "couponCode": "CASHBACK20" }
                Rule(type=BUY_ONE_GET_ONE, value=null, conditions=[Condition(field=category, operator=equals, value=oil_filter)], expiryDate=null, usageLimit=null, stackable=null)	{ "couponName": "Oil Filter BOGO Offer", "couponCode": "OILFILTERBOGO" }
                Rule(type=DISCOUNT, value=0.15, conditions=[], expiryDate=null, usageLimit=null, stackable=null)	{ "couponName": "15% Off Spare Parts", "couponCode": "SPAREPARTS15OFF" }
                Final Output Requirement
                Output only the JSON object — no text or explanation.
                Input Rule: %s
        """ + couponRawJson;
        GeminiResponse rawResponse = geminiApi.generateText(prompt);
        return rawResponse;

    }


}
