package tools.fm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author handson
 * "queryDtoList": [
 *                {
 * 			"condition": "in",
 * 			"key": "id",
 * 			"value": [1,2,3]
 *        },
 * {
 * 			"condition": "le",
 * 			"key": "id",
 * 			"value": "4"
 *        }
 * 	],
 */
@Data
@ApiModel(description = "QueryDto")
public class QueryDto {

    /**
     * mybatis映射数据库字段
     */
    @ApiModelProperty("查询字段的属性名称：比如 userName")
    private String key;

    @ApiModelProperty("查询字段的属性值")
    private Object value;

    @ApiModelProperty("固定值如下：eq," +
            "    like,模糊查询 " +
            "    ge,就是 greater than or equal 大于等于" +
            "    le,小于等于" +
            "    gt, 大于" +
            "    lt, 小于" +
            "    in 包含（数组）")
    private String condition;

}
