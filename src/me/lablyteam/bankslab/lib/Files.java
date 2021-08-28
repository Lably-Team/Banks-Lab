package me.lablyteam.bankslab.lib;

import me.lablyteam.bankslab.BanksLabMain;

public class Files {
	
	private YamlFile config;
	private YamlFile banks;
	private YamlFile lang;
	private YamlFile items;
	
	public Files(BanksLabMain main) {
		this.config = main.getConfig();
		this.banks = new YamlFile(main, "banks");
		this.lang = new YamlFile(main, "lang");
		this.items = new YamlFile(main, "items");
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
	
	public YamlFile getItems() {
		return items;
	}
	
	public void reload() {
		getConfig().reload();
		getBanks().reload();
		getLang().reload();
	}
	
}
