package sbc.gui;

/**
 * class GridViewPort represents the visible part of the grid.
 * Each point on the grid is displayed as a tile on the canvas.
 * There are 3 coordinate systems in use:
 *  * (gridX, gridY): "grid coordinate", Actual grid points
 *      * Positive direction of x-axis: to the right
 *      * Positive direction of y-axis: upwards
 *  * (tileX, tileY): "tile coordinate", Visible tiles with
 *      the top-left visible tile have coordinates (0,0)
 *      * Positive direction of x-axis: to the right
 *      * Positive direction of y-axis: downwards
 *  * (screenX, screenY): "pixel coordinate" the coordinate
 *      of the top left point of the tile on the canvas.
 *      * Positive direction of x-axis: to the right
 *      * Positive direction of y-axis: downwards
 */
public class GridViewPort {
    // minimum size of a tile in pixels
    public static final int MIN_TILE_SIZE = 4;
    public static final int DEFAULT_TILE_SIZE = 16;

    // size of the display canvas in pixels
    private int width;
    private int height;

    // size of a tile in pixels
    private int tileSize;

    // x and y grid coordinates of tile (0,0)
    private int topLeftX;
    private int topLeftY;

    // x and y offset of the (0,0) tile from the
    // top left corner of the screen in fraction
    // of the tile size normalized to be in (-1, 0]
    private double offsetX;
    private double offsetY;


    /**
     * @param width width of the canvas in pixels
     * @param height height of the canvas in pixels
     */
    public GridViewPort(int width, int height) {
        this.width = width;
        this.height = height;
        this.topLeftX = 0;
        this.topLeftY = 0;
        setTileSize(DEFAULT_TILE_SIZE);
        addOffset(width / 2 - tileSize / 2, height / 2 - tileSize / 2);
        normalizeOffset();
    }


    // ********** Getters **********

    /**
     * Returns the width of the canvas in pixels
     * @return the width of the canvas in pixels
     */
    public int width() { return width; }

    /**
     * Returns the height of the canvas in pixels
     * @return the height of the canvas in pixels
     */
    public int height() { return height; }


    /**
     * Returns the size of tiles in pixels
     * @return the size of tiles in pixels
     */
    public int tileSize() { return tileSize; }


    /**
     * Returns the x-gird-coordinate of tile (0,0)
     * @return the x-gird-coordinate of tile (0,0)
     */
    public int topLeftX() { return topLeftX; }

    /**
     * Returns the x-gird-coordinate of tile (0,0)
     * @return the x-gird-coordinate of tile (0,0)
     */
    public int topLeftY() { return topLeftY; }


    /**
     * Returns the x-offset in pixels
     * @return the x-offset in pixels
     */
    public int offsetX() { return (int)(offsetX * tileSize); }

    /**
     * Returns the y-offset in pixels
     * @return the y-offset in pixels
     */
    public int offsetY() { return (int)(offsetY * tileSize); }


    /**
     * Returns the number of tiles visible along the width of the canvas
     * @return the number of tiles visible along the width of the canvas
     */
    public int tileCountX() { return width / tileSize + 2; }

    /**
     * Returns the number of tiles visible along the height of the canvas
     * @return the number of tiles visible along the height of the canvas
     */
    public int tileCountY() { return height / tileSize + 2; }


    /**
     * Returns true if any tile with given x-grid-coordinate is visible through the view port
     * @param gridX x-gird-coordinate
     * @return true if any tile with given x-grid-coordinate is visible through the view port
     */
    public boolean isVisibleGridX(int gridX) {
        int tileX = gridToTileX(gridX);
        return tileX >= 0 && tileX < tileCountX();
    }

    /**
     * Returns true if any tile with given y-grid-coordinate is visible through the view port
     * @param gridY y-gird-coordinate
     * @return true if any tile with given y-grid-coordinate is visible through the view port
     */
    public boolean isVisibleGridY(int gridY) {
        int tileY = gridToTileY(gridY);
        return tileY >= 0 && tileY < tileCountY();
    }

