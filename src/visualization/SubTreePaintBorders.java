package visualization;

import java.awt.*;

class SubTreePaintBorders {
    private int rootX;
    private int rootY;
    private int leftLeafX;
    private int leftLeafY;
    private int rightLeafX;
    private int rightLeafY;

    SubTreePaintBorders(int rootX, int rootY, int leftLeafX, int leftLeafY,
                        int rightLeafX, int rightLeafY) {
        this.rootX = rootX;
        this.rootY = rootY;
        this.leftLeafX = leftLeafX;
        this.leftLeafY = leftLeafY;
        this.rightLeafX = rightLeafX;
        this.rightLeafY = rightLeafY;
    }

    Point getRootLocation() {
        return new Point(rootX, rootY);
    }

    Point getLeftLeafLocation() {
        return new Point(leftLeafX, leftLeafY);
    }

    Point getRightLeafLocation() {
        return new Point(rightLeafX, rightLeafY);
    }

    int getRootX() {
        return rootX;
    }

    int getRootY() {
        return rootY;
    }

    int getLeftLeafX() {
        return leftLeafX;
    }

    int getLeftLeafY() {
        return leftLeafY;
    }

    int getRightLeafX() {
        return rightLeafX;
    }

    int getRightLeafY() {
        return rightLeafY;
    }
}
