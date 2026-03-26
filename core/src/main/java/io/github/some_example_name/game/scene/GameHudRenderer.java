package io.github.some_example_name.game.scene;

import java.util.Collection;
import java.util.Locale;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.HealthBar;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.util.CancerEvolutionManager;
import io.github.some_example_name.game.util.ChemoManager;

public final class GameHudRenderer {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public GameHudRenderer() {
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.05f);
        this.shapeRenderer = new ShapeRenderer();
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
                         ChemoManager chemoManager, int infectedCells, int totalTargets,
                         float elapsedSeconds, float winTimeSeconds, String progressPrompt) {
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
        font.draw(output.getBatch(), "INFECTED: " + infectedCells + " / " + totalTargets, 20f, top - 44f);
        font.draw(output.getBatch(),
                String.format(Locale.US, "TIME: %.1fs / %.0fs", elapsedSeconds, winTimeSeconds),
                20f,
                top - 66f);

        font.setColor(new Color(0.85f, 0.35f, 1f, 1f));
        font.draw(output.getBatch(), "SPREAD: " + (int) spread + "%", 250f, top);
        font.draw(output.getBatch(), "STAGE: " + cancerManager.getCurrentStage(), 250f, top - 22f);

        font.setColor(Color.ORANGE);
        font.draw(output.getBatch(), chemoManager.isActive()
                        ? "NEXT CHEMO: " + (int) chemoManager.getTimeUntilNextChemo() + "s"
                        : "CHEMO: INACTIVE",
                250f,
                top - 44f);

        font.setColor(Color.YELLOW);
        font.draw(output.getBatch(), progressPrompt, 20f, 36f);
        font.draw(output.getBatch(), "MOVE: WASD / ARROWS   DASH: SHIFT   PAUSE: P", 20f, 62f);
        output.endUi();
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
