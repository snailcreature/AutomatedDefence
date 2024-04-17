package codes.snail.block.entity;

import codes.snail.AutomatedDefence;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AutoTurretBlockEntity extends BlockEntity {
    public AutoTurretBlockEntity(BlockPos pos, BlockState state) {
        super (AutomatedDefence.AUTO_TURRET_BLOCK_ENTITY, pos, state);
    }

    public static void tick (World world, BlockPos pos, BlockState state,
                             AutoTurretBlockEntity be) {

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return super.toUpdatePacket();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return super.toInitialChunkDataNbt();
    }
}
