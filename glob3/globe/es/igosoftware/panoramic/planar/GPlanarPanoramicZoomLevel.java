

package es.igosoftware.panoramic.planar;

import com.google.gson.annotations.SerializedName;


class GPlanarPanoramicZoomLevel {

   public static final int TILE_WIDTH  = 256;
   public static final int TILE_HEIGHT = 256;


   @SerializedName("level")
   private int             _level;

   @SerializedName("width")
   private int             _width;

   @SerializedName("height")
   private int             _height;

   @SerializedName("widthInTiles")
   private int             _widthInTiles;

   @SerializedName("heightInTiles")
   private int             _heightInTiles;


   GPlanarPanoramicZoomLevel() {
      // empty constructor for GSON deserialization
   }


   GPlanarPanoramicZoomLevel(final int level,
                             final int width,
                             final int height,
                             final int tileWidth,
                             final int tileHeight) {
      _level = level;

      _width = width;
      _height = height;

      int widthInTiles = _width / tileWidth;
      if ((widthInTiles * tileWidth) < _width) {
         widthInTiles++;
      }
      _widthInTiles = widthInTiles;

      int heightInTiles = _height / tileHeight;
      if ((heightInTiles * tileHeight) < _height) {
         heightInTiles++;
      }
      _heightInTiles = heightInTiles;
   }


   public int getLevel() {
      return _level;
   }


   public int getWidth() {
      return _width;
   }


   public int getHeight() {
      return _height;
   }


   public int getWidthInTiles() {
      return _widthInTiles;
   }


   public int getHeightInTiles() {
      return _heightInTiles;
   }


   @Override
   public String toString() {
      return "Level=" + _level + ", Pixels=" + _width + "x" + _height + ", " + ((float) (_width * _height) / 1024 / 1024)
             + "Mpx, Tiles=" + _widthInTiles + "x" + _heightInTiles;
   }

}
