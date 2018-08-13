package be.feeps.epicballoons.balloons;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import be.feeps.epicballoons.config.LangConfig;
import be.feeps.epicballoons.nms.Reflection;
import be.feeps.epicballoons.utils.Sounds;

/**
 * Created by Feeps on 16/08/2017
 */

public class EpicBalloons {
    public static Map<Player, EpicBalloons> epicBalloonsMap = new HashMap<>();
    private Player player;
    private ItemStack item;
    private Slime slime;
    private ArmorStand armorStand;

    public EpicBalloons(Player player, ItemStack item) {
	this.player = player;
	this.item = item;
	if (epicBalloonsMap.containsKey(this.player))
	    epicBalloonsMap.get(this.player).remove();

	epicBalloonsMap.put(player, this);
    }

    @SuppressWarnings("unchecked")
    public void spawn() {
	try {
	    // Get the handle of the world
	    Object world = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());

	    // Create a new instance of a balloon
	    Object balloon;
	    if (Bukkit.getVersion().contains("1.13")) {
		balloon = Reflection.getClass(Reflection.PackageType.BFB, "NMSBalloons")
			.getConstructor(Reflection.getClass(Reflection.PackageType.NMS, "World")).newInstance(world);
		((be.feeps.epicballoons.nms.v1_13_R1.NMSBalloons) balloon).setData(this.player, this.item);
	    } else {
		balloon = Reflection.getClass(Reflection.PackageType.BFB, "NMSBalloons")
			.getConstructor(Reflection.getClass(Reflection.PackageType.NMS, "World"), Player.class,
				ItemStack.class)
			.newInstance(world, this.player, this.item);
	    }
	    // Add entity on the world
	    world.getClass()
		    .getMethod("addEntity", Reflection.getClass(Reflection.PackageType.NMS, "Entity"),
			    CreatureSpawnEvent.SpawnReason.class)
		    .invoke(world, balloon, CreatureSpawnEvent.SpawnReason.CUSTOM);

	    // Get the slime from the NMSBalloons class
	    this.slime = (Slime) balloon.getClass().getMethod("getSlime").invoke(balloon);
	    // Get the armorstand from the NMSBalloons class
	    this.armorStand = (ArmorStand) balloon.getClass().getMethod("getContents").invoke(balloon);
	    this.player.sendMessage(LangConfig.Msg.MessagesSpawn.toPrefix());
	} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
		| InstantiationException e) {
	    e.printStackTrace();
	}
    }

    public void remove() {
	this.slime.remove();
	this.player.getWorld().playSound(this.armorStand.getLocation().clone().add(0, 2, 0),
		Sounds.CHICKEN_EGG_POP.bukkitSound(), 1, 2);
	if (Bukkit.getServer().getVersion().contains("1.13")) {
	    this.player.spawnParticle(Particle.CLOUD, this.armorStand.getLocation().clone().add(0, 2, 0), 10, 0, 0, 0);
	} else {
	    this.player.spigot().playEffect(this.armorStand.getLocation().clone().add(0, 2, 0), Effect.valueOf("CLOUD"),
		    0, 0, 0f, 0f, 0f, 0.1f, 10, 32);
	}
	this.armorStand.remove();
	epicBalloonsMap.remove(this.player, this);
	this.player.sendMessage(LangConfig.Msg.MessagesRemove.toPrefix());
    }

    public void setItem(ItemStack item) {
	this.armorStand.setHelmet(item);
    }
}
