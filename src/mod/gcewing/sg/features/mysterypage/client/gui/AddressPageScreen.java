package gcewing.sg.features.mysterypage.client.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import gcewing.sg.SGCraft;
import gcewing.sg.client.gui.SGScreen;
import gcewing.sg.network.SGChannel;
import gcewing.sg.util.SGAddressing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class AddressPageScreen extends SGScreen {

    // Background texture (128x128). Visible paper occupies:
    //   x: [6, 83]  — paper centre at 44.5
    //   y: [6, 122] — paper centre at 64
    private static final int TEX_SIZE           = 128;
    private static final int TEX_CONTENT_LEFT   = 6;
    private static final int TEX_CONTENT_RIGHT  = 83;
    private static final int TEX_CONTENT_TOP    = 6;
    private static final int TEX_CONTENT_BOTTOM = 122;

    // Gray rune sprite sheet (symbols48gray.png — 512x256, 10 glyphs/row, 48x48 px each)
    private static final ResourceLocation SYMBOL_TEX =
            new ResourceLocation("sgcraft", "textures/gui/symbols48gray.png");
    private static final int SRC_TEX_W    = 512;
    private static final int SRC_TEX_H    = 256;
    private static final int SRC_CELL     = 48;
    private static final int SYMS_PER_ROW = 10;

    private final String address;
    private final String formattedAddress;
    private final EnumHand hand;

    // Title state — pendingTitle persists across initGui re-calls (screen resize)
    private String pendingTitle;
    private boolean editingTitle;

    // Layout values computed in initGui()
    private int pageX, pageY, renderSize;
    private float ts;
    private int contentCX, innerTopY, innerBottomY, titleAreaH;
    private int editBtnX, editBtnY, editBtnW;
    private int titleFieldX, titleFieldY, titleFieldW;

    private GuiTextField titleField;

    public AddressPageScreen(String address, String title, EnumHand hand) {
        this.address = address;
        this.formattedAddress = SGAddressing.formatAddress(address, "-", "-");
        this.hand = hand;
        this.pendingTitle = title == null ? "" : title;
        if (SGCraft.saveAddressToClipboard) {
            setClipboardString(this.formattedAddress);
        }
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void initGui() {
        super.initGui();
        renderSize = Math.max(192, Math.min(256, this.height / 2));
        ts = renderSize / (float) TEX_SIZE;

        float paperCenterTx = (TEX_CONTENT_LEFT + TEX_CONTENT_RIGHT) / 2.0f; // 44.5
        pageX = (int)(this.width  / 2 - paperCenterTx * ts);
        pageY = this.height / 2 - renderSize / 2;
        contentCX = this.width / 2;

        int innerPad = Math.max(4, (int)(ts * 2));
        innerTopY    = pageY + (int)(TEX_CONTENT_TOP    * ts) + innerPad;
        innerBottomY = pageY + (int)(TEX_CONTENT_BOTTOM * ts) - innerPad;

        // Title zone: reserve one font line at the top of the content area
        titleAreaH = this.fontRenderer.FONT_HEIGHT + 4;

        // Edit button — pencil icon flush with the right edge of the paper
        int contentRightX = pageX + (int)(TEX_CONTENT_RIGHT * ts);
        editBtnW = this.fontRenderer.getStringWidth("\u270e") + 6;
        editBtnX = contentRightX - editBtnW - 2;
        editBtnY = innerTopY + 2;

        // Title text field: from left paper edge to left of edit button
        titleFieldX = pageX + (int)(TEX_CONTENT_LEFT * ts) + 2;
        titleFieldY = innerTopY + 2;
        titleFieldW = editBtnX - titleFieldX - 4;

        titleField = new GuiTextField(0, this.fontRenderer,
                titleFieldX, titleFieldY, titleFieldW, this.fontRenderer.FONT_HEIGHT);
        titleField.setMaxStringLength(32);
        titleField.setEnableBackgroundDrawing(false);
        titleField.setFocused(false);
        titleField.setText(pendingTitle);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (titleField != null) titleField.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        // Commit any in-progress edit, then sync title to server
        if (editingTitle) {
            pendingTitle = titleField.getText();
            editingTitle = false;
        }
        SGChannel.sendSetAddressPageTitle(hand, pendingTitle);
        if (SGCraft.saveAddressToClipboard) {
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Code copied to your clipboard.", false);
        }
        super.onGuiClosed();
    }

    /** Draw one row of address glyphs centred at (cx, y) using the gray symbol texture. */
    private void drawSymbolRow(String row, int cx, int y, int cellSize) {
        int x0 = cx - row.length() * cellSize / 2;
        bindTexture(SYMBOL_TEX, SRC_TEX_W, SRC_TEX_H);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        for (int i = 0; i < row.length(); i++) {
            int s   = SGAddressing.charToSymbol(row.charAt(i));
            int col = s % SYMS_PER_ROW;
            int r   = s / SYMS_PER_ROW;
            drawTexturedRect(x0 + i * cellSize, y, cellSize, cellSize,
                    col * SRC_CELL, r * SRC_CELL, SRC_CELL, SRC_CELL);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Draw notebook background
        bindTexture(
            new ResourceLocation("sgcraft", "textures/gui/notebook_background.png"),
            TEX_SIZE, TEX_SIZE
        );
        drawTexturedRect(pageX, pageY, renderSize, renderSize, 0, 0, TEX_SIZE, TEX_SIZE);

        // ---- Title zone ----
        if (editingTitle) {
            // Subtle highlight behind the active text field
            drawRect(titleFieldX - 1, titleFieldY - 1,
                     titleFieldX + titleFieldW + 1, titleFieldY + this.fontRenderer.FONT_HEIGHT + 1,
                     0x44FFFFFF);
            titleField.drawTextBox();
        } else {
            String displayTitle = pendingTitle.isEmpty() ? "<Unnamed>" : pendingTitle;
            int titleColor = pendingTitle.isEmpty() ? 0x555555 : 0x777777;
            int titleW = this.fontRenderer.getStringWidth(displayTitle);
            this.fontRenderer.drawString(displayTitle, contentCX - titleW / 2, titleFieldY, titleColor, false);
        }

        // Edit button ("✎") — highlighted while editing or hovered
        boolean hovered = mouseX >= editBtnX && mouseX < editBtnX + editBtnW
                       && mouseY >= editBtnY && mouseY < editBtnY + 10;
        int btnColor = editingTitle ? 0xFFFFAA : (hovered ? 0xEEEEEE : 0x888888);
        this.fontRenderer.drawString("\u270e", editBtnX + 2, editBtnY + 1, btnColor, false);

        // Reset GL colour state that may have been dirtied by drawRect / GuiTextField
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        // ---- Glyph rows (shifted down by titleAreaH to give title space) ----
        String row1 = address.length() >= 4 ? address.substring(0, 4) : address;
        String row2 = address.length() >= 7 ? address.substring(4, 7)
                    : (address.length() > 4 ? address.substring(4) : "");
        String row3 = address.length() == 9 ? address.substring(7) : "";
        int numRows = row3.isEmpty() ? (row2.isEmpty() ? 1 : 2) : 3;

        int contentW  = TEX_CONTENT_RIGHT - TEX_CONTENT_LEFT; // 77 tx
        int cellSize  = Math.min(SRC_CELL, (int)(contentW * ts * 0.88f / 4));
        int rowSpacing = cellSize + 6;

        int textAreaH     = (int)(ts * 10) + 8;
        int symbolZoneTop = innerTopY + titleAreaH + 2;
        int symbolZoneH   = (innerBottomY - symbolZoneTop) - textAreaH - 4;
        int glyphSpan     = (numRows - 1) * rowSpacing + cellSize;
        int firstRowY     = symbolZoneTop + Math.max(0, (symbolZoneH - glyphSpan) / 2);

        if (!row1.isEmpty()) drawSymbolRow(row1, contentCX, firstRowY,                  cellSize);
        if (!row2.isEmpty()) drawSymbolRow(row2, contentCX, firstRowY + rowSpacing,     cellSize);
        if (!row3.isEmpty()) drawSymbolRow(row3, contentCX, firstRowY + rowSpacing * 2, cellSize);

        // ---- Address text ----
        float textScale = Math.max(1.0f, ts * 0.75f);
        int textY = innerBottomY - textAreaH + 2;
        int textW = this.fontRenderer.getStringWidth(formattedAddress);
        GlStateManager.pushMatrix();
        GlStateManager.translate(contentCX, textY, 0);
        GlStateManager.scale(textScale, textScale, 1.0f);
        this.fontRenderer.drawString(formattedAddress, -textW / 2, 0, 0x777777, false);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        if (button == 0) {
            boolean clickedEditBtn = x >= editBtnX && x < editBtnX + editBtnW
                                  && y >= editBtnY && y < editBtnY + 10;
            if (clickedEditBtn) {
                if (editingTitle) {
                    pendingTitle = titleField.getText();
                    editingTitle = false;
                    titleField.setFocused(false);
                } else {
                    editingTitle = true;
                    titleField.setFocused(true);
                    titleField.setCursorPositionEnd();
                }
                return;
            }
            // Click anywhere else while editing confirms the edit
            if (editingTitle) {
                pendingTitle = titleField.getText();
                editingTitle = false;
                titleField.setFocused(false);
            }
        }
        if (editingTitle && titleField != null) {
            titleField.mouseClicked(x, y, button);
        }
        super.mouseClicked(x, y, button);
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        if (editingTitle) {
            if (key == org.lwjgl.input.Keyboard.KEY_RETURN
                    || key == org.lwjgl.input.Keyboard.KEY_NUMPADENTER) {
                pendingTitle = titleField.getText();
                editingTitle = false;
                titleField.setFocused(false);
                return;
            }
            if (key == org.lwjgl.input.Keyboard.KEY_ESCAPE) {
                // Cancel: restore previous committed title
                titleField.setText(pendingTitle);
                editingTitle = false;
                titleField.setFocused(false);
                return;
            }
            titleField.textboxKeyTyped(c, key);
            return;
        }
        super.keyTyped(c, key);
    }

    @Override protected void drawBackgroundLayer() {}
}
