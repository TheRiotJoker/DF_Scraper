package de.dfscraper.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class SpecialItemStats {
    private int agility;
    private int endurance;
    private String color;
}
