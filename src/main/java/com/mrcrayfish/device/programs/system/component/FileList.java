package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Stack;

/**
 * Created by Casey on 10-Jun-17.
 */
public class FileList extends ItemList<File>
{
    private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");

    private static final Color ITEM_BACKGROUND = new Color(215, 217, 224);
    private static final Color ITEM_SELECTED = new Color(221, 208, 208);

    private static final ListItemRenderer<File> ITEM_RENDERER = new ListItemRenderer<File>(18) {
        @Override
        public void render(File file, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
        {
            gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());

            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSETS);
            if(file.isFolder())
            {
                RenderUtil.drawRectWithTexture(x + 2, y + 2, 0, 0, 16, 14, 16, 14);
            }
            else
            {
                Application.Icon icon = ApplicationManager.getApp(file.getOpeningApp()).getIcon();
                Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getResource());
                RenderUtil.drawRectWithTexture(x + 2, y + 2, icon.getU(), icon.getV(), 14, 14, 14, 14);
            }
            gui.drawString(Minecraft.getMinecraft().fontRendererObj, file.getName(), x + 22, y + 5, Color.WHITE.getRGB());
        }
    };

    private final Folder parent;
    private Folder current;

    private Stack<Folder> predecessor = new Stack<>();

    /**
     * Default constructor for the item list. Should be noted that the
     * height is determined by how many visible items there are.
     *
     * @param left         how many pixels from the left
     * @param top          how many pixels from the top
     * @param width        width of the list
     * @param visibleItems how many items are visible
     */
    public FileList(int left, int top, int width, int visibleItems, Folder parent)
    {
        super(left, top, width, visibleItems);
        this.parent = parent;
        this.current = parent;
        this.openFolder(parent, true);
    }

    @Override
    public void init(Layout layout)
    {
        super.init(layout);
        this.setListItemRenderer(ITEM_RENDERER);
        this.sortBy(File.SORT_BY_NAME);
    }

    public void openFolder(Folder folder, boolean push)
    {
        if(push) this.predecessor.push(current);
        this.current = folder;
        this.removeAll();
        this.setItems(folder.getFiles());
    }

    public void goToPreviousFolder()
    {
        if(predecessor.size() > 1)
        {
            Folder folder = predecessor.pop();
            openFolder(folder, false);
        }
    }

    public void addFile(File file)
    {
        super.addItem(file);
        current.add(file);
    }

    public void removeFile(int index)
    {
        File file = super.removeItem(index);
        if(file != null)
        {
            current.delete(file.getName());
        }
    }
}