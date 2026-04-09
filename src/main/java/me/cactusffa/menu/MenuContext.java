package me.cactusffa.menu;

public record MenuContext(MenuType type, String id) {

    public static MenuContext root() {
        return new MenuContext(MenuType.ROOT, "");
    }

    public static MenuContext category(String id) {
        return new MenuContext(MenuType.CATEGORY, id);
    }

    public static MenuContext admin() {
        return new MenuContext(MenuType.ADMIN, "");
    }
}
