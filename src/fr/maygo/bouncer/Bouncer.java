package fr.maygo.bouncer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class Bouncer extends JavaPlugin implements Listener {
	
	EnumParticle particle;
	int multiplier;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		try {
			particle = EnumParticle.valueOf(getConfig().getString("particle"));
		} catch (IllegalArgumentException e) {
			Bukkit.getConsoleSender().sendMessage("§cCould'nt load the particle ! Error : Particle Not Found !");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		multiplier = getConfig().getInt("multiplier");
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();
		if(event.getAction().equals(Action.PHYSICAL)) {
			if(event.getClickedBlock().getType() == Material.GOLD_PLATE) {
				player.setVelocity(player.getLocation().getDirection().multiply(multiplier));
				player.setVelocity(new Vector(player.getVelocity().getX(), multiplier, player.getVelocity().getZ()));
				playEffect(particle, player.getLocation());
			}
		}
	}
	
	public static void playEffect(EnumParticle particle, Location loc) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
				(float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0, 0, 0, 0, 1);
		for (Player online : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
}
