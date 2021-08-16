package me.lablyteam.bankslab.lib;

import me.lablyteam.bankslab.BanksLabMain;

public class Files {
	
	private YamlFile config;
	private YamlFile banks;
	private YamlFile lang;
	
	public Files(BanksLabMain main) {
		this.config = main.getConfig();
		this.banks = new YamlFile(main, "banks");
		this.lang = new YamlFile(main, "lang");
	}
	
	public YamlFile getConfig() {
		return config;
	}
	
	public YamlFile getBanks() {
		return banks;
	}
	
	public YamlFile getLang() {
		return lang;
	}
	
	public void reload() {
		getConfig().reload();
		getBanks().reload();
		getLang().reload();
	}
	
}
