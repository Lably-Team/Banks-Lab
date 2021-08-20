package me.lablyteam.bankslab.account;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lablyteam.bankslab.enums.AccountMethod;

public class AccountLog {
	
	private AccountMethod method;
	private int amount;
	private UUID uuid;
	
	public AccountLog(AccountMethod method, int amount, Player player) {
		this.method = method;
		this.amount = amount;
		this.uuid = player.getUniqueId();
	}
	
	public AccountMethod getMethod() {
		return method;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	public UUID getPlayerUUID() {
		return uuid;
	}
	
	public String toString() {
		return method.getName() + ":" + amount + ":" + uuid;
	}
	
}