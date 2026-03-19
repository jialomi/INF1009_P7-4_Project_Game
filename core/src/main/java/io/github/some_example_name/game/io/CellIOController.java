package io.github.some_example_name.game.io;

import com.badlogic.gdx.math.Vector2;

/**
 * The main facade that the Game's OrganScene talks to.
 * It manages the flow of input and output specifically for the Tumor Cell game.
 */
public class CellIOController {

    private CellInputMapper inputMapper;
    private CellDataManager dataManager;
    private CellAudioHandler audioHandler;
    private CellUIRenderer uiRenderer;
    private WebIntegrationService webService;

    public CellIOController() {
        this.inputMapper = new CellInputMapper();
        this.dataManager = new CellDataManager();
        this.audioHandler = new CellAudioHandler();
        this.uiRenderer = new CellUIRenderer();
        this.webService = new WebIntegrationService();
    }

    /**
     * Called every frame by the active scene to poll inputs
     */
    public void update(float delta, CellGameState gameState) {
        // 1. Process Movement
        Vector2 moveDir = inputMapper.processMovementInput();
        if (moveDir.x != 0 || moveDir.y != 0) {
            // In the scene, you would retrieve this and apply it via MovementManager
            // e.g., Vector2 currentDir =
            // ioController.getInputMapper().processMovementInput();
        }
    }

    /**
     * Called during the render cycle to draw the HUD
     */
    public void renderUI(CellGameState gameState, float currentSpreadPercentage, int currentExp, int requiredExp) {
        uiRenderer.drawCancerStage(gameState.cancerStage);
        uiRenderer.drawSpreadPercentage(currentSpreadPercentage);
        uiRenderer.drawChemoTimer(gameState.chemoTimer);
        uiRenderer.drawPlayerExpBar(currentExp, requiredExp);
    }

    /**
     * Triggers the ending logic
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

    // Getters for subsystems so the Scene can access specific events
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