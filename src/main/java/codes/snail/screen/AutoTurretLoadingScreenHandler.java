package codes.snail.screen;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import static codes.snail.AutomatedDefence.AUTO_TURRET_LOADING_SCREEN_HANDLER;

public class AutoTurretLoadingScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public AutoTurretLoadingScreenHandler(int syncId,
                                          PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(11));
    }

    public AutoTurretLoadingScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(AUTO_TURRET_LOADING_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        // BOW_SLOT
        this.addSlot(new Slot(inventory, 0, 44, 18) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ConventionalItemTags.BOWS);
            }
        });
        // ENCHANTMENT_SLOT
        this.addSlot(new Slot(inventory, 1, 44, 54) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.ENCHANTED_BOOK);
            }
        });

        int m;
        int l;
        // Arrow Slots
        for (m = 0; m < 3; m++) {
            for (l = 0; l < 3; l++) {
                this.addSlot(new Slot(inventory, 2 + l + m*3, 97 + l*18,
                        17 + m*18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return stack.isIn(ItemTags.ARROWS);
                    }
                });
            }
        }
        // Player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9,
                        8 + l * 18, 84 + m * 18));
            }
        }
        // Player hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.markDirty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}
