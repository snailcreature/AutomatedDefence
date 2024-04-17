package codes.snail;

import codes.snail.block.AutoTurret;
import codes.snail.block.entity.AutoTurretBlockEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomatedDefence implements ModInitializer {
	public static final String MOD_ID = "automated-defence";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final AutoTurret AUTO_TURRET =
			new AutoTurret(AbstractBlock.Settings.create().strength(4.0f).hardness(Blocks.DISPENSER.getHardness()).requiresTool());
	public static final BlockEntityType<AutoTurretBlockEntity> AUTO_TURRET_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "auto_turret_block_entity"), FabricBlockEntityTypeBuilder.create(AutoTurretBlockEntity::new, AUTO_TURRET).build());

	private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(AUTO_TURRET))
			.displayName(Text.translatable(String.format("itemGroup.%s.creative_group", MOD_ID)))
			.entries((context, entries) -> {
				entries.add(AUTO_TURRET);
			})
			.build();
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Automated Defence initialising!");

		Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "creative_group"), ITEM_GROUP);

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "auto_turret"), AUTO_TURRET);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,
				"auto_turret"), new BlockItem(AUTO_TURRET,
				new FabricItemSettings().maxCount(1)));
	}
}