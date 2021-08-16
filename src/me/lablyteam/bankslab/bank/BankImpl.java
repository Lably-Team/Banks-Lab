package me.lablyteam.bankslab.bank;

import org.bukkit.configuration.ConfigurationSection;

public class BankImpl implements Bank {
	
	private String id;
	private String name;
	private double interestTax;
	private int interestTimestamp;
	
	public BankImpl(String id, ConfigurationSection bankSection) {
		this.name = bankSection.getString("name");
		this.id = id;
		this.interestTax = bankSection.getDouble("configuration.interest-tax", 1);
		this.interestTimestamp = bankSection.getInt("configuration.interest-timestamp", 5);
	}
	
	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public double getInterestTax() {
		return interestTax;
	}

	@Override
	public int getInterestTimestamp() {
		return ((1000 * 60) * /*Hora: 60*/ 1) * interestTimestamp;
	}

}
