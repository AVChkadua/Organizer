package ru.mephi.chkadua;

/**
 * Контейнер, хранящий информацию о файле (название, категорию и путь к файлу)
 * @author Anton_Chkadua
 */
public class FileInfoContainer {

    private String name;
    private String category;
    private String path;

    /**
     * Получает название файла
     * @return Название файла
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя файла
     * @param name Новое имя файла
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получает категорию, к которой относится файл
     * @return Категория файла
     */
    public String getCategory() {
        return category;
    }

    /**
     * Устанавливает категорию файла
     * @param category Новая категория файла
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Получает путь к файлу
     * @return Путь к файлу
     */
    public String getPath() {
        return path;
    }

    /**
     * Устанавливает путь к файлу
     * @param path Новый путь к файлу
     */
    public void setPath(String path) {
        this.path = path;
    }
}
