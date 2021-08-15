package me.lablyteam.bankslab.lib;

import java.text.NumberFormat;

public class Utility {
	public static String formatCurrency(double currency) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		String balance = formatter.format(currency).replace("€", "");
		if (balance.endsWith(".00")) {
	    	int centsIndex = balance.lastIndexOf(".00");
	        if (centsIndex != -1) {
	        	balance = balance.substring(1, centsIndex);
	    	}
	    }
		return balance;
	}
	
	public static double getPercentage(double number, double total) {
        return number * 100 / total;
    }
	
	public static double decreaseByPercentage(double number, double percentage) {
		return number * percentage / 100;
	}
}
