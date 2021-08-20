package me.lablyteam.bankslab.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lablyteam.bankslab.enums.AccountMethod;
import me.lablyteam.bankslab.sqlite.Database;

public class AccountLogsImpl implements AccountLogs {
	
	private UUID uuid;
	private List<String> log;
	
	public AccountLogsImpl(Database database, UUID uuid) {
		this.uuid = uuid;
		this.log = new ArrayList<>();
		Database.SQLData data = database.getData(getPlayer());
		if(data.getLog() != null) {
			for(String str : data.getLog().split(";")) {
				log.add(str);
			}
		}
	}
	
	@Override
	public List<String> getAllLog() {
		return log;
	}

	@Override
	public AccountLog getLog(int index) {
		return this.parse(getAllLog().get(index).split(";")[index]);
	}

	@Override
	public void addLog(AccountMethod method, int amount) {
		log.add(method.name().toLowerCase()+":"+amount+":"+getPlayer().getUniqueId());
	}

	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	@Override
	public String toDatabaseString() {
		StringBuilder builder = new StringBuilder();
		for(String str : log) {
			if(!builder.toString().isEmpty()) {
				builder.append(";");
			}
			builder.append(str);
		}
		return builder.toString();
	}

	@Override
	public int size() {
		return log.size();
	}
}
