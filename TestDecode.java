import me.cactusffa.util.ItemSerializer;
public class TestDecode {
  public static void main(String[] args) {
    String s = "rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFwdAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFibGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAkwABGtleXN0ABJMamF2YS9sYW5nL09iamVjdDtMAAZ2YWx1ZXNxAH4ABHhwdXIAE1tMamF2YS5sYW5nLk9iamVjdDuQzlifEHMpbAIAAHhwAAAABXQAAj09dAALRGF0YVZlcnNpb250AAJpZHQABWNvdW50dAAOc2NoZW1hX3ZlcnNpb251cQB+AAYAAAAFdAAeb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrc3IAEWphdmEubGFuZy5JbnRlZ2VyEuKgpPeBhzgCAAFJAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAAAEj90ABBtaW5lY3JhZnQ6c2hpZWxkc3EAfgAPAAAAAXEAfgAT";
    var items = ItemSerializer.decodeItems(s);
    System.out.println("len=" + items.length);
    for (int i = 0; i < items.length; i++) System.out.println(i + ": " + items[i]);
  }
}
