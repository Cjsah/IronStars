package com.xekr.ironstars.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xekr.ironstars.efficiency.EFFMachine;
import com.xekr.ironstars.efficiency.EFFNetwork;
import com.xekr.ironstars.registry.AllDimensions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("is").then(Commands.literal("test").executes(context -> {
            CommandSourceStack source = context.getSource();
            ServerPlayer player = source.getPlayerOrException();
            Vec3 from = player.getEyePosition();
            float pitch = player.getXRot();
            float yaw = player.getYRot();
            float px = Mth.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
            float pz = Mth.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
            float zoom = -Mth.cos(-pitch * ((float)Math.PI / 180F));
            float y = Mth.sin(-pitch * ((float)Math.PI / 180F));
            float x = pz * zoom;
            float z = px * zoom;
            Vec3 to = from.add((double)x * 5.0D, (double)y * 5.0D, (double)z * 5.0D);
            BlockHitResult clip = source.getLevel().clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            if (clip.getType() == HitResult.Type.BLOCK && source.getLevel().getBlockEntity(clip.getBlockPos()) != null) {
                BlockPos blockPos = clip.getBlockPos();
                BlockEntity blockEntity = source.getLevel().getBlockEntity(blockPos);
                if (blockEntity != null) {
                    BlockDataAccessor accessor = new BlockDataAccessor(blockEntity, blockPos);
                    source.sendSuccess(accessor.getPrintSuccess(accessor.getData()), false);
                    if (blockEntity instanceof EFFMachine machine) {
                        System.out.println(machine.getNetwork());
                        System.out.println(EFFNetwork.NETWORK);
                    }
                }
            }else source.sendFailure(new TextComponent("Unknown Pos"));
            return Command.SINGLE_SUCCESS;
        })));
    }
}
