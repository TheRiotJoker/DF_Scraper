package de.dfscraper.service;

import de.dfscraper.entity.ShopItem;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class ShopService {
    public ArrayList<ShopItem> getPrices(String query) {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://fairview.deadfrontier.com/onlinezombiemmo/trade_search.php"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("tradezone=21&searchname="+query+"&search=trades&searchtype=buyinglistitemname"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return getPricesInternal(response.body());
        }catch(Exception e) {
            System.out.println("An exception occurred, see:"+e.getMessage());
            return null;
        }
    }

    private ArrayList<ShopItem> getPricesInternal(String apiResponse) {
        ArrayList<ShopItem> shopItems = new ArrayList<>();
        System.out.println(apiResponse);
        String[] shopEntries = apiResponse.split("&tradelist_\\d+_trade_id=\\d+");
        for(String s : shopEntries) {
            ShopItem item = mapShopEntryToObject(s);
            if(item != null) {
                shopItems.add(item);
            }
        }
        return shopItems;
    }

    private ShopItem mapShopEntryToObject(String shopEntry) {
        String[] shopEntryFields = shopEntry.split("&");
        if (shopEntryFields.length < 11) {
            return null;
        }
        String playerName = shopEntryFields[2].substring(shopEntryFields[2].lastIndexOf('=')+1);
        String itemName = shopEntryFields[6].substring(shopEntryFields[6].lastIndexOf('=')+1);
        String price = shopEntryFields[7].substring(shopEntryFields[7].lastIndexOf('=')+1);
        String tradeZone = shopEntryFields[8].substring(shopEntryFields[8].lastIndexOf('=')+1);
        String category = shopEntryFields[9].substring(shopEntryFields[9].lastIndexOf('=')+1);
        String quantity = shopEntryFields[10].substring(shopEntryFields[10].lastIndexOf('=')+1);
        String pricePerItem = shopEntryFields[11].substring(shopEntryFields[11].lastIndexOf('=')+1);
        return ShopItem.builder()
                .sellerName(playerName)
                .itemName(itemName)
                .totalPrice(Long.parseLong(price))
                .tradeZone(Long.parseLong(tradeZone))
                .category(category)
                .quantity(Long.parseLong(quantity))
                .pricePerItem(Double.parseDouble(pricePerItem))
                .build();
    }
}
