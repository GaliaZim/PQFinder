package visualization;

import visualization.drawables.ConnectedDrawable;
import visualization.drawables.LeafDrawable;

import java.awt.*;

public class SubTreeBoundaries {
    private int endLeafX;
    private ConnectedDrawable root;
    private Rectangle border;

    public SubTreeBoundaries(ConnectedDrawable root, int x, int y, int width, int height) {
        this.border = new Rectangle(x, y, width, height);
        this.endLeafX = x + width;
        this.root = root;
    }

    public SubTreeBoundaries(LeafDrawable leafDrawable) {
        this.border = leafDrawable.getBorders();
        this.endLeafX = border.x + border.width;
        this.root = leafDrawable;
    }

    public int getEndLeafX() {
        return endLeafX;
    }

    public ConnectedDrawable getRoot() {
        return root;
    }

    public Rectangle getBorder() {
        return border;
    }
}