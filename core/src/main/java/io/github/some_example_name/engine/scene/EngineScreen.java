package io.github.some_example_name.engine.scene;

public interface EngineScreen {
    void initialise();
    void update(float delta);
    void render(float delta);
    void resize(int width, int height);
    void dispose();
}
