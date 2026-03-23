package io.github.some_example_name.game.io;

import com.badlogic.gdx.Gdx;

public class WebIntegrationService {
    // url to change once decided on a specific cancer website
    // this is currently an american cancer society
    // - so possibly need to change to an sg one?
    // or we could give a list of singaporean (or global if we wanted to)
    // cancer websites that the user can choose to visit instead
    private final String donationURL = "https://www.singaporecancersociety.org.sg/get-involved/donate.html";

    public void openDonationSiteInBrowser() {
        // uses LibGDX net capability to open default os browser
        Gdx.net.openURI(donationURL);
    }
}