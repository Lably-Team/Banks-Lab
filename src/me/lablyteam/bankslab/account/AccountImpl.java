package me.lablyteam.bankslab.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.logging.AccountLogs;
import me.lablyteam.bankslab.account.logging.AccountLogsImpl;
import me.lablyteam.bankslab.api.events.BankInterestReceiveEvent;
import me.lablyteam.bankslab.bank.Bank;
import me.lablyteam.bankslab.enums.AccountMethod;
import me.lablyteam.bankslab.lib.Utility;
import me.lablyteam.bankslab.lib.YamlFile;
import me.lablyteam.bankslab.sqlite.Database;
import me.lablyteam.bankslab.sqlite.SQLite;
import net.milkbowl.vault.economy.Economy;

class AccountImpl implements Account {
	
	private BanksLabMain main;
	
	private UUID owner;
	private Economy economy;
	private SQLite database;
	private AccountLogs logs;
	private double balance;
	private Bank bank;
	private long lastInterest;
	private long loggedAt;
	
	private Database.SQLData memoryData;
	
	public AccountImpl(BanksLabMain main, Player player) {
		this.main = main;
		this.owner = player.getUniqueId();
		this.economy = main.getEconomy();
		this.database = main.getDatabase();
		this.logs = new AccountLogsImpl(database, owner);
		this.memoryData = this.database.getData(player);
		this.balance = memoryData.getMoney();
		this.bank = main.getCache().getBanks().get(memoryData.getBankName());
		this.lastInterest = memoryData.getLastInterest();
		this.loggedAt = System.currentTimeMillis();
	}
	
	@Override
	public Player getOwner() {
		return Bukkit.getPlayer(owner);
	}
	
	@Override
	public boolean deposit(double amount) {
		double money = economy.getBalance(getOwner());
		if(money < amount || amount < 1) {
			return false;
		}
		
		economy.withdrawPlayer(getOwner(), amount);
		this.balance += money;
		if(amount > 0) {
			getLogs().addLog(AccountMethod.DEPOSIT, (int)amount);
		}
		return true;
	}

	@Override
	public boolean withdraw(double amount) {
		if(balance < amount || amount < 1) {
			return false;
		}
		economy.depositPlayer(getOwner(), amount);
		this.balance -= amount;
		if(amount > 0) {
			getLogs().addLog(AccountMethod.WITHDRAW, (int)amount);
		}
		
		return true;
	}

	@Override
	public boolean setMoney(double amount) {
		this.balance = amount;
		return true;
	}

	@Override
	public AccountLogs getLogs() {
		return logs;
	}

	@Override
	public double getMoney() {
		return balance;
	}
	
	public String toString() {
		return getOwner().getName() + "'s Account";
	}

	@Override
	public void save() {
		String logs = getLogs().toDatabaseString();
		Database.SetFactory factory = database.createSetFactory(getOwner());
		factory.setMoney(balance);
		factory.setLog(logs);
		factory.setLastInterest(lastInterest);
		factory.executeChanges(database.getSQLConnection());
	}

	@Override
	public Bank getBank() {
		Map<String, Bank> banks = main.getCache().getBanks();
		if(!banks.containsKey(memoryData.getBankName()))return banks.get("default");
		return bank;
	}

	@Override
	public long getLastInterest() {
		return lastInterest;
	}

	@Override
	public void setLastInterest(long interest) {
		this.lastInterest = interest;
	}

	@Override
	public long getNextInterest() {
		return lastInterest + bank.getInterestTimestamp();
	}

	@Override
	public void addMoney(double amount) {
		if(amount < 1) return;
		this.balance += amount;
	}

	@Override
	public void removeMoney(double amount) {
		if(amount < 1) return;
		double result = this.balance - amount;
		if(result < 1) {
			this.balance = 0;
		} else {
			this.balance = result;
		}
	}

	@Override
	public boolean checkInterest() {
		if(System.currentTimeMillis() > getNextInterest()) {
			Bank bank = getBank();
			double tax = bank.getInterestTax();
			double interest = Utility.decreaseByPercentage(getMoney(), tax);
			long timestamp = bank.getInterestTimestamp();
			
			BankInterestReceiveEvent call = new BankInterestReceiveEvent(getOwner(), interest, tax, timestamp, bank, this);
			Bukkit.getPluginManager().callEvent(call);
			setLastInterest(System.currentTimeMillis());
			if(call.isCancelled()) {
				return false;
			}
			
			addMoney(interest);
			YamlFile lang = main.getFiles().getLang();
			getOwner().sendMessage(readMessage(lang.getStringList("interest-message"), interest));
			return true;
		}
		return false;
	}

	@Override
	public long loggedAt() {
		return loggedAt;
	}
	
	private String[] readMessage(List<String> messages, double amount) {
		List<String> newList = new ArrayList<>();
		for(String message : messages) {
			newList.add(ChatColor.translateAlternateColorCodes('&', message
					.replace("%amount%", ""+(int)amount)
					.replace("%formatted_amount%", Utility.formatCurrency(amount))));
		}
		
		return newList.stream().toArray(String[]::new);
	}
	
}
