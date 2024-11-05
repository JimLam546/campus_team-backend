package com.jim.Campus_Team.config;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author Jim_Lam
 * @date 2024-11-05 11:10
 * @description HttpSessionConfig
 */
@Component
public class HttpSessionConfig extends ServerEndpointConfig.Configurator {

    /**
     * 修改握手
     * @param sec
     * @param request
     * @param response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
        super.modifyHandshake(sec, request, response);
    }
}