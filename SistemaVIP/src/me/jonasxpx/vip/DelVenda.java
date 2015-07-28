package me.jonasxpx.vip;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DelVenda implements CommandExecutor{

	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(args.length >= 1){
			Manager.delVenda(args[0], sender.getName());
			return true;
		}
		sender.sendMessage("§cUse /delvenda <Código>");
		return false;
	}
}
