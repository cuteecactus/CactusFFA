import me.cactusffa.util.ItemSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestSerializer {
  public static void main(String[] args) {
    ItemStack[] items = new ItemStack[41];
    items[0] = new ItemStack(Material.DIAMOND_SWORD, 1);
    items[5] = new ItemStack(Material.COOKED_BEEF, 16);
    String encoded = ItemSerializer.encodeItems(items);
    ItemStack[] decoded = ItemSerializer.decodeItems(encoded);
    System.out.println("encoded len=" + encoded.length());
    System.out.println("decoded size=" + decoded.length);
    for (int i = 0; i < decoded.length; i++) {
      System.out.println(i + ": " + decoded[i]);
    }
  }
}
