package de.dfscraper.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ShopItem {
    private String sellerName;
    private String itemName;
    private long totalPrice;
    private long tradeZone;
    private String category;
    private long quantity;
    private double pricePerItem;
}
