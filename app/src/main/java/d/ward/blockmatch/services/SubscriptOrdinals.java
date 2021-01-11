package d.ward.blockmatch.services;

import java.util.Hashtable;

// Class for working with subscript ordinals
public class SubscriptOrdinals {
    private Hashtable<Integer, String> subscriptOrdinals = new Hashtable<Integer, String>();

    public SubscriptOrdinals() {
        this.buildOrdinals();
    }

    // Builds and cache's ordinals
    private void  buildOrdinals() {
        this.subscriptOrdinals.put(0, "th");
        this.subscriptOrdinals.put(1, "st");
        this.subscriptOrdinals.put(2, "nd");
        this.subscriptOrdinals.put(3, "rd");
        this.subscriptOrdinals.put(4, "th");
        this.subscriptOrdinals.put(5, "th");
        this.subscriptOrdinals.put(6, "th");
        this.subscriptOrdinals.put(7, "th");
        this.subscriptOrdinals.put(8, "th");
        this.subscriptOrdinals.put(9, "th");
        this.subscriptOrdinals.put(10, "th");
        this.subscriptOrdinals.put(11, "th");
        this.subscriptOrdinals.put(12, "th");
        this.subscriptOrdinals.put(13, "th");
        this.subscriptOrdinals.put(14, "th");
        this.subscriptOrdinals.put(15, "th");
        this.subscriptOrdinals.put(16, "th");
        this.subscriptOrdinals.put(17, "th");
        this.subscriptOrdinals.put(18, "th");
        this.subscriptOrdinals.put(19, "th");
    }

    // Gets subscript ordinal for number supplied
    public String getSubscriptOrdinal(int number) {
        String subscriptOrdinal;

        String numberStr = String.valueOf(number);
        int numberInOnesPlace = Integer.parseInt(numberStr.substring(numberStr.length() - 1));
        Integer integer = Integer.valueOf(numberInOnesPlace);

        if (number < 20) {
            subscriptOrdinal = this.subscriptOrdinals.get(number);
        } else {
            subscriptOrdinal = this.subscriptOrdinals.get(integer);
        }

        return subscriptOrdinal;
    }
}
