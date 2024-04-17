package codes.snail.block;

import codes.snail.AutomatedDefence;
import codes.snail.block.entity.AutoTurretBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class AutoTurret extends BlockWithEntity implements BlockEntityProvider {
    public AutoTurret(Settings settings) {
        super(settings);
    }

    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(0, 0, 0, 16, 4, 16),
            Block.createCuboidShape(2, 4, 2, 14, 16, 14),
            Block.createCuboidShape(7, 19, 7, 9, 29, 9),
            Block.createCuboidShape(2, 29, 2, 14, 32, 14),
            Block.createCuboidShape(2, 16, 2, 14, 19, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoTurretBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AutomatedDefence.AUTO_TURRET_BLOCK_ENTITY,
                AutoTurretBlockEntity::tick);
    }
}
