package ru.mephi.chkadua;

/**
 * Контейнер, хранящий информацию о материале (название, категорию и путь к файлу)
 * @author Anton_Chkadua
 */
public class FileInfoContainer {

    private String name;
    private String category;
    private String path;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
