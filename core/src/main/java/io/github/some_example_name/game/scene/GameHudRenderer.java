package io.github.some_example_name.game.scene;

import java.util.Collection;
import java.util.Locale;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.HealthBar;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.util.CancerEvolutionManager;
import io.github.some_example_name.game.util.ChemoManager;
import io.github.some_example_name.engine.io.AssetService;
import io.github.some_example_name.game.util.UIUtils;

public final class GameHudRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    // key gui
    private final Texture wTexture, aTexture, sTexture, dTexture;
    private final Texture upTexture, leftTexture, downTexture, rightTexture;
    private final Texture shiftTexture, pTexture, f3Texture;

    public GameHudRenderer(AssetService assets) {
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.05f);
        this.shapeRenderer = new ShapeRenderer();

        // load textures from asset manager
        this.wTexture = assets.getTexture("key-gui/movement/w.png");
        this.aTexture = assets.getTexture("key-gui/movement/a.png");
        this.sTexture = assets.getTexture("key-gui/movement/s.png");
        this.dTexture = assets.getTexture("key-gui/movement/d.png");

        this.upTexture = assets.getTexture("key-gui/movement/arrow_up.png");
        this.leftTexture = assets.getTexture("key-gui/movement/arrow_left.png");
        this.downTexture = assets.getTexture("key-gui/movement/arrow_down.png");
        this.rightTexture = assets.getTexture("key-gui/movement/arrow_right.png");

        this.shiftTexture = assets.getTexture("key-gui/movement/shift.png");
        this.pTexture = assets.getTexture("key-gui/settingKeys/p.png");
        this.f3Texture = assets.getTexture("key-gui/settingKeys/f3.png");
    }

    public void renderWorldHealthBars(OutputManager output, Collection<Entity> entities, float interpolationAlpha) {
        shapeRenderer.setProjectionMatrix(output.getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : entities) {
            if (!entity.isActive()) {
                continue;
            }
            HealthBar healthBar = null;
            if (entity instanceof CancerCell) {
                healthBar = ((CancerCell) entity).getHealthBar();
            } else if (entity instanceof NormalCell) {
                healthBar = ((NormalCell) entity).getHealthBar();
            } else if (entity instanceof TCell) {
                healthBar = ((TCell) entity).getHealthBar();
            }
            if (healthBar != null) {
                healthBar.render(shapeRenderer, interpolationAlpha);
            }
        }
        shapeRenderer.end();
    }

    public void renderUi(OutputManager output, CancerCell player, CancerEvolutionManager cancerManager,
            ChemoManager chemoManager, int infectedCells,
            float elapsedSeconds, String progressPrompt) {
        float uiWidth = output.getUiWidth();
        float uiHeight = output.getUiHeight();

        com.badlogic.gdx.Gdx.gl.glEnable(GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(output.getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 0.85f);
        shapeRenderer.rect(10f, uiHeight - 118f, 520f, 108f);

        shapeRenderer.setColor(0.2f, 0.2f, 0.24f, 0.92f);
        shapeRenderer.rect(10f, 12f, uiWidth - 20f, 28f);

        float spread = cancerManager.getCurrentSpreadPercent();
        float spreadWidth = (uiWidth - 20f) * (spread / 100f);
        shapeRenderer.setColor(0.72f, 0.15f, 0.32f, 1f);
        shapeRenderer.rect(10f, 12f, spreadWidth, 28f);

        shapeRenderer.end();
        com.badlogic.gdx.Gdx.gl.glDisable(GL20.GL_BLEND);

        output.beginUi();
        float top = uiHeight - 25f;
        font.setColor(Color.WHITE);
        font.draw(output.getBatch(), "HP: " + (int) player.getHp() + " / " + (int) player.getMaxHp(), 20f, top);
        font.draw(output.getBatch(), "LEVEL: " + player.getLevel(), 20f, top - 22f);
        font.draw(output.getBatch(), "CELLS INFECTED: " + infectedCells, 20f, top - 44f);
        font.draw(output.getBatch(),
                String.format(Locale.US, "TIME ELAPSED: %.1fs", elapsedSeconds),
                20f,
                top - 66f);

        font.setColor(new Color(0.85f, 0.35f, 1f, 1f));
        font.draw(output.getBatch(), "SPREAD: " + Math.round(spread) + "%", 250f, top);
        font.draw(output.getBatch(),
                cancerManager.isTerminalStage() ? "STAGE: TERMINAL" : "STAGE: " + cancerManager.getCurrentStage(),
                250f,
                top - 22f);

        font.setColor(Color.ORANGE);
        font.draw(output.getBatch(), chemoManager.isActive()
                ? "NEXT CHEMO: " + (int) chemoManager.getTimeUntilNextChemo(
                        cancerManager.getCurrentStage(),
                        cancerManager.getCurrentSpreadPercent()) + "s"
                : "CHEMO: INACTIVE",
                250f,
                top - 44f);
        font.setColor(Color.SALMON);
        font.draw(output.getBatch(), cancerManager.getImmuneStrengthDescription(), 250f, top - 66f);

        font.setColor(Color.YELLOW);
        // font.draw(output.getBatch(), progressPrompt, 20f, 36f);
        // font.draw(output.getBatch(), "MOVE: WASD / ARROWS DASH: SHIFT PAUSE: P", 20f,
        // 62f);

        // progress prompt
        float progressY = 56f;
        font.draw(output.getBatch(), progressPrompt, 20f, progressY);

        // horizontal movement row
        float rowY = 64f;
        float clusterIconSize = 24f;
        float singleIconSize = 28f;
        float gap = 4f;
        float clusterWidth = (clusterIconSize * 3f) + (gap * 2f);
        float spacing = 12f;

        // calculate exact text widths to right-align entire block
        GlyphLayout slashLayout = new GlyphLayout(font, "/");
        GlyphLayout moveLayout = new GlyphLayout(font, "[WASD/ARROWS] Move");
        GlyphLayout dashLayout = new GlyphLayout(font, "[SHIFT] Dash");

        float shiftRatio = (float) shiftTexture.getWidth() / shiftTexture.getHeight();
        float shiftWidth = singleIconSize * shiftRatio;

        // total width of movement row
        float movementRowWidth = clusterWidth + spacing + slashLayout.width + spacing + clusterWidth + spacing
                + moveLayout.width + (spacing * 3f) + shiftWidth + 8f + dashLayout.width;

        // anchor block to right side of screen (with 20px padding)
        float blockStartX = uiWidth - movementRowWidth - 20f;

        float currentX = blockStartX;
        float textY = rowY + clusterIconSize + 2f;

        // draw wasd
        UIUtils.drawKeyCluster(output, wTexture, aTexture, sTexture, dTexture, currentX, rowY, clusterIconSize);
        currentX += clusterWidth + spacing;

        // draw "/"
        font.draw(output.getBatch(), slashLayout, currentX, textY);
        currentX += slashLayout.width + spacing;

        // draw arrows
        UIUtils.drawKeyCluster(output, upTexture, leftTexture, downTexture, rightTexture, currentX, rowY,
                clusterIconSize);
        currentX += clusterWidth + spacing;

        // draw "Move"
        font.draw(output.getBatch(), moveLayout, currentX, textY);
        currentX += moveLayout.width + (spacing * 3f);

        // draw "Dash"
        output.getBatch().draw(shiftTexture, currentX, rowY + 6f, shiftWidth, singleIconSize);
        currentX += shiftWidth + 8f;
        font.draw(output.getBatch(), dashLayout, currentX, rowY + singleIconSize + 2f);

        // f3 and pause row
        float f3RowY = 24f;
        // align left edge of this row with movement row above it to create a neat block
        float bottomX = blockStartX;

        // draw F3
        output.getBatch().draw(f3Texture, bottomX, f3RowY, singleIconSize, singleIconSize);
        bottomX += singleIconSize + 8f;

        GlyphLayout f3Layout = new GlyphLayout(font, "[F3] Toggle hitboxes");
        font.draw(output.getBatch(), f3Layout, bottomX, f3RowY + singleIconSize - 4f);
        bottomX += f3Layout.width + 40f;

        // draw "Pause"
        output.getBatch().draw(pTexture, bottomX, f3RowY, singleIconSize, singleIconSize);
        bottomX += singleIconSize + 8f;
        font.draw(output.getBatch(), "[P] Pause", bottomX, f3RowY + singleIconSize - 4f);

        output.endUi();
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
