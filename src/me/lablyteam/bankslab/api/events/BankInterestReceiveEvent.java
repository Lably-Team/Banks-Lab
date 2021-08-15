package me.lablyteam.bankslab.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.bank.Bank;

public class BankInterestReceiveEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	
	private double tax;
	private double money;
	private long timestamp;
	private Bank bank;
	private Account account;
	private boolean cancelled;
	
	public BankInterestReceiveEvent(Player player, double money, double tax, long timestamp, Bank bank, Account account) {
		super(player);
		this.tax = tax;
		this.money = money;
		this.timestamp = timestamp;
		this.bank = bank;
		this.account = account;
		this.cancelled = false;
	}
	
	public double getTax() {
		return tax;
	}
	
	public double getMoney() {
		return money;
	}
	
	public long getTimeStamp() {
		return timestamp;
	}
	
	public Bank getBank() {
		return bank;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
