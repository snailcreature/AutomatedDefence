package codes.snail.block.entity;

import codes.snail.AutomatedDefence;
import codes.snail.inventory.ImplementedInventory;
import codes.snail.screen.AutoTurretLoadingScreenHandler;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codes.snail.AutomatedDefence.MOD_ID;

public class AutoTurretBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(11,
            ItemStack.EMPTY);

    private static final int BOW_SLOT = 0;
    private static final Vec3d BOW_POS = new Vec3d(0.5, 1.5, 0.5);

    private static final int ENCHANT_SLOT = 1;
    private int POWER;
    private int PUNCH;
    private int FLAME;
    private boolean INFINITY;

    private static final Random ARROW_DITHER = Random.create();

    private static final int FULL_LOAD = 10;
    private int LOAD = 0;

    private Box VIEW_BOX;

    @Nullable
    private HostileEntity TARGET = null;

    public AutoTurretBlockEntity(BlockPos pos, BlockState state) {
        super (AutomatedDefence.AUTO_TURRET_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state, AutoTurretBlockEntity be) {
        if (world.isClient()) {
            return;
        }

        if (VIEW_BOX == null) {
            VIEW_BOX = new Box(pos.subtract(new Vec3i(16, 16, 16)),
                    pos.add(16, 16, 16));
            markDirty();
        }

        if (hasAmmunition() && hasBow()) {
            if (TARGET == null) {
                Random random = Random.create();
                List<HostileEntity> nearbyEntities =
                        world.getEntitiesByType(TypeFilter.instanceOf(HostileEntity.class), VIEW_BOX, EntityPredicates.VALID_ENTITY);
                if (!nearbyEntities.isEmpty()) {
                    TARGET =
                            nearbyEntities.get(random.nextInt(nearbyEntities.size()));
                }
                markDirty();
                return;
            }
            if (!TARGET.getPos().isInRange(pos.toCenterPos(), 16) || TARGET.isDead()) {
                TARGET = null;
                return;
            }
            LOAD += 1;
            if (LOAD >= FULL_LOAD) {
                calculateEnchantments();
                Vec3d velocity =
                        (pos.toCenterPos().add(BOW_POS)
                                .subtract(TARGET.getX(),
                                        TARGET.getY()+TARGET.getEyeY(),
                                        TARGET.getZ())
                        ).add(
                                MathHelper.nextBetween(ARROW_DITHER, -0.25f, 0.25f),
                                MathHelper.nextBetween(ARROW_DITHER, -1f, 0.5f),
                                MathHelper.nextBetween(ARROW_DITHER, -0.25f, 0.25f)
                        ).normalize().rotateY(MathHelper.PI).multiply(10, 0.1, 10);

                ItemStack arrows = getArrowFromInventory();
                if (arrows.isOf(Items.SPECTRAL_ARROW)) {
                    SpectralArrowEntity arrow = new SpectralArrowEntity(world,
                            pos.getX() + BOW_POS.getX(),
                            pos.getY() + BOW_POS.getY(),
                            pos.getZ() + BOW_POS.getZ());
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    arrow.setVelocity(velocity);
                    if (POWER > 0) arrow.setDamage(arrow.getDamage() + (double)POWER * 0.5 + 0.5);
                    if (PUNCH > 0) arrow.setPunch(PUNCH);
                    if (FLAME > 0) arrow.setOnFireFor(100);
                    world.spawnEntity(arrow);
                    arrows.decrement(1);
                }
                else {
                    ArrowEntity arrow = new ArrowEntity(world,
                            pos.getX() + BOW_POS.getX(),
                            pos.getY() + BOW_POS.getY(),
                            pos.getZ() + BOW_POS.getZ());
                    arrow.initFromStack(arrows);
                    arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    arrow.setVelocity(velocity);
                    if (POWER > 0) arrow.setDamage(arrow.getDamage() + (double)POWER * 0.5 + 0.5);
                    if (PUNCH > 0) arrow.setPunch(PUNCH);
                    if (FLAME > 0) arrow.setOnFireFor(100);
                    world.spawnEntity(arrow);
                    if (!INFINITY) {
                        arrows.decrement(1);
                    }
                }
                getStack(BOW_SLOT).damage(1, TARGET, (p) -> {});
                LOAD = 0;
            }
            markDirty();
            return;
        }
        TARGET = null;
        markDirty();
    }

    private ItemStack getArrowFromInventory() {
        for (int i = 2; i < inventory.size(); i++) {
            if (!getStack(i).isEmpty() && getStack(i).isIn(ItemTags.ARROWS)) {
                return getStack(i);
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean hasAmmunition() {
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

    private void calculateEnchantments() {
        ItemStack bow = getStack(BOW_SLOT);
        ItemStack book = getStack(ENCHANT_SLOT);

        POWER = Math.max(EnchantmentHelper.getLevel(Enchantments.POWER, bow),
                EnchantmentHelper.get(book).get(Enchantments.POWER) != null ?
                        EnchantmentHelper.get(book).get(Enchantments.POWER) : 0);

        PUNCH = Math.max(EnchantmentHelper.getLevel(Enchantments.PUNCH, bow),
                EnchantmentHelper.get(book).get(Enchantments.PUNCH) != null ?
                        EnchantmentHelper.get(book).get(Enchantments.PUNCH) : 0);

        FLAME = Math.max(EnchantmentHelper.getLevel(Enchantments.FLAME, bow),
                EnchantmentHelper.get(book).get(Enchantments.FLAME) != null ?
                        EnchantmentHelper.get(book).get(Enchantments.FLAME) : 0);

        INFINITY = Math.max(EnchantmentHelper.getLevel(Enchantments.INFINITY, bow),
                EnchantmentHelper.get(book).get(Enchantments.INFINITY) != null ?
                        EnchantmentHelper.get(book).get(Enchantments.INFINITY) : 0) > 0;
        markDirty();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(String.format("block.%s.auto_turret",
                MOD_ID));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        nbt.putDouble("auto_turret_vb_minX", VIEW_BOX.minX);
        nbt.putDouble("auto_turret_vb_minY", VIEW_BOX.minY);
        nbt.putDouble("auto_turret_vb_minZ", VIEW_BOX.minZ);
        nbt.putDouble("auto_turret_vb_maxX", VIEW_BOX.maxX);
        nbt.putDouble("auto_turret_vb_maxY", VIEW_BOX.maxY);
        nbt.putDouble("auto_turret_vb_maxZ", VIEW_BOX.maxZ);
        nbt.putInt("auto_turret_power", POWER);
        nbt.putInt("auto_turret_punch", PUNCH);
        nbt.putInt("auto_turret_flame", FLAME);
        nbt.putBoolean("auto_turret_infinity", INFINITY);
        if (TARGET != null) nbt.putInt("auto_turret_target_id", TARGET.getId());
        nbt.putInt("auto_turret_load", LOAD);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        LOAD = nbt.getInt("auto_turret_load");
        try {
            TARGET = (HostileEntity) world.getEntityById(nbt.getInt("auto_turret_target_id"));
        } catch (Exception err) {
            TARGET = null;
        }
        POWER = nbt.getInt("auto_turret_power");
        PUNCH = nbt.getInt("auto_turret_punch");
        FLAME = nbt.getInt("auto_turret_flame");
        INFINITY = nbt.getBoolean("auto_turret_infinity");
        VIEW_BOX = new Box(nbt.getDouble("auto_turret_vb_minX"),
                nbt.getDouble("auto_turret_vb_minY"),
                nbt.getDouble("auto_turret_vb_minZ"),
                nbt.getDouble("auto_turret_vb_maxX"),
                nbt.getDouble("auto_turret_vb_maxY"),
                nbt.getDouble("auto_turret_vb_maxZ"));
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
