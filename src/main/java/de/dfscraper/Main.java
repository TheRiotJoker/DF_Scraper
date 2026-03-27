package de.dfscraper;


import de.dfscraper.entity.ShopItem;
import de.dfscraper.service.ShopService;

import java.util.ArrayList;

public class Main {

    static ShopService service = new ShopService();

    public static void main(String[] args) {

        String[] categories = {"weapon_melee", "weapon_pistol", "weapon_rifle", "weapon_shotgun", "weapon_lightmachinegun", "weapon_heavymachinegun", "weapon_grenadelauncher", "ammo_handgun",
        "ammo_rifle", "ammo_shotgun", "ammo_grenade", "ammo_special", "food", "clothing_basic", "clothing_coat", "clothing_headwear", "barricading", "misc", "implants", "backpack"};


        getItemPrices();

    }

    private static void getItemPrices() {
        ArrayList<ShopItem> items = service.getPrices("shinobu mesh");

        System.out.println(items.size());
        items.forEach(System.out::println);
    }

    private static void getItemNames() {
        service.writeItemsForCategoryToFile("medical");
    }



}
