package me.lablyteam.bankslab.account.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lablyteam.bankslab.enums.AccountMethod;

public interface AccountLogs {
	List<String> getAllLog();
	AccountLog getLog(int index);
	void addLog(AccountMethod method, int amount);
	String toDatabaseString();
	Player getPlayer();
	int size();
	
	default AccountLog parse(String str) {
		String[] splitted = str.split(":");
		AccountMethod method = AccountMethod.valueOf(splitted[0].toUpperCase());
		int amount = Integer.valueOf(splitted[1]);
		Player player = Bukkit.getPlayer(UUID.fromString(splitted[2]));
		long createdAt = Long.valueOf(splitted[3]);
		return new AccountLog(method, amount, createdAt, player);
	}
	
	default List<AccountLog> parseAll() {
		// LOG: withdraw:amount:player;other;other;other
		List<AccountLog> logs = new ArrayList<>();
		getAllLog().forEach((str) -> {
			if(str.isEmpty())return;
			logs.add(parse(str));
		});
		return logs;
	}
}
