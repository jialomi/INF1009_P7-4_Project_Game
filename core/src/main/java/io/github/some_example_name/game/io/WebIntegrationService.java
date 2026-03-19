package io.github.some_example_name.game.io;

import com.badlogic.gdx.Gdx;

public class WebIntegrationService {
    private final String donationURL = "https://www.cancer.org/donate.html";

    public void openDonationSiteInBrowser() {
        // Uses LibGDX net capability to open the default OS browser
        Gdx.net.openURI(donationURL);
    }
}