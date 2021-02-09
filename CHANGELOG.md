### 1.0.9 Release (29.01.2021 - 09.02.2021)
* Fixed `StaticPane#fillHorizontallyWith` andS `StaticPane#fillVerticallyWith` methods not working if there are no items registered.
* Fixed `ArrayIndexOutOfBounds` while using `StaticPane#fillBorder` method.
* ~~Now `StaticPane#fillHorizontallyWith` and `StaticPane#fillVerticallyWith` methods will fill the given area by skipping items if there are.~~

### 1.0.8 Release (10.01.2021 - 27.01.2021)
* Added new methods to fill a row line vertically with given item.
* Added `Fillable` interface that includes some filling methods.
* Added `StaticPane#fillBorder` method to fill GUI's border.
* Added `Gui#setOnDrag` as a tag to XML usage.
* Added coloring to gui titles.
* Changed some exception messages.

### 1.0.7 Release (17.11.2020)
* Added StaticPane#getLocations method to get list of gui item locations.

### 1.0.6 Release (16.11.2020)
* Added Gui#onDrag consumer to handle InventoryDragEvent.
* Fixed InventoryDragEvent issues in older versions of Minecraft.

### 1.0.5 Release (13.11.2020 - 14.11.2020)
* Added support for Minecraft 1.16.4
* Added new method to Static Pane.
  * StaticPane#fillHorizontallyWith method to fill horizontal line with given item stack.
* Changed a package name - Developer Alert
   * com.github.despical.inventoryframework -> me.despical.inventoryframework

### 1.0.3 / 1.0.4 Release (08.10.2020)
* Fixed item lores in 1.13 and higher versions.
* Fixed items being picked up.

### 1.0.1-SNAPSHOT / 1.0.2 Release (04.09.2020)
* Fixed since tags.
* Fixed getting null pointer from some NMS items.
* Added support to 1.8 - 1.16.2.

### 1.0.0 Release (04.09.2020)
* Update pom.xml