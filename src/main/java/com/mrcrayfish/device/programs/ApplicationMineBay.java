package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.Inventory;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ApplicationMineBay extends Application
{
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	
	private String[] categories = { "Building", "Combat", "Tools", "Food", "Materials", "Redstone", "Alchemy", "Rare", "Misc" };
	
	private Layout layoutAdd;
	private Label labelInventory;
	private Inventory inventory;
	private Label labelSetPriceAndAmount;
	
	public ApplicationMineBay()
	{
		super(Reference.MOD_ID + "MineBay", "MineBay");
	}
	
	@Override
	public void init(int x, int y)
	{
		Layout home = new Layout(300, 145);
		home.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + 25, Color.GRAY.getRGB());
				gui.drawRect(x, y + 24, x + width, y + 25, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 25, x + 95, y + height, Color.LIGHT_GRAY.getRGB());
				gui.drawRect(x + 94, y + 25, x + 95, y + height, Color.GRAY.getRGB());
			}
		});
		
		Button btnAddItem = new Button("Add Item", 50, 5, 60, 15);
		btnAddItem.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutAdd);
			}
		});
		home.addComponent(btnAddItem);
		
		Button btnViewItem = new Button("Your Auctions", 115, 5, 80, 15);
		home.addComponent(btnViewItem);
		
		Label labelBalance = new Label("Balance", 295, 3);
		labelBalance.setAlignment(Label.ALIGN_RIGHT);
		home.addComponent(labelBalance);
		
		final Label labelMoney = new Label("$0", 295, 13);
		labelMoney.setAlignment(Label.ALIGN_RIGHT);
		labelMoney.setScale(1);
		labelMoney.setShadow(false);
		home.addComponent(labelMoney);
		
		Label labelCategories = new Label("Categories", 5, 29);
		labelCategories.setShadow(false);
		home.addComponent(labelCategories);
		
		ItemList<String> categories = new ItemList<String>(5, 40, 70, 7);
		for(String category : this.categories) {
			categories.addItem(category);
		}
		home.addComponent(categories);
		
		Label labelItems = new Label("Items", 100, 29);
		labelItems.setShadow(false);
		home.addComponent(labelItems);
		
		ItemList<ItemStack> items = new ItemList<ItemStack>(100, 40, 180, 4);
		items.setListItemRenderer(new ListItemRenderer<ItemStack>(20)
		{
			@Override
			public void render(ItemStack e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				if(selected) 
				{
					gui.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
				} 
				else 
				{
					gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());
				}
				
				RenderUtil.renderItem(x + 2, y + 2, e, true);
				gui.drawString(mc.fontRendererObj, e.getDisplayName(), x + 24, y + 6, Color.WHITE.getRGB());
			}
		});
		home.addComponent(items);
		
		items.addItem(new ItemStack(Items.painting));
		
		ItemStack sugar = new ItemStack(Items.sugar);
		sugar.setStackDisplayName(EnumChatFormatting.AQUA + "The Good Stuff");
		items.addItem(sugar);
		
		items.addItem(new ItemStack(Items.saddle));
		
		Button btnBuy = new Button("Buy", 100, 127, 50, 15);
		btnBuy.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				labelMoney.setText("HELLLLLLO");
			}
		});
		home.addComponent(btnBuy);
		
		layoutAdd = new Layout(172, 112);
		layoutAdd.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + 39, Color.GRAY.getRGB());
				gui.drawRect(x, y + 39, x + width, y + 40, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 40, x + width, y + height, Color.LIGHT_GRAY.getRGB());
				
				mc.fontRendererObj.drawString("Item", x + 5, y + 5, Color.WHITE.getRGB(), true);
				mc.fontRendererObj.drawString("Inventory", x + 5, y + 43, Color.WHITE.getRGB(), false);
				
				gui.drawRect(x + 5, y + 16, x + 25, y + 36, Color.DARK_GRAY.getRGB());
				gui.drawRect(x + 6, y + 17, x + 24, y + 35, Color.LIGHT_GRAY.getRGB());
				
				if(inventory.getSelectedSlotIndex() != -1)
				{
					ItemStack stack = mc.thePlayer.inventory.getStackInSlot(inventory.getSelectedSlotIndex());
					if(stack != null)
					{
						GlStateManager.pushMatrix();
						{
							RenderUtil.renderItem(x + 4, y + 4, stack, false);
						}
						GlStateManager.popMatrix();
					}
				}
			}
		});
		
		inventory = new Inventory(5, 53);
		layoutAdd.addComponent(inventory);
		
		setCurrentLayout(home);
		
		BankUtil.getBalance(new Callback()
		{
			@Override
			public void execute(NBTTagCompound nbt, boolean success)
			{
				if(success) 
				{
					labelMoney.setText("$" + nbt.getInteger("balance"));
				}
			}
		});
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{

	}
}