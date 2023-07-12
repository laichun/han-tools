/*
package com.todostudy.tools.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.boke.config.BK;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

*/
/**
 * @author hanson
 *//*

@MappedTypes({JsonNode.class})
public class JsonbTypeHandler extends BaseTypeHandler {

    @Override // 用于insert时的映射
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            PGobject ext = new PGobject();
            ext.setType(BK.jsonb);
            ext.setValue(parameter.toString());
            ps.setObject(i, ext);
        }
    }

    @Override   // 用于select时的映射
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        PGobject pGobject= (PGobject)resultSet.getObject(s);
       // return JacksonUtil.valueToTree(pGobject.getValue());
        return JacksonUtil.toJsonNode(pGobject.getValue());
    }

    @Override   // 用于select时的映射
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        PGobject pGobject= (PGobject) resultSet.getObject(i);
        return JacksonUtil.toJsonNode(pGobject.getValue());
        //return JacksonUtil.valueToTree(object);
    }

    @Override  // 用于select时的映射
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        PGobject pGobject= (PGobject) callableStatement.getObject(i);
        return JacksonUtil.toJsonNode(pGobject.getValue());
        //return JacksonUtil.valueToTree(object);
    }
}
*/
