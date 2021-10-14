package era;

import com.fs.starfarer.api.BaseModPlugin;

public class EraMod extends BaseModPlugin {

    @Override
    public void onNewGame() {
        onGameLoad(true);
    }

    @Override
    public void onGameLoad(boolean newGame) {
        new EraListener();
    }
}
