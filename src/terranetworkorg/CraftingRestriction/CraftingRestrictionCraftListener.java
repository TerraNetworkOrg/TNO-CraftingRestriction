package terranetworkorg.CraftingRestriction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

public class CraftingRestrictionCraftListener implements Listener{
	
	private CraftingRestriction plugin;
	
	public CraftingRestrictionCraftListener(CraftingRestriction plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryCraft(InventoryCraftEvent event){
		if (event.isCancelled())
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		Player player = event.getPlayer();
		ItemStack itemKey = event.getResult();
		
		if (itemKey == null){
			return;
		} else {			
			int itemID = itemKey.getTypeId();
			String blockString = (new Integer(itemID)).toString().replace("'", "");
			ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("Restrict.ID");
	    	Set<String> allKeys = section.getKeys(false);
			
			Set<String> blacklistArray = new HashSet<String>(Arrays.asList(this.plugin.getConfig().getString("General.Blacklist").split(",")));
			
			int itemtype = itemKey.getTypeId();
			if (blacklistArray.contains("" + itemtype)){
				blockString = (new Integer(itemtype)).toString().replace("'", "");
			} else{
				int itemdamage = itemKey.getDurability();
				if(itemdamage == 0){
					blockString = (new Integer(itemtype)).toString().replace("'", "");
				} else{
					blockString = (new Integer(itemtype)).toString().replace("'", "") + ":" + (new Integer(itemdamage)).toString().replace("'", "");
				}				
			}
	    	
			if(allKeys.contains(blockString)){
				if (CraftingRestriction.permission.has(player, this.plugin.getConfig().getString("Restrict.ID." + blockString))){
					return;
				} else{
					player.sendMessage(ChatColor.RED + this.plugin.getLanguage().getString("Messages.CRAFT_DENIED"));
					event.setCancelled(true);
				}
			}
		}
	}
}
