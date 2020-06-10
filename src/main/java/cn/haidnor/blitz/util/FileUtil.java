package cn.haidnor.blitz.util;

/**
 * 处理文件名相关的工具
 *
 * @author Haidnor
 */
public class FileUtil {

    /**
     * 在数字前补 0.
     * 例如调用 supplementZero(5, 123) 将返回 00123
     *
     * @param length 需要的总长度
     * @param num    编号
     * @return 用0补全后的字符串
     */
    public static String supplementZero(int length, int num) {
        StringBuffer sb = new StringBuffer(length);
        String strNum = num + "";
        sb.append(strNum);
        int zeroNum = length - strNum.length();
        for (int i = 0; i < zeroNum; i++) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

}
