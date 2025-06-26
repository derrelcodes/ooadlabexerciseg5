// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/CustomImageItem.java
public class CustomImageItem extends CreationItem {
    private boolean isCompositionLayer = false;

    public CustomImageItem(String imagePath, int x, int y) {
        super(imagePath, x, y);
    }

    @Override
    public boolean canFlip() {
        return false;
    }

    @Override
    public boolean canScale() {
        return false;
    }

    @Override
    public boolean canTranspose() {
        return true;
    }

    // Method to mark this instance as the special composed layer
    public void setAsCompositionLayer(boolean isComposition) {
        this.isCompositionLayer = isComposition;
    }

    // Override the base method to indicate if this is a locked background
    @Override
    public boolean isBackgroundLayer() {
        return this.isCompositionLayer;
    }
}