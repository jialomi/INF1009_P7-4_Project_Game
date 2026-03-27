package io.github.some_example_name.game.io;

import io.github.some_example_name.engine.io.EngineServices;

public class CellIOController {
    private final CellInputMapper inputMapper;
    private final CellDataManager dataManager;
    private final CellAudioHandler audioHandler;
    private final WebIntegrationService webService;

    public CellIOController(EngineServices services) {
        if (services == null)
            throw new IllegalArgumentException("EngineServices cannot be null");

        this.inputMapper = new CellInputMapper(services.getInput());
        this.dataManager = new CellDataManager();
        this.audioHandler = new CellAudioHandler(services.getAudio());
        this.webService = new WebIntegrationService();
    }

    public void dispose() {
    }

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
