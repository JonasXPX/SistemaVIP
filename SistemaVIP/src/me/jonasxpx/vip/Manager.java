package me.jonasxpx.vip;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.uol.pagseguro.domain.Item;
import br.com.uol.pagseguro.domain.Transaction;

public abstract class Manager {

	private static SistemaVIP sistemaVip;
	
	protected static void start(SistemaVIP plugin){
		sistemaVip = plugin;
		System.out.println("_-> Sistema inicializado <-_");
	}
	
	public static boolean isRegistred(String CODE){
		if(new File(sistemaVip.getDataFolder() + "/codigos/" + CODE.toUpperCase() + ".yml").exists())
			return true;
		else
			return false;
	}
	
	public static Map<String, String> loadIdsECommands(){
		Map<String, String> cmds = new HashMap<>();
		for(String cmd : sistemaVip.getConfig().getStringList("Ids")){
			System.out.println(cmd);
			cmds.put(cmd.split(",")[0], cmd.split(",")[1]);
		}
		return cmds;
	}
	
	public static void register(final String nick, final Transaction tns) {
		try {
			System.out.println(sistemaVip.getDataFolder() + "/codigos/" + tns.getCode().toUpperCase() + ".yml");
			File file = new File(sistemaVip.getDataFolder() + "/codigos/" + tns.getCode().toUpperCase() + ".yml");
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			tns.getSender().setIp(Bukkit.getPlayer(nick).getAddress().getAddress().getHostAddress());
			config.set("Compra.Nick", nick.toLowerCase());
			config.set("Compra.Nome" , tns.getSender().getName());
			config.set("Compra.UF", tns.getShipping().getAddress().getState());
			config.set("Compra.IP", Bukkit.getPlayer(nick).getAddress().getAddress().getHostAddress());
			for(Item item : tns.getItems()){
				config.set("Itens." + item.getId() + ".Quantia", item.getQuantity());
			}
			config.save(file);
			new Log(nick, tns, Bukkit.getPlayer(nick)).register();;
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.getPlayer(nick).kickPlayer("§aOlá, "+ tns.getSender().getName() +"\n§aVocê esta prestes a ativar seu VIP!\n"
							+ "\nEntre novamente, e redigite o comando.");
				}
			}.runTask(sistemaVip);
		} catch (IOException e) {
			Bukkit.getPlayer(nick).sendMessage("§6Sistema com problemas, informe a um STAFF");
			e.printStackTrace();
		}
	}
	
	public static void ativar(Transaction tns, String player, boolean checkSaler){
		try {
			File file = new File(sistemaVip.getDataFolder() + "/codigos/" + tns.getCode().toUpperCase() + ".yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			Player play = Bukkit.getPlayer(player);
			if(!isOwner(tns.getCode(), player)){
				play.sendMessage("§6Este código não é seu!.");
				return;
			}
			for(Item item : tns.getItems()){
				if(config.getInt("Itens." + item.getId() + ".Quantia") > 0){
					config.set("Itens." + item.getId() + ".Quantia", config.getInt("Itens." + item.getId() + ".Quantia")-1);
					System.out.println(item.getId());
					String cmd = SistemaVIP.cmds.get(item.getId()).replaceAll("@player", player);
					System.out.println(cmd);
					Bukkit.dispatchCommand(sistemaVip.getServer().getConsoleSender(), cmd);
					break;
				}
				play.sendMessage("§aCodigo já atilizado!");
			}
			config.save(file);
		} catch (IOException e) {
			Bukkit.getPlayer(player).sendMessage("§6Sistema com problemas, informe a um STAFF");
			e.printStackTrace();
		}
	}
	
	public static boolean isOwner(String code, String player){
		File file = getFileByCode(code);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.getString("Compra.Nick").equalsIgnoreCase(player))
			return true;
		else
			return false;
	}
	
	public static File getFileByCode(String CODE){
		return new File(sistemaVip.getDataFolder() + "/codigos/" + CODE.toUpperCase() + ".yml");
	}
	
	public static boolean isForSale(String CODE){
		File file = new File(sistemaVip.getDataFolder() + "/vendas/" + CODE.toUpperCase() + ".yml");
		if(!file.exists()){
			return false;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.getBoolean("Venda.Ativo"))
			return true;
		else
			return false;
	}
	public static void venda(Transaction tns, int valor, String nick, String para){
		try{
			if(nick.equalsIgnoreCase(para)){
				Bukkit.getPlayer(nick).sendMessage("§6Você não pode vender para você mesmo");
				return;
			}
			if(isForSale(tns.getCode())){
				Bukkit.getPlayer(nick).sendMessage("§6Erro!, código ja postado para venda, para remover a venda digite /delvenda <CODIGO>");
				return;
			}
			if(isRegistred(tns.getCode())){
				Bukkit.getPlayer(nick).sendMessage("Esse código já foi registrado e não pode ser mais vendido.");
				return;
			}
			File file = new File(sistemaVip.getDataFolder() + "/vendas/" + tns.getCode().toUpperCase() + ".yml");
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Venda.Ativo", true);
			config.set("Venda.Valor", valor);
			config.set("Venda.Nick", nick);
			config.set("Venda.Para", para);
			sistemaVip.getConfig().set("Codigos." + nick.toLowerCase(), tns.getCode());
			sistemaVip.saveConfig();
			config.save(file);
			Bukkit.getPlayer(nick).sendMessage("§6Codigo validado!.\n"
					+ "§aSeu código já esta à venda, para remover a venda digite /delvenda <CODIGO>");
			if(Bukkit.getPlayerExact(para) != null)
				Bukkit.getPlayerExact(para).sendMessage("§a§l[!]§6Um VIP foi oferecido para você, no valor de §l" + NumberFormat.getCurrencyInstance().format(valor) 
						+ " §6Digite §l/comprarvip " + nick);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void comprar(String nick, String vendedor){
		Player player = Bukkit.getPlayer(nick);
		if(!sistemaVip.getConfig().contains("Codigos."+ vendedor.toLowerCase())){
			player.sendMessage("§6Não foi encontrado vendas para este jogador");
			return;
		}
		String CODE = sistemaVip.getConfig().getString("Codigos." + vendedor.toLowerCase());
		if(!isForSale(CODE)){
			player.sendMessage("Codigo não esta a venda!.");
			return;
		}
		File file = new File(sistemaVip.getDataFolder() + "/vendas/" + CODE.toUpperCase() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(!config.getString("Venda.Para").equalsIgnoreCase(nick)){
			player.sendMessage("§6Esta venda não é para você");
			return;
		}
		if(sistemaVip.eco.getBalance(player) < config.getInt("Venda.Valor")){
			player.sendMessage("§6Dinheiro insuficiente");
			return;
		}
		if(isRegistred(CODE)){
			sistemaVip.eco.withdrawPlayer(player, config.getInt("Venda.Valor"));
			sistemaVip.eco.depositPlayer(config.getString("Venda.Nick"), config.getInt("Venda.Valor"));
		}
		player.sendMessage("§aAguarde, analisando código do jogador...");
		new Thread(new CallPagSeguro(nick, CODE, Type.ATIVAÇAO)).start();
	}
	
	public static void delVenda(String code, String nick){
		if(!isForSale(code)){
			Bukkit.getPlayer(nick).sendMessage("§6Nada encontrado");;
			return;
		}
		if(isRegistred(code)){
			Bukkit.getPlayer(nick).sendMessage("§cCódigo já vendido!.");
			return;
		}
		File file = new File(sistemaVip.getDataFolder() + "/vendas/" + code.toUpperCase() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(!config.getString("Venda.Nick").equalsIgnoreCase(nick)){
			Bukkit.getPlayer(nick).sendMessage("§6Você não tem permissão para remover esta venda!.");
			return;
		}
		/*if(!config.getBoolean("Venda.Ativo")){
			Bukkit.getPlayer(nick).sendMessage("§6Código já adquirido pelo jogador, não pode ser mais removida.");
			return;
		}
		*/
		file.delete();
		Bukkit.getPlayer(nick).sendMessage("§6Código removido da venda!.");
	}
	
}
