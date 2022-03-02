package com.cgcg.mybatis.core;

import org.apache.commons.codec.binary.Hex;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.*;

/**
 * Blob/String转换器
 * @author zhicong.lin
 * @since 2022/3/1 15:00
 */
public class BlobToStringTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
    throws SQLException {
        ByteArrayInputStream bis;
        try {
            bis = new ByteArrayInputStream(parameter.getBytes(Hex.DEFAULT_CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Blob Encoding Error!");
        }
        ps.setBinaryStream(i, bis, parameter.length());
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        return blob2String(blob);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        return blob2String(blob);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        return blob2String(blob);
    }

    private String blob2String(Blob blob) throws SQLException {
        byte[] returnValue = null;
        if (null != blob) {
            returnValue = blob.getBytes(1, (int) blob.length());
        }
        try {
            assert returnValue != null;
            return new String(returnValue, Hex.DEFAULT_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Blob Encoding Error!");
        }
    }

}
