package me.lablyteam.bankslab;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.bank.Bank;
import me.lablyteam.bankslab.bank.BankImpl;
import me.lablyteam.bankslab.commands.BanksLabCommand;
import me.lablyteam.bankslab.gui.BankMenu;
import me.lablyteam.bankslab.lib.Cache;
import me.lablyteam.bankslab.lib.Files;
import me.lablyteam.bankslab.lib.YamlFile;
import me.lablyteam.bankslab.listeners.BankMenuListener;
import me.lablyteam.bankslab.listeners.PlayerJoinListener;
import me.lablyteam.bankslab.listeners.PlayerQuitListener;
import me.lablyteam.bankslab.sqlite.SQLite;
import me.lablyteam.bankslab.task.InterestTask;
import net.milkbowl.vault.economy.Economy;

public class BanksLabMain extends JavaPlugin {
	
	private Cache cache;
	private AnnotatedCommandTreeBuilder builder;
	private BukkitCommandManager commandManager;
	private Economy economy;
	private SQLite sqlite;
	private BankMenu bankMenu;
	
	private YamlFile config;
	private Files files;
	
	public void onEnable() {
		if(!isEnabled("Vault", "ProtocolLib")) {
			getLogger().severe("Dependencies not found! (Vault or ProtocolLib)");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		if(!setupEconomy()) {
			getLogger().severe("Economy not found! consider using Essentials or other plugin for economy.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		this.cache = new Cache();
		
		this.config = new YamlFile(this, "config");
		this.files = new Files(this);
		
		this.sqlite = new SQLite(this);
		this.bankMenu = new BankMenu(this);
		
		YamlFile banks = files.getBanks();
		
		if(!banks.contains("default")) {
			getLogger().severe("§cYou need to create an bank with the id \"default\" in the §fbanks.yml§c file.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		cache.getBanks().clear();
		for(String name : banks.getKeys(false)) {
			ConfigurationSection bankSection = banks.getConfigurationSection(name);
			Bank bank = new BankImpl(name, bankSection);
			cache.getBanks().put(name, bank);
		}
		
		try {
			this.sqlite.load();
		} catch (SQLException ex) {
			getLogger().log(Level.SEVERE, "An error ocurred while enabling the plugin:", ex);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		/*
		RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
		this.economy = provider.getProvider();
		*/
		commandManager = new BukkitCommandManager("BanksLab");
		PartInjector injector = PartInjector.create();
		injector.install(new DefaultsModule());
		injector.install(new BukkitModule());
		builder = new AnnotatedCommandTreeBuilderImpl(injector);
		
		registerCommand(new BanksLabCommand(this));
		
		registerEvents(
				new PlayerJoinListener(this),
				new PlayerQuitListener(this),
				new BankMenuListener(this));
		
		// If /reload
		getCache().getAccounts().clear();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!getDatabase().hasAccount(player))return;
			Account account = Account.initAccount(this, player);
			getCache().getAccounts().put(player.getUniqueId(), account);
		}
		
		Bukkit.getScheduler().runTaskTimer(this, new InterestTask(this), 0L, 40L);
		
		getLogger().info("§aBanksLab loaded");
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public YamlFile getConfig() {
		return config;
	}
	
	public Files getFiles() {
		return files;
	}
	
	public BankMenu getBankMenu() {
		return bankMenu;
	}
	
	public Account getAccount(Player player) {
		return getCache().getAccounts().get(player.getUniqueId());
	}
	
	public void registerCommand(CommandClass commandClass) {
		commandManager.registerCommands(builder.fromClass(commandClass));
	}

	public AnnotatedCommandTreeBuilder getCommandBuilder() {
		return builder;
	}

	public BukkitCommandManager getCommandManager() {
		return commandManager;
	}
	
	public Economy getEconomy() {
		return economy;
	}
	
	public SQLite getDatabase() {
		return sqlite;
	}
	
	private void registerEvents(Listener...listeners) {
		for(Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}
	
	private boolean isEnabled(String...plugins) {
		boolean result = true;
		for(String plugin : plugins) {
			if(getServer().getPluginManager().getPlugin(plugin) == null) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
}