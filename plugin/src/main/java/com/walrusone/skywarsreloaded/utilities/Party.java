package com.walrusone.skywarsreloaded.utilities;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

public class Party {
	
	private static ArrayList<Party> parties = new ArrayList<>();
	private UUID leader;
	private String name;
	private ArrayList<UUID> members;
	private ArrayList<UUID> invited;

	public Party(Player player, String partyName) {
		
		if(CheckParty(player)) {
			sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.alreadyinparty"));
			return;
		
		leader = player.getUniqueId();
		name = partyName;
		members = new ArrayList<>();
		invited = new ArrayList<>();
		members.add(player.getUniqueId());
		parties.add(this);
	}
	
	public static void removeParty(Party party) {
		party.sendPartyMessage(new Messaging.MessageFormatter()
				.setVariable("leader", Bukkit.getPlayer(party.getLeader()).getName())
				.setVariable("partyname", party.getPartyName()).format("party.disbanded"));
		parties.remove(party);
	}
	
	public static Party getParty(Player player) {
		for (Party party: parties) {
			if (party.getMembers().contains(player.getUniqueId())) {
				return party;
			}
		}
		return null;
	}
	
	public String getPartyName() {
		return name;
	}
	
	public void setPartyName(String newName) {
		name = newName;
	}
	
	private void addMember(Player player) {
		if(CheckParty(player)) {
			sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.alreadyinparty"));
			return;
	}
		if (!members.contains(player.getUniqueId())) {
			this.sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.joined"));
			members.add(player.getUniqueId());
		}
	}
	
	public void removeMember(Player player) {
		if (members.contains(player.getUniqueId())) {
			members.remove(player.getUniqueId());
			this.sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.left"));
		}
	}
	
	public void sendPartyMessage(String message) {
		for (UUID uuid: members) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}
	
	public UUID getLeader() {
		return leader;
	}
	
	public ArrayList<UUID> getMembers() {
		return members;
	}
	
	public boolean CheckParty(Player ply) {
		
		if(getParty(ply) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getSize() {
		return members.size();
	}

	public void invite(Player player) {
		if(CheckParty(player)) {
			sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.alreadyinparty"));
			return;
		}
		final UUID invite = player.getUniqueId();
		this.invited.add(invite);
		new BukkitRunnable() {

			@Override
			public void run() {
				boolean remove = false;
				if (invited.contains(invite)) {
					remove = true;
				}
				if (remove) {
					invited.remove(invite);
				}
			}
			
		}.runTaskLater(SkyWarsReloaded.get(), 300);
	}
	
	public boolean acceptInvite(Player player) {
		boolean result = false;
		if (invited.contains(player.getUniqueId()) && members.size() < SkyWarsReloaded.getCfg().maxPartySize()) {
			addMember(player);
			result = true;
		}
		invited.remove(player.getUniqueId());
		return result;
	}

	public static Party getPartyOfInvite(Player player) {
		for (Party party: parties) {
			if (party.getInvited().contains(player.getUniqueId())) {
				return party;
			}
		}
		return null;
	}

	private ArrayList<UUID> getInvited() {
		return invited;
	}

	public boolean declineInvite(Player player) {
		if (invited.contains(player.getUniqueId())) {
			invited.remove(player.getUniqueId());
			sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.declined"));
			return true;
		}
		return true;
	}
}
