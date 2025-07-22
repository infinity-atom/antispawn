package me.lennyd.antispawn;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class Util {
    public static Text formatInfo(String text) {
        return Text.literal("")
                .append(Text.literal("[antispawn] ").formatted(Formatting.DARK_AQUA))
                .append(Text.literal(text).formatted(Formatting.AQUA));
    }

    public static Text formatError(String text) {
        return Text.literal("")
                .append(Text.literal("[antispawn] ").formatted(Formatting.DARK_AQUA))
                .append(Text.literal(text).formatted(Formatting.RED));
    }

    public static boolean isInsideBox(BlockPos pos, BlockPos corner1, BlockPos corner2) {
        int minX = Math.min(corner1.getX(), corner2.getX());
        int maxX = Math.max(corner1.getX(), corner2.getX());

        int minY = Math.min(corner1.getY(), corner2.getY());
        int maxY = Math.max(corner1.getY(), corner2.getY());

        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }
}
