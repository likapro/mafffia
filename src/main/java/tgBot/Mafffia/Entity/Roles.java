package tgBot.Mafffia.Entity;

public enum Roles {
    MAFIA("мафия"),
    DOCTOR("доктор"),
    PERSON("мирный житель");

    private String name;
    Roles(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
