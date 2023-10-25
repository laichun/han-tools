package han;

import com.todostudy.tools.service.SendWXEnterpriseService;
import com.todostudy.tools.web.req.WXENDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class WXSendMsg {
    @Test
    public void sendWX(){
        WXENDto dto = new WXENDto();
         String con="柏科企业-发送内容来了";
         List<String> list = new ArrayList<>();
         dto.setText(dto.buildWXText(con,list));
        SendWXEnterpriseService.builder().api("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=58647923-2bac-4f5c-aecd-a5b16f2feb47")
                .build().sendMsg(dto);
    }
}
