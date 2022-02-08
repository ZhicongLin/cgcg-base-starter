package com.cgcg.mongo.file;

/**
 * @author zhicong.lin
 */
public enum FileMime {
    /*
     * jpg
     */
    JPG("jpg", "image/jpeg"), /*
     * gif
     */
    GIF("gif", "image/gif"), /*
     * bmp
     */
    BMP("bmp", "image/bmp"), /*
     * png
     */
    PNG("png", "image/png");
    private final String name;
    private final String mime;

    /**
     * 构造器
     *
     * @param name
     * @param mime
     * @return null
     * @author zhicong.lin
     * @date 2022/2/8 9:28
     */
    FileMime(String name, String mime) {
        this.name = name;
        this.mime = mime;
    }


    /**
     * 获取媒体类型
     *
     * @param name
     * @return java.lang.String
     * @author zhicong.lin
     * @date 2022/2/8 9:21
     */
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
