package dev.example.mine3x3;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Mine3x3Plugin extends JavaPlugin implements Listener {

    private final Set<UUID> enabled = new HashSet<>();
    private final Set<UUID> working = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        UUID id = player.getUniqueId();
        boolean now;
        if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
            now = true;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
            now = false;
        } else {
            now = !enabled.contains(id);
        }
        if (now) enabled.add(id); else enabled.remove(id);
        player.sendRichMessage(now
                ? "<green>3x3 mining enabled.</green> <gray>(Sneak to mine a single block.)</gray>"
                : "<red>3x3 mining disabled.</red>");
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (!enabled.contains(id) || working.contains(id) || player.isSneaking()) return;

        RayTraceResult hit = player.rayTraceBlocks(8);
        if (hit == null || hit.getHitBlockFace() == null) return;
        BlockFace face = hit.getHitBlockFace();

        ItemStack tool = player.getInventory().getItemInMainHand();
        Block origin = event.getBlock();

        // Two axes perpendicular to the face that was mined.
        int[][] offsets = switch (face) {
            case UP, DOWN -> new int[][]{{1, 0, 0}, {0, 0, 1}};
            case NORTH, SOUTH -> new int[][]{{1, 0, 0}, {0, 1, 0}};
            default -> new int[][]{{0, 0, 1}, {0, 1, 0}};
        };

        working.add(id);
        try {
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (a == 0 && b == 0) continue;
                    Block target = origin.getRelative(
                            a * offsets[0][0] + b * offsets[1][0],
                            a * offsets[0][1] + b * offsets[1][1],
                            a * offsets[0][2] + b * offsets[1][2]);
                    if (!canBreak(target, tool)) continue;
                    player.breakBlock(target); // fires events, so protection plugins still apply
                }
            }
        } finally {
            working.remove(id);
        }
    }

    private boolean canBreak(Block block, ItemStack tool) {
        Material type = block.getType();
        if (type.isAir() || block.isLiquid()) return false;
        if (type.getHardness() < 0) return false; // bedrock, barriers, etc.
        return block.isPreferredTool(tool);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        enabled.remove(event.getPlayer().getUniqueId());
        working.remove(event.getPlayer().getUniqueId());
    }
}
