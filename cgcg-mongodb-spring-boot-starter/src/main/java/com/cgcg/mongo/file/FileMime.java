package com.cgcg.mongo.file;

/**
 * @author zhicong.lin
 */
public enum FileMime {
    /*
     * jpg
     */
    JPG("jpg", "image/jpeg"),
    /*
     * gif
     */
    GIF("gif", "image/gif"),
    /*
     * bmp
     */
    BMP("bmp", "image/bmp"),
    /*
     * png
     */
    PNG("png", "image/png");
    private final String name;
    private final String mime;

    FileMime(String name, String mime) {
        this.name = name;
        this.mime = mime;
    }

    public static String get(String name) {
        FileMime[] values = FileMime.values();
        for (FileMime value : values) {
            if (value.name.equalsIgnoreCase(name)) {
                return value.mime;
            }
        }
        return name;
    }
}
