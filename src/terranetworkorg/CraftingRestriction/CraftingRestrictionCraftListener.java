package terranetworkorg.CraftingRestriction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

@SuppressWarnings("unused")
public class CraftingRestrictionCraftListener extends InventoryListener{
	
	private CraftingRestriction plugin;
	
	public CraftingRestrictionCraftListener(CraftingRestriction plugin){
		this.plugin = plugin;
	}
	
	public void onInventoryCraft(InventoryCraftEvent event){
		if (event.isCancelled())
			return;
		if (!(event.getPlayer() instanceof Player))
			return;
		Player player = event.getPlayer();
		ItemStack itemKey = event.getResult();
		int itemID = itemKey.getTypeId();
		String blockString = (new Integer(itemID)).toString().replace("'", "");
		if (CraftingRestriction.config.getKeys("Restrict.ID").contains(blockString)){
			if (CraftingRestriction.permission.has(player, CraftingRestriction.config.getString("Restrict.ID." + blockString))){
				return;
			} else{
				player.sendMessage(ChatColor.RED + CraftingRestriction.language.getString("Messages.CRAFT_DENIED"));
				event.setCancelled(true);
			}
		}
	}
}
