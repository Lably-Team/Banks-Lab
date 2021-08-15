package me.lablyteam.bankslab.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.lablyteam.bankslab.BanksLabMain;

public class SQLite extends Database {
	private String dbname;
	private BanksLabMain plugin;
    public SQLite(BanksLabMain plugin){
        super(plugin);
        this.plugin = plugin;
        dbname = "accounts";
    }

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
            	Bukkit.getConsoleSender().sendMessage("File write error: "+dbname+".db");
            }
        }
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.", "");
        }
        return null;
    }

    public void load() throws SQLException {
        connection = getSQLConnection();
        try(Statement s = connection.createStatement()) {
        	s.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (player varchar(32) NOT NULL PRIMARY KEY, money varchar(32) NOT NULL, log varchar(32) NOT NULL, bank varchar(32) NOT NULL, lastinterest varchar(32) NOT NULL);");
            s.close();
        }
        initialize();
    }
}