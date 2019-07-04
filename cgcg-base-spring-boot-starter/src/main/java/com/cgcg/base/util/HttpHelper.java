package com.cgcg.base.util;

import com.cgcg.base.core.enums.CharsetCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Http Body Utils
 *
 * @auth zhicong.lin
 * @date 2019/6/27
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpHelper {

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public static String getStringBody(ServletRequest request) {
        final StringBuilder sb = new StringBuilder();
        try (final InputStream is = request.getInputStream();
             final InputStreamReader in = new InputStreamReader(is, CharsetCode.forUtf8());
             final BufferedReader reader = new BufferedReader(in)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return sb.toString();
    }

    /**
     * 获取请求Body
     *
     * @param is
     * @return
     */
    public static String getStringBody(InputStream is) {
        final StringBuilder sb = new StringBuilder();
        try (final InputStreamReader in = new InputStreamReader(is, CharsetCode.forUtf8());
             final BufferedReader reader = new BufferedReader(in)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return sb.toString();
    }

}
