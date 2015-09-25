package me.jonasxpx.vip;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ComprarVIP implements CommandExecutor{

	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(args.length >= 1){
			Player p = (Player)sender;
			for(ItemStack stack : p.getInventory().getContents()){
				if(stack != null){
					p.sendMessage("§cEsvazie seu inventario!.");
					return true;
				}
			}
			Manager.comprar(sender.getName(), args[0].toLowerCase());
			return true;
		}
		sender.sendMessage("§cUse: /comprarvip <NickDoVendedor>");
		return false;
	}
}
