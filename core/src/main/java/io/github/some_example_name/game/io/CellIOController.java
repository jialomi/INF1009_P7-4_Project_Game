package io.github.some_example_name.game.io;

import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.engine.io.EngineServices;

/**
 * main facade that game's OrganScene talks to
 * manages flow of input and output specifically for tumor cell game
 */
public class CellIOController {

    private CellInputMapper inputMapper;
    private CellDataManager dataManager;
    private CellAudioHandler audioHandler;
    private CellUIRenderer uiRenderer;
    private WebIntegrationService webService;

    public CellIOController(EngineServices services) {
        if (services == null) {
            throw new IllegalArgumentException("EngineServices cannot be null");
        }
        this.inputMapper = new CellInputMapper(services.getInput());
        this.dataManager = new CellDataManager();
        this.audioHandler = new CellAudioHandler(services.getAudio());
        this.uiRenderer = new CellUIRenderer(services.getOutputManager());
        this.webService = new WebIntegrationService();
    }

    /**
     * called every frame by active scene to poll inputs
     */
    public void update(float delta, CellGameState gameState) {
        // process movement
        Vector2 moveDir = inputMapper.processMovementInput();
        if (moveDir.x != 0 || moveDir.y != 0) {
            // in the scene, you would retrieve this and apply it via MovementManager
            // e.g.
            // Vector2 currentDir = ioController.getInputMapper().processMovementInput();
        }
    }

    /**
     * called during render cycle to draw hud
     */
    public void renderUI(CellGameState gameState, float currentSpreadPercentage, int currentExp, int requiredExp) {
        uiRenderer.drawCancerStage(gameState.cancerStage);
        uiRenderer.drawSpreadPercentage(currentSpreadPercentage);
        uiRenderer.drawChemoTimer(gameState.chemoTimer);
        uiRenderer.drawPlayerExpBar(currentExp, requiredExp);
    }

    /**
     * triggers ending scene logic
     */
    public void handleGameOver(boolean isBadEnding) {
        if (isBadEnding) {
            System.out.println("Player reached 100% spread. Triggering web service...");
            webService.openDonationSiteInBrowser();
        } else {
            System.out.println("T-Cells won. Return to main menu.");
        }
    }

    public void dispose() {
        if (uiRenderer != null) {
            uiRenderer.dispose();
        }
    }

    // getters for subsystems so the scene can access specific events
    public CellDataManager getDataManager() {
        return dataManager;
    }

    public CellAudioHandler getAudioHandler() {
        return audioHandler;
    }

    public CellInputMapper getInputMapper() {
        return inputMapper;
    }
}
