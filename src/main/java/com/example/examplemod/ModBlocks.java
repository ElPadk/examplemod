package com.example.examplemod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.GLASS)
            .noOcclusion()
            .isRedstoneConductor((state, getter, pos) -> false)
            .isSuffocating((state, getter, pos) -> false)
            .isViewBlocking((state, getter, pos) -> false)
            .strength(0.3f)
            .instabreak()
        ) {
            @Override
            public boolean useShapeForLightOcclusion(BlockState state) {
                return false;
            }
            
            @Override
            public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
                return Shapes.empty();
            }
            
            @Override
            public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
                return Shapes.block();
            }
            
            @Override
            public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
                return Shapes.block();
            }
            
            @Override
            public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
                return Shapes.empty();
            }
            
            @Override
            public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
                return 1.0f;
            }
        });

    public static final RegistryObject<Item> TEST_BLOCK_ITEM = ITEMS.register("test_block",
            () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    @SubscribeEvent
    public static void onCreativeTabBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(TEST_BLOCK_ITEM);
        }
    }
}