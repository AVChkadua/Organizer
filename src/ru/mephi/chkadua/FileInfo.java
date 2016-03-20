package ru.mephi.chkadua;

/**
 * Контейнер, хранящий информацию о файле (название, категорию и путь к файлу)
 * @author Anton_Chkadua
 */
public class FileInfo {

    private String name;
    private String category;
    private String path;

    /**
     * Конструктор объекта с информацией о файле с заданными параметрами
     * @param name Имя файла
     * @param category Категория файла
     * @param path Путь к файлу
     */
    public FileInfo(String name, String category, String path) {
        this.name = name;
        this.category = category;
        this.path = path;
    }

    /**
     * Конструктор по умолчанию
     */
    public FileInfo() {
        new FileInfo(null,null,null);
    }

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
