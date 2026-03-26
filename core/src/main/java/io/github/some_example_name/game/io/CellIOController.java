package io.github.some_example_name.game.io;

import io.github.some_example_name.engine.io.EngineServices;

/**
 * main facade that game's OrganScene talks to
 * manages flow of input and output specifically for tumor cell game
 */
public class CellIOController {
    private final CellInputMapper inputMapper;
    private final CellDataManager dataManager;
    private final CellAudioHandler audioHandler;
    private final CellUIRenderer uiRenderer;
    private final WebIntegrationService webService;

    public CellIOController(EngineServices services) {
        if (services == null)
            throw new IllegalArgumentException("EngineServices cannot be null");

        this.inputMapper = new CellInputMapper(services.getInput());
        this.dataManager = new CellDataManager();
        this.audioHandler = new CellAudioHandler(services.getAudio());
        this.uiRenderer = new CellUIRenderer(services.getOutputManager());
        this.webService = new WebIntegrationService();
    }

    /**
     * must be called once when organ level finishes loading
     * sets up initial audio and state visuals
     */
    public void onLevelStart(CellGameState gameState) {
        // automatically pull organ name from save/game state and play correct bgm
        if (gameState != null && gameState.currentOrgan != null) {
            audioHandler.setOrganBGM(gameState.currentOrgan);
        } else {
            // fallback just in case
            audioHandler.setOrganBGM("lungs_bgm.mp3");
        }
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

    public WebIntegrationService getWebService() {
        return webService;
    }
}
