/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.helper;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author neetocode
 */
public class CookieHelper {

  public void setCookie(String name, String value, int expiry) {

    FacesContext facesContext = FacesContext.getCurrentInstance();

    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
    Cookie cookie = null;

    Cookie[] userCookies = request.getCookies();
    if (userCookies != null && userCookies.length > 0 ) {
        for (Cookie userCookie : userCookies) {
            if (userCookie.getName().equals(name)) {
                cookie = userCookie;
                break;
            }
        }
    }
    
    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    
    
    if (cookie != null) {
        cookie.setValue(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    Cookie newCookie = new Cookie(name, value);
    newCookie.setPath(request.getContextPath());
    newCookie.setMaxAge(expiry);

    response.addCookie(newCookie);
    
    
  }

  public Cookie getCookie(String name) {

    FacesContext facesContext = FacesContext.getCurrentInstance();

    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
    Cookie cookie;

    Cookie[] userCookies = request.getCookies();
    if (userCookies != null && userCookies.length > 0 ) {
        for (Cookie userCookie : userCookies) {
            if (userCookie.getName().equals(name)) {
                cookie = userCookie;
                return cookie;
            }
        }
    }
    return null;
  }
}