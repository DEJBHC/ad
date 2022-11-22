package org.jeecg.config.thirdapp;

import lombok.Data;

/**
 * 第三方App对接
 * @author: jeecg-boot
 */
@Data
public class ThirdAppTypeItemVo {

    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 应用Key
     */
    private String clientId;
    /**
     * 应用Secret
     */
    private String clientSecret;
    /**
     * 应用ID
     */
    private String agentId;
    /**
     * 目前仅企业微信用到：自建应用Secret
     * 20221021 cfm： 钉钉企业内部应用事件订阅加密 aes_key，微信公众平台服务器配置 EncodingAESKey
     */
    private String agentAppSecret;
    /**
     * token
     * 20221021 cfm add: 钉钉企业内部应用事件订阅签名 token， 微信公众平台服务器配置 Token
     */
    private String token;

    public int getAgentIdInt() {
        return Integer.parseInt(agentId);
    }

}
