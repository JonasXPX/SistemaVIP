package me.jonasxpx.vip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SistemaVIP extends JavaPlugin{
	
	public static Map<String, String> cmds = new HashMap<String, String>();
	public static ArrayList<String> pendente = new ArrayList<String>();
	public static String email;
	public static String token;
	public Economy eco;

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.email = getConfig().getString("Email");
		this.token = getConfig().getString("Token");
		Manager.start(this);
		cmds.putAll(Manager.loadIdsECommands());
		getCommand("ativarvip").setExecutor(new Ativar());
		getCommand("delvenda").setExecutor(new DelVenda());
		getCommand("comprarvip").setExecutor(new ComprarVIP());
		getCommand("vendervip").setExecutor(new VenderVIP());
		setupEconomy();
	}
	
	
	private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            eco = economyProvider.getProvider();
        }

        return (eco != null);
    }
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
	}
}
