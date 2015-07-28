package me.jonasxpx.vip;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ComprarVIP implements CommandExecutor{

	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(args.length >= 1){
			Manager.comprar(sender.getName(), args[0].toLowerCase());
			return true;
		}
		sender.sendMessage("§cUse: /comprarvip <NickDoVendedor>");
		return false;
	}
}
