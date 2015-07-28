package me.jonasxpx.vip;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Ativar implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player p = (Player)sender;
		System.out.println(p.getInventory().firstEmpty());
		for(ItemStack stack : p.getInventory().getContents()){
			if(stack != null){
				p.sendMessage("§cEsvazie seu inventario!.");
				return true;
			}
		}
		if(args.length >= 1){
			sender.sendMessage("§aValidando aguarde...");
			if(SistemaVIP.pendente.contains(sender.getName())){
				sender.sendMessage("§aAguarde seu código esta em analize");
				return true;
			}
			SistemaVIP.pendente.add(sender.getName());
			new Thread(new CallPagSeguro(sender.getName(), args[0].toUpperCase(), Type.ATIVAÇAO)).start();
		}
		return true;
	}
}
