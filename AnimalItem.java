// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/AnimalItem.java
public class AnimalItem extends CreationItem {
    public AnimalItem(String imagePath, int x, int y) {
        super(imagePath, x, y);
    }

    @Override
    public boolean canFlip() {
        return true; // Animals can be flipped
    }

    @Override
    public boolean canScale() {
        return true; // Animals can be scaled
    }

    @Override
    public boolean canTranspose() {
        return true; // Animals can be moved
    }
}