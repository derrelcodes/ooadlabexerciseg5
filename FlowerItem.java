// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/FlowerItem.java
public class FlowerItem extends CreationItem {
    public FlowerItem(String imagePath, int x, int y) {
        super(imagePath, x, y);
    }

    @Override
    public boolean canFlip() {
        return true; // Flowers can be flipped
    }

    @Override
    public boolean canScale() {
        return true; // Flowers can be scaled
    }

    @Override
    public boolean canTranspose() {
        return true; // Flowers can be moved
    }
}