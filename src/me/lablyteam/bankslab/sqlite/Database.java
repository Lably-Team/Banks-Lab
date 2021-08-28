package me.lablyteam.bankslab.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.bank.Bank;

// This code sucks, but works
public abstract class Database {
	protected BanksLabMain plugin;
    protected Connection connection;
    protected String table = "accounts";
    protected int tokens = 0;
    public Database(BanksLabMain instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load() throws SQLException;

    protected void initialize() {
        connection = getSQLConnection();
        
        try {
        	PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE player = ? OR (player IS NULL AND ? IS NULL)");
        	ResultSet rs = ps.executeQuery();
        	close(ps,rs);
        }catch(SQLException ex) {
        	ex.printStackTrace();
        }
    }
    
    public Account createAccount(Player player, Bank bank, double startBalance) {
        try (Connection conn = getSQLConnection(); PreparedStatement statement = conn.prepareStatement("INSERT INTO " + table + " (player, money, log, bank, lastinterest) VALUES(?, ?, ?, ?, ?);")) {
        	statement.setString(1, player.getUniqueId().toString());
        	statement.setString(2, String.valueOf(startBalance));
        	statement.setString(3, "");
        	statement.setString(4, bank.getId());
        	statement.setString(5, String.valueOf(System.currentTimeMillis()));
        	statement.executeUpdate();
        	statement.close();
        	Account account = Account.initAccount(plugin, player);
        	plugin.getCache().getAccounts().put(player.getUniqueId(), account);
        	return account;
        } catch(SQLException ex) {
        	plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
        return null;
    }
    
    public SQLData getData(Player player) {
    	try {
    		Connection conn = getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '"+player.getUniqueId()+"';");
            ResultSet result = ps.executeQuery();
            String uuid = player.getUniqueId().toString();
            SQLData sqlResult = null;
            while(result.next()) {
            	if(result.getString("player").equalsIgnoreCase(uuid)) {
            		double money = Double.valueOf(result.getString("money"));
            		String log = result.getString("log");
            		String bank = result.getString("bank");
            		long lastInterest = Long.valueOf(result.getString("lastinterest"));
            		sqlResult = new SQLData(uuid, money, log, bank, lastInterest);
            		close(ps, result);
            		break;
            	}
            }
            return sqlResult;
    	} catch(SQLException ex) {
    		plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
    	}
    	
    	return null;
    }
    
    public SetFactory createSetFactory(Player player) {
    	if(!hasAccount(player))return null;
    	SQLData data = getData(player);
    	SetFactory factory = new SetFactory(data);
    	return factory;
    }
    
    public boolean hasAccount(Player player) {
    	try {
    		Connection conn = getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '"+player.getUniqueId()+"';");
            ResultSet result = ps.executeQuery();
            String uuid = player.getUniqueId().toString();
            boolean res = false;
            while(result.next()) {
            	if(result.getString("player").equalsIgnoreCase(uuid)) {
            		res = true;
            		close(ps, result);
            		break;
            	}
            }
            return res;
    	} catch(SQLException ex) {
    		plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
    	}
    	
    	return false;
    }
    
    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
    
    
    public class SQLData {
    	private String uuid;
    	private double money;
    	private String log;
    	private String bank;
    	private long lastInterest;
    	
    	public SQLData(String uuid, double money, String log, String bank, long lastInterest) {
    		this.uuid = uuid;
    		this.money = money;
    		this.log = log;
    		this.bank = bank;
    		this.lastInterest = lastInterest;
    	}
    	
    	public UUID getUUID() {
    		return UUID.fromString(uuid);
    	}
    	
		public double getMoney() {
			return money;
		}
		
		public String getLog() {
			return log;
		}
		
		public String getBankName() {
			return bank;
		}
		
		public long getLastInterest() {
			return lastInterest;
		}
    }
    
    public class SetFactory {
    	private String uuid;
    	private double money;
    	private String log;
    	private String bank;
    	private long lastInterest;
    	
    	public SetFactory(String uuid, double money, String log, String bank, long lastInterest) {
    		this.uuid = uuid;
    		this.setMoney(money);
    		this.setLog(log);
    		this.setBank(bank);
    		this.setLastInterest(lastInterest);
    	}
    	
    	public SetFactory(SQLData data) {
    		this(data.getUUID().toString(), data.getMoney(), data.getLog(), data.getBankName(), data.getLastInterest());
    	}

		public double getMoney() {
			return money;
		}

		public void setMoney(double money) {
			this.money = money;
		}

		public String getUuid() {
			return uuid;
		}

		public String getLog() {
			return log;
		}

		public void setLog(String log) {
			this.log = log;
		}

		public String getBank() {
			return bank;
		}

		public void setBank(String bank) {
			this.bank = bank;
		}
		
		public void setLastInterest(long lastInterest) {
			this.lastInterest = lastInterest;
		}
    	
		public void executeChanges(Connection connection) {
			try {
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + uuid + "';");
				ResultSet result = statement.executeQuery();
				
				while(result.next()) {
					String resultUUID = result.getString("player");
					if(resultUUID.equalsIgnoreCase(uuid)) {
						PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET money = '"+String.valueOf(money)+"', log = '"+log+"', bank = '"+bank+"', lastinterest = '"+lastInterest+"' WHERE player = '"+uuid+"';");
						ps.execute();
						close(ps, result);
						break;
					}
				}
			}catch(SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			}
		}
    	
    }
}

// No me funen
// ~ Atog