package me.lennyd.antispawn.mixin;

import me.lennyd.antispawn.Util;
import me.lennyd.antispawn.antispawn;
import me.lennyd.antispawn.config.ConfigRegion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(SpawnRestriction.class)
public class SpawnRestrictionMixin {
    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void canSpawn(EntityType<?> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld wd = world.toServerWorld();

        try {
            for (ConfigRegion region : antispawn.CF.regions) {
                if (!Objects.equals(region.dimension,
                        wd.getRegistryKey().getValue().toString())) continue;
                if (Util.isInsideBox(pos,
                        new BlockPos(region.corner1X, region.corner1Y, region.corner1Z),
                        new BlockPos(region.corner2X, region.corner2Y, region.corner2Z))) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        } catch (Exception exception) {
            antispawn.LOGGER.error("While trying to prevent spawn", exception);
        }
    }
}
