// derrelcodes/ooadlabexerciseg5/ooadlabexerciseg5-main/CreationFactory.java
public class CreationFactory {
    public static CreationItem createItem(String type, String imagePath, int x, int y) {
        if (type == null) { // Default or fallback
            return new CustomImageItem(imagePath, x, y);
        }
        switch (type) {
            case "Animal":
                return new AnimalItem(imagePath, x, y);
            case "Flower":
                return new FlowerItem(imagePath, x, y);
            case "Image":
            default:
                return new CustomImageItem(imagePath, x, y);
        }
    }
}