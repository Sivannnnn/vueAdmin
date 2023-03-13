package com.sivan.controller;


import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.sivan.common.lang.Const;
import com.sivan.common.lang.Result;
import com.sivan.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

// @RestController的作用等同于@Controller + @ResponseBody
// @Controller 将当前修饰的类注入SpringBoot IOC容器，使得从该类所在的项目跑起来的过程中，这个类就被实例化。
// @ResponseBody 它的作用简短截说就是指该类中所有的API接口返回的数据，甭管你对应的方法返回Map或是其他Object，
//               它会以Json字符串的形式返回给客户端
@RestController
public class AuthController extends BaseController{
    // 注入SysUserService类
    @Autowired
    Producer producer;
    // 引入Google的Producer类便于生成图片验证码

    // @GetMapping请求方式 --- 前端访问图片验证码
    @GetMapping("/captcha")
    // 使用Result类规则，抛出IO流的异常
    public Result captcha() throws IOException {
        // 使用UUID生成key——32位的随机数
        String key = UUID.randomUUID().toString();
        // 使用Google的producer生成验证码
        String code = producer.createText();

        // 为了测试
        // key = "12345";
        // code = "12345";

        // 生成图片
        BufferedImage image = producer.createImage(code);
        // 输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 图片写进流，写成jpg格式
        ImageIO.write(image, "jpg", outputStream);

        // 图片转成格式为base64编码
        Base64Encoder encoder = new Base64Encoder();
        // 固定base64编码的头部
        String str = "data:image/jepg;base64,";
        // 完整生成base64编码
        String base64Img = str + encoder.encode(outputStream.toByteArray());

        // 生成完成后，存入Redis
        // 存入hset 第一个参数为：常量引入
        redisUtil.hset(Const.CAPTCHA_KEY,key,code,120);

        // 往前端返回验证码图片数据
        return Result.succ(
                // 存入MapUtil里
                MapUtil.builder()
                            .put("token",key)
                            .put("captchaImg",base64Img)
                            .build()
        );
    }

    /**
     * 获取用户信息接口
     */
    @GetMapping("/sys/userInfo")
    public Result userInfo(Principal principal){
        // 获取用户信息
        SysUser sysUser = sysUserService.getByUsername((principal.getName()));
        // 返回用户信息
        return Result.succ(MapUtil.builder()
                .put("id",sysUser.getId())
                .put("username",sysUser.getUsername())
                .put("avatar",sysUser.getAvatar())
                .put("created",sysUser.getCreated())
                // 转成map形式
                .map()
        );
    }
}
