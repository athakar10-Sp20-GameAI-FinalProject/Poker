package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public enum CardEnum {
    ACE("A"), TWO("2"), THREE("3"), FOUR("4"),
    FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"),
    NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"), KING("K");

    private String name;
    private int value;
    private HashMap<String, Integer> values = new HashMap<String, Integer>() {{
    	put("A", 14);
    	put("2", 2);
    	put("3", 3);
    	put("4", 4);
    	put("5", 5);
    	put("6", 6);
    	put("7", 7);
    	put("8", 8);
    	put("9", 9);
    	put("10", 10);
    	put("J", 11);
    	put("Q", 12);
    	put("K", 13);
    }};

    CardEnum (String name) {
        this.name = name;
        this.value = values.get(name);
    }

    String getType() {
        return this.name;
    }
    
    int getValue() {
    	return this.value;
    }
    
    public static List<Integer> sortCards(List<Card> cards) {
    	ArrayList<Integer> nums = new ArrayList<>();
    	for(int i = 0; i < cards.size(); i++) {
    		CardEnum card = cards.get(i).getCard();
    		switch (card) {
    			case ACE: nums.add(1); nums.add(14);
    			break;
    			case TWO: nums.add(2);
    			break;
    			case THREE: nums.add(3);
    			break;
    			case FOUR: nums.add(4);
    			break;
    			case FIVE: nums.add(5);
    			break;
    			case SIX: nums.add(6);
    			break;
    			case SEVEN: nums.add(7);
    			break;
    			case EIGHT: nums.add(8);
    			break;
    			case NINE: nums.add(9);
    			break;
    			case TEN: nums.add(10);
    			break;
    			case JACK: nums.add(11);
    			break;
    			case QUEEN: nums.add(12);
    			break;
    			case KING: nums.add(13);
    			break;
    			default: System.out.println("Error in sortCards");
    		}
    	}
    	Collections.sort(nums);
    	return nums;
    }
    
    public static List<Integer> allCardEnums() {
    	return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
    }
}
