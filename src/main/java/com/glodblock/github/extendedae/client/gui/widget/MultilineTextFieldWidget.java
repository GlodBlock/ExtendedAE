package com.glodblock.github.extendedae.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class MultilineTextFieldWidget extends AbstractWidget {

    private Consumer<String> responder = _ -> {};
    private Pattern filter = null;
    private int maxLength = DEFAULT_MAX_LENGTH;

    public static final int DEFAULT_MAX_LENGTH = Integer.MAX_VALUE;

    private final Font font;
    private final CachedTextField textField;
    private double scrollAmount;
    private boolean dragging;

    public MultilineTextFieldWidget(Font font,
                                    int x, int y,
                                    int w, int h,
                                    Component placeholder) {
        super(x, y, w, h, placeholder);
        this.font = font;
        this.textField = new CachedTextField(font, w - 4);
        this.textField.setCharacterLimit(DEFAULT_MAX_LENGTH);
        this.textField.setCursorListener(this::clampScroll);
        this.textField.setValueListener(_ -> clampScroll());
        this.textField.setCursorListener(this::ensureCursorVisible);
    }

    public void setResponder(@NotNull Consumer<String> responder) {
        this.responder = responder;
    }

    public void setFilter(Pattern filter) {
        this.filter = filter;
        String cur = getValue();
        String s = sanitize(cur);
        if (!s.equals(cur)) {
            this.textField.setValue(s);
            onEdited();
        }
    }

    public void setMaxLength(int len) {
        this.maxLength = Math.max(0, len);
        String cur = getValue();
        if (cur.length() > maxLength) {
            this.textField.setValue(cur.substring(0, maxLength));
            onEdited();
        }
    }

    public String getValue() {
        return textField.value();
    }

    public void setValue(String v) {
        String s = sanitize(v);
        textField.setValue(s);
        onEdited();
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        var key = event.key();
        if (!isFocused() || key == GLFW.GLFW_KEY_TAB || key == GLFW.GLFW_KEY_ESCAPE) {
            return false;
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            textField.insertText("\n");
            clampScroll();
            ensureCursorVisible();
            sanitizeAndNotify();
            return true;
        }

        this.textField.keyPressed(event);
        this.clampScroll();
        this.ensureCursorVisible();
        this.sanitizeAndNotify();
        return true;
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent event) {
        char chr = (char) event.codepoint();
        if (!isFocused()) return false;
        if (chr == '\n' || chr == '\r') return true;
        if (filter != null && !filter.matcher(String.valueOf(chr)).matches()) return true;
        textField.insertText(String.valueOf(chr));
        clampScroll();
        ensureCursorVisible();
        sanitizeAndNotify();
        return true;
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (!isActive() || !isValidClickButton(event.buttonInfo()) || !isMouseOver(event.x(), event.y())) return false;
        this.setFocused(true);

        if (!event.hasShiftDown()) {
            this.textField.setSelecting(false);
        }

        moveCursorToMouse(event.x(), event.y());
        this.textField.setSelecting(true);
        dragging = true;
        return true;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        dragging = false;
        this.textField.setSelecting(false);
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(@NotNull MouseButtonEvent event, double dx, double dy) {
        if (dragging && isFocused()) {
            moveCursorToMouse(event.x(), event.y());
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // NO-OP
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        if (!isMouseOver(mx, my)) return false;
        setScrollAmount(scrollAmount - vertical * font.lineHeight);
        return true;
    }

    public double getMaxScroll() {
        int textH = textField.lineCount() * font.lineHeight;
        return Math.max(textH - (height - 4), 0);
    }

    public void setScrollAmount(double a) {
        this.scrollAmount = Mth.clamp(a, 0, getMaxScroll());
    }

    private void clampScroll() {
        setScrollAmount(this.scrollAmount);
    }

    private void ensureCursorVisible() {
        int viewH     = height - 4;
        int caretLine = textField.lineAtCursor();
        int caretY    = caretLine * font.lineHeight;

        double top    = scrollAmount;
        double bottom = scrollAmount + viewH - font.lineHeight;

        if (caretY < top) {
            setScrollAmount(caretY);
        } else if (caretY > bottom) {
            setScrollAmount(caretY - (viewH - font.lineHeight));
        }
    }


    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor g, int mX, int mY, float partial) {
        int bg = 0xFF202020, border = isFocused() ? 0xFFFFFFFF : 0xFF808080;
        g.fill(RenderPipelines.GUI, getX(), getY(), getX() + width, getY() + height, bg);
        g.fill(RenderPipelines.GUI, getX(), getY(), getX() + width, getY() + 1, border);
        g.fill(RenderPipelines.GUI, getX(), getY() + height - 1, getX() + width, getY() + height, border);
        g.fill(RenderPipelines.GUI, getX(), getY(), getX() + 1, getY() + height, border);
        g.fill(RenderPipelines.GUI, getX() + width - 1, getY(), getX() + width, getY() + height, border);

        int clipL = getX() + 2, clipT = getY() + 2, clipR = getX() + width - 2, clipB = getY() + height - 2;
        g.enableScissor(clipL, clipT, clipR, clipB);

        int firstLine = (int) (scrollAmount / font.lineHeight);
        int y = clipT - (int) scrollAmount + firstLine * font.lineHeight;

        int selectionBegin = textField.hasSelection() ? textField.selection().begin() : -1;
        int selectionEnd   = textField.hasSelection() ? textField.selection().end()   : -1;
        int selectionColor = 0x80007FFF;

        if (isFocused() && blink()) {
            int curLine = textField.lineAtCursor();
            Line ln = textField.line(curLine);
            int cx = clipL + font.width(textField.value().substring(ln.begin(), textField.cursor()));
            int cy = clipT + curLine * font.lineHeight - (int) scrollAmount;
            if (cy >= clipT && cy < clipB) g.fill(RenderPipelines.GUI, cx, cy, cx + 1, cy + font.lineHeight, 0xFFFFFFFF);
        }

        for (int idx = firstLine; idx < textField.lineCount() && y <= clipB; idx++) {
            Line ln = textField.line(idx);
            String str = textField.value().substring(ln.begin(), ln.end());

            g.text(font, str, clipL, y, 0xFFE0E0E0);
            if (textField.hasSelection()) {
                int lineStartChar = ln.begin();
                int lineEndChar   = ln.end();

                if (!(selectionEnd <= lineStartChar || selectionBegin >= lineEndChar)) {
                    int selStartInLine = Math.max(0, selectionBegin - lineStartChar);
                    int selEndInLine   = Math.min(str.length(), selectionEnd - lineStartChar);

                    if (selStartInLine < selEndInLine) {
                        String preSel = str.substring(0, selStartInLine);
                        String selectionText = str.substring(selStartInLine, selEndInLine);

                        int selX = clipL + font.width(preSel);
                        int selW = font.width(selectionText);

                        g.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, selX, y, selX + selW, y + font.lineHeight, selectionColor);
                    }
                }
            }
            y += font.lineHeight;
        }

        g.disableScissor();

        if (textField.value().isEmpty() && !isFocused()) {
            g.text(font, getMessage(), clipL, clipT, 0xFF808080);
        }
    }

    private void sanitizeAndNotify() {
        String cur = getValue();
        String s = sanitize(cur);
        if (!s.equals(cur)) textField.setValue(s);
        onEdited();
    }

    private void onEdited() {
        try { responder.accept(getValue()); } catch (Throwable ignored) {}
    }

    private String sanitize(String in) {
        if (in == null) return "";
        StringBuilder b = new StringBuilder(in.length());
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\r') continue;
            if (c == '\n') { b.append('\n'); continue; }
            if (filter == null || filter.matcher(String.valueOf(c)).matches()) b.append(c);
        }
        String s = b.toString();
        if (s.length() > maxLength) s = s.substring(0, maxLength);
        return s;
    }

    private void moveCursorToMouse(double mx, double my) {
        double relX = mx - (getX() + 2);
        double relY = my - (getY() + 2) + scrollAmount;
        textField.seekCursorToPoint(relX, relY);
    }
    private boolean blink() { return (Util.getMillis() / 500) % 2 == 0; }

    private record Line(int begin, int end) {}

    private static final class CachedTextField extends MultilineTextField {
        private List<Line> cache = new ArrayList<>();
        record Selection(int begin, int end) {}

        CachedTextField(Font font, int w) {
            super(font, w);
            rebuild();
        }

        int  lineCount()    { return cache.size(); }
        Line line(int idx)  { return cache.get(Mth.clamp(idx, 0, cache.size()-1)); }
        int  lineAtCursor() { return super.getLineAtCursor(); }

        Selection selection() {
            var sv = super.getSelected();
            return new Selection(sv.beginIndex(), sv.endIndex());
        }

        @Override public void setValue(@NotNull String v)   { super.setValue(v);   rebuild(); }
        @Override public void insertText(@NotNull String t) { super.insertText(t); rebuild(); }

        private void rebuild() {
            if (cache == null) cache = new ArrayList<>();
            cache.clear();
            super.iterateLines().forEach(sv ->
                    cache.add(new Line(sv.beginIndex(), sv.endIndex()))
            );
        }
    }
}
