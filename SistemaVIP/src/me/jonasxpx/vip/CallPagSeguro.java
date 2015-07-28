package me.jonasxpx.vip;

import org.bukkit.Bukkit;

import br.com.uol.pagseguro.domain.AccountCredentials;
import br.com.uol.pagseguro.domain.Transaction;
import br.com.uol.pagseguro.exception.PagSeguroServiceException;
import br.com.uol.pagseguro.service.TransactionSearchService;

public class CallPagSeguro implements Runnable{

	private String player;
	private String para;
	private String code;
	private Transaction tns = null;
	private Type type;
	private int valor;
	
	public CallPagSeguro(String player, String code, Type type) {
		this.player = player;
		this.code = code;
		this.type = type;
	}
	public CallPagSeguro(String code, int valor, String nick, String para) {
		this.player = nick;
		this.para = para;
		this.code = code;
		this.type = Type.VENDA;
		this.valor = valor;
	}
	
	@Override
	public void run() {
		try{
			tns = TransactionSearchService.searchByCode(new AccountCredentials(SistemaVIP.email, SistemaVIP.token),
					code);
			if(tns != null){
			/*	if(tns.getDate().before(new Date(Long.parseLong("1437537585524")))){
					Bukkit.getPlayer(player).sendMessage("§cVocê esta tentando ativar um VIP antigo, use /ativar <Código>");
					SistemaVIP.pendente.remove(player);
					return;
				}*/
				if(tns.getItems().get(0).getId().equalsIgnoreCase("0001") || tns.getItems().get(0).getId().equalsIgnoreCase("0002")){
					Bukkit.getPlayer(player).sendMessage("§cVocê esta tentando ativar um VIP do sistema antigo, use o comando /ativar <código>");
					SistemaVIP.pendente.remove(player);
					return;
				}
				if(tns.getStatus().ordinal() != 3){
					Bukkit.getPlayer(player).sendMessage("§cErro, " + tns.getStatus().getDescription());
					SistemaVIP.pendente.remove(player);
					return;
				}
				switch (type) {
				case ATIVAÇAO:
					SistemaVIP.pendente.remove(player);
					if(Manager.isRegistred(tns.getCode()))
						Manager.ativar(tns, player, true);
					else
						Manager.register(player, tns);
					break;

				case VENDA:
					Manager.venda(tns, valor, player, para);
					break;
				}
				
			}
		}catch(PagSeguroServiceException e){
			Bukkit.getPlayer(player).sendMessage("§cErro, Mensagem do PagSeguro:\n "+ e.getMessage());
			SistemaVIP.pendente.remove(player);
		}
	}
	
	/*
	 * Status = cancelado, aprovado, aguarndado pagamento....
	 */
}
