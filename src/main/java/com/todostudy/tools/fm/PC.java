package com.todostudy.tools.fm;

import com.baomidou.mybatisplus.core.toolkit.AES;

public interface PC {

    int t0 = 0;
    int t1 = 1;
    int t2 = 2;
    int t3 = 3;
    int t4 = 4;
    int t5 = 5;
    int t245 = 256;
    int t1024 = 1024;

    String f_ = "-";
    String fix = "_";
    String f_eq = "=";
    String f_url = "&";
    String f1 = ":";
    String MSM = "_msm";
    String REQ = "req";
    String DES="DES";
    String AES="AES";
    String DES_CIPHER="DES/CBC/PKCS5Padding";
    String JSONB = "jsonb";
    String JSON = "JSON";
    String ORGAN_ID = "organizationId";
    String ORG_ID = "orgId";
    String TENANT_ID = "tenantId";


    String AUTH_ERROR = "用户无权限操作";
    String DATA_NULL = "数据不存在";
    String REPEAT_ERROR = "重复处理数据/数据已处理";
    String DATA_ERROR = "参数错误";

    String SUCCESS = "success";
    int OK_CODE = 200;
    int ERROR_CODE = -1;

    String UTF8 = "utf-8";
    String GBK = "gbk";
    String DATA_FORM_1 = "yyyy-MM-dd";
    String DATA_FORM_2 = "yyyyMMddHHmmss";
    String GMT = "GMT+8:00";

    String PAGE_SIZE = "pageSize";
    String PAGE = "page";
}
