package de.dfscraper;


import de.dfscraper.entity.ShopItem;
import de.dfscraper.service.ShopService;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ShopService service = new ShopService();
        ArrayList<ShopItem> items = service.getPrices("tatakau");
        System.out.println(items.size());
        items.forEach(System.out::println);
    }


}
