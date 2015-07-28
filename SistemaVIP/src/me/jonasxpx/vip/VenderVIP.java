package me.jonasxpx.vip;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VenderVIP implements CommandExecutor{

	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		try{
			sender.sendMessage("§aValidando código, aguarde....");
			new Thread(new CallPagSeguro(args[0], Integer.parseInt(args[2]), sender.getName(), args[1])).start();
		}catch(ArrayIndexOutOfBoundsException ex){
			sender.sendMessage("§cUse /vendervip <Código> <Nick para quem ira vender> <valor>");
		}
		return false;
	}
}
