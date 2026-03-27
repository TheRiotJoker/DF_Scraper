package de.dfscraper.service;

import de.dfscraper.entity.ShopItem;
import de.dfscraper.entity.SpecialItemStats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class ShopService {
    public ArrayList<ShopItem> getPrices(String query) {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://fairview.deadfrontier.com/onlinezombiemmo/trade_search.php"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("tradezone=21&searchname="+query+"&search=trades&searchtype=buyinglistitemname"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            return getPricesInternal(response.body());
        }catch(Exception e) {
            System.out.println("An exception occurred, see:"+e.getMessage());
            return null;
        }
    }

    public void writeItemsForCategoryToFile(String category) {
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://fairview.deadfrontier.com/onlinezombiemmo/trade_search.php"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("tradezone=21&searchname=&memID=&profession=&category="+category+"&search=trades&searchtype=buyinglistcategory"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var items = getPricesInternal(response.body());

            var map = items.stream().collect(Collectors.toMap(ShopItem::getItemName, item -> item, (existing, replacement) -> existing));

            File file = new File(category + ".txt");

            FileOutputStream stream = new FileOutputStream(file);

            PrintWriter writer = new PrintWriter(stream);

            writer.write(map.keySet().toString());

            writer.close();
            stream.close();

        }catch(Exception e) {
            System.out.println("An exception occurred, see:"+e.getMessage());
        }
    }

    private ArrayList<ShopItem> getPricesInternal(String apiResponse) {
        ArrayList<ShopItem> shopItems = new ArrayList<>();
        String[] shopEntries = apiResponse.split("&tradelist_\\d+_trade_id=\\d+");
        for(String s : shopEntries) {

            if(s.length() < 10) continue;

            ShopItem item;

            try {
                item = mapShopEntryToObject(s);
            } catch (Exception e) {
                System.out.println("Exception caught: "+e.getMessage());
                continue;
            }

            if(item != null) {
                shopItems.add(item);
            }
        }
        return shopItems;
    }

    private ShopItem mapShopEntryToObject(String shopEntry) {

        // All fields are separated by & symbols, so

        String[] shopEntryFields = shopEntry.split("&");

        var formatted = Arrays.stream(shopEntryFields).map(s -> s.replaceAll("tradelist_\\d+_", "")).toList();

        Map<String, String> values = new HashMap<>();

        for (String entry : formatted) {

            String[] keyValue = entry.split("=");

            if(keyValue.length != 2) continue;

            values.put(entry.split("=")[0], entry.split("=")[1]);
        }

        String itemInfo = values.get("item");

        String itemId = !itemInfo.contains("_") ? itemInfo : itemInfo.substring(0, itemInfo.indexOf('_'));

        System.out.println(itemInfo);

        return ShopItem.builder()
                .sellerName(values.get("member_name"))
                .itemId(itemId)
                .specialItemStats(findSpecialStats(itemInfo, values.get("category")))
                .itemName(values.get("itemname"))
                .totalPrice(Long.parseLong(values.get("price")))
                .tradeZone(Long.parseLong(values.get("trade_zone")))
                .category(values.get("category"))
                .quantity(Long.parseLong(values.get("quantity")))
                .pricePerItem(Double.parseDouble(values.get("priceper")))
                .build();
    }

    private SpecialItemStats findSpecialStats(String itemInfo, String category) {
        String[] extras = itemInfo.split("_");

        boolean foundStats = false;

        var builder = SpecialItemStats.builder();

        for(String extra : extras) {
            if(extra.contains("colour")) {
                foundStats = true;
                builder.color(extra.replace("colour", ""));
            }
            if(extra.contains("stats") && category.contentEquals("armour")) {
                foundStats = true;
                String toParse = extra.replace("stats", "");

                builder.agility(Integer.parseInt(toParse.substring(0, 2)));
                builder.endurance(Integer.parseInt(toParse.substring(2,4)));
            }
            if(extra.contains("stats") && category.contains("weapon")) {
                // Todo: create fucking entity classes to contain all of these things,
                // Todo: no way in fucking hell are we keeping every information inside of the singular entity class
            }

        }

        return foundStats ? builder.build() : null;

    }

}
