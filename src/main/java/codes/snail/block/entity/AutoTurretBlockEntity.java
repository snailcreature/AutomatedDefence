package codes.snail.block.entity;

import codes.snail.AutomatedDefence;
import codes.snail.inventory.ImplementedInventory;
import codes.snail.screen.AutoTurretLoadingScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static codes.snail.AutomatedDefence.MOD_ID;

public class AutoTurretBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(11,
            ItemStack.EMPTY);

    private static final int BOW_SLOT = 0;
    private static final int ENCHANT_SLOT = 1;

    public AutoTurretBlockEntity(BlockPos pos, BlockState state) {
        super (AutomatedDefence.AUTO_TURRET_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state,
                             AutoTurretBlockEntity be) {
        if (world.isClient()) {
            return;
        }

        if (hasAmmnunition() && hasBow()) {

        }
    }

    private boolean hasAmmnunition() {
        for (int i = 2; i < inventory.size(); i++) {
            if (!getStack(i).isEmpty() && getStack(i).isIn(ItemTags.ARROWS)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBow() {
        ItemStack bow_stack = this.getStack(BOW_SLOT);
        return !bow_stack.isEmpty() && bow_stack.isIn(ConventionalItemTags.BOWS);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(String.format("block.%s.auto_turret",
                MOD_ID));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
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

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId,
                                     PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoTurretLoadingScreenHandler(syncId, playerInventory,
                this);
    }
}
