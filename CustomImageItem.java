// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/CustomImageItem.java
public class CustomImageItem extends CreationItem {
    public CustomImageItem(String imagePath, int x, int y) {
        super(imagePath, x, y);
    }

    @Override
    public boolean canFlip() {
        return false; // Custom images cannot be flipped
    }

    @Override
    public boolean canScale() {
        return false; // Custom images cannot be scaled
    }

    @Override
    public boolean canTranspose() {
        return true; // Custom images can be transposed (moved)
    }
}