package me.lablyteam.bankslab.account.logging;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lablyteam.bankslab.enums.AccountMethod;

public class AccountLog {
	
	private AccountMethod method;
	private int amount;
	private UUID uuid;
	private long createdAt;
	
	public AccountLog(AccountMethod method, int amount, long createdAt, Player player) {
		this.method = method;
		this.amount = amount;
		this.uuid = player.getUniqueId();
		this.createdAt = createdAt;
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
	
	public long createdAt() {
		return createdAt;
	}
	
	public String toString() {
		return method.getName() + ":" + amount + ":" + uuid;
	}
	
}