    /**
     * Returns true if tile with given grid coordinated is visible through the vie port
     * @param gridX x-grid-coordinate
     * @param gridY y-grid coordinate
     * @return true if tile with given grid coordinated is visible through the vie port
     */
    public boolean isVisibleGridXY(int gridX, int gridY) {
        return isVisibleGridX(gridX) && isVisibleGridY(gridY);
    }



    // ********** Update viewport **********

    /**
     * Add offset in pixels
     * @param offsetX x-offset in pixels
     * @param offsetY y-offset in pixels
     */
    public void addOffset(int offsetX, int offsetY) {
        addOffset((double)offsetX / tileSize, (double)offsetY / tileSize);
    }

    // add offset in fraction of tileSize
    private void addOffset(double offsetX, double offsetY) {
        this.offsetX += offsetX;
        this.offsetY += offsetY;
        normalizeOffset();
    }

    // normalizes offset to be in (-1, 0]
    private void normalizeOffset() {
        if (offsetX > 0 || offsetX <= -1) {
            int w = (int) offsetX;
            double f = offsetX % 1;
            if (f > 0) {
                w += 1;
                f -= 1;
            }
            setTopLeftToTileX(-w);
            offsetX = f;
        }
        if (offsetY > 0 || offsetY <= -1) {
            int w = (int) offsetY;
            double f = offsetY % 1;
            if (f > 0) {
                w += 1;
                f -= 1;
            }
            setTopLeftToTileY(-w);
            offsetY = f;
        }
    }


    private void setTopLeftToTileX(int tileX) {
        topLeftX += tileX;
    }

    private void setTopLeftToTileY(int tileY) {
        topLeftY -= tileY;
    }


    /**
     * Set the tile size in pixels, cannot be set lower than MIN_TILE_SIZE
     * @param pixels tile size in pixels
     */
    private void setTileSize(int pixels) {
        pixels = Math.max(MIN_TILE_SIZE, pixels);
        tileSize = pixels;
    }

    /**
     * Changes the tile size by the given amount. Cannot be set lowe than MIN_TILE_SIZE
     * @param pixels change in tile size in pixels
     */
    public void zoom(int pixels) {
        double oldWidthX = (double) width / tileSize;
        double oldWidthY = (double) height / tileSize;
        setTileSize(tileSize + pixels);
        double diffX = (double) width / tileSize - oldWidthX;
        double diffY = (double) height / tileSize - oldWidthY;
        addOffset(diffX / 2, diffY / 2);
    }


    /**
     * Set the width of the viewport canvas
     * @param width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * set the height of the viewport canvas
     * @param height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public void reset() {
        this.topLeftX = 0;
        this.topLeftY = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        setTileSize(DEFAULT_TILE_SIZE);
        addOffset(width / 2 - tileSize / 2, height / 2 - tileSize / 2);
        normalizeOffset();
    }

    public void setCanvasSize(int width, int height) {
        int diffWidth = width - this.width;
        int diffHeight = height - this.height;
        this.width = width;
        this.height = height;
        addOffset(diffWidth / 2, diffHeight / 2);

    }

    // ********** Conversion Functions **********

    public int tileToScreenX(int tileX) {
        return tileX * tileSize + offsetX();
    }

    public int tileToScreenY(int tileY) {
        return tileY * tileSize + offsetY();
    }

    public int screenToTileX(int screenX) {
        return (screenX - offsetX()) / tileSize;
    }

    public int screenToTileY(int screenY) {
        return (screenY - offsetY()) / tileSize;
    }


    private int gridToTileX(int gridX) {
        return gridX - topLeftX;
    }

    private int gridToTileY(int gridY) {
        return topLeftY - gridY;
    }

    public int tileToGridX(int tileX) {
        return topLeftX + tileX;
    }

    public int tileToGridY(int tileY) {
        return topLeftY - tileY;
    }


    public int gridToScreenX(int gridX) {
        return tileToScreenX(gridToTileX(gridX));
    }

    public int gridToScreenY(int gridY) {
        return tileToScreenY(gridToTileY(gridY));
    }

    public int screenToGridX(int screenX) {
        return tileToGridX(screenToTileX(screenX));
    }

    public int screenToGridY(int screenY) {
        return tileToGridY(screenToTileY(screenY));
    }
}